package net.adambruce.dsn.now.client;

import net.adambruce.dsn.now.model.config.Configuration;
import net.adambruce.dsn.now.model.config.Site;
import net.adambruce.dsn.now.model.config.Spacecraft;
import net.adambruce.dsn.now.model.merged.MergedData;
import net.adambruce.dsn.now.model.merged.MergedDishData;
import net.adambruce.dsn.now.model.merged.MergedStationData;
import net.adambruce.dsn.now.model.merged.MergedTargetData;
import net.adambruce.dsn.now.model.state.Dish;
import net.adambruce.dsn.now.model.state.Signal;
import net.adambruce.dsn.now.model.state.State;
import net.adambruce.dsn.now.model.state.Station;
import net.adambruce.dsn.now.model.state.Target;
import org.apache.commons.io.IOUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.BasicHttpEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeepSpaceNetworkClientTest {

    @Mock
    private CloseableHttpClient configClient;

    @Mock
    private CloseableHttpClient dsnClient;

    @Mock
    private ClassicHttpResponse httpResponse;

    private final DeepSpaceNetworkClient client = DeepSpaceNetworkClient.newDeepSpaceNetworkClient();

    @Test
    void shouldCreateNewClientWithDefaultMaxConfigAge() {
        DeepSpaceNetworkClient.newDeepSpaceNetworkClient();
    }

    @Test
    void shouldCreateNewClientWithMaxConfigAge() {
        DeepSpaceNetworkClient.newDeepSpaceNetworkClient(Duration.ofMinutes(1));
    }

    @Test
    void shouldFetchConfiguration() throws Exception {
        try (MockedStatic<HttpClients> staticHttpClient = mockStatic(HttpClients.class)) {
            staticHttpClient.when(HttpClients::createDefault).thenReturn(configClient);
            byte[] response = getBytes("config/config.xml");
            when(configClient.execute(any(HttpGet.class), any(HttpClientResponseHandler.class))).thenReturn(response);

            Configuration configuration = client.fetchConfiguration();

            ArgumentCaptor<HttpClientResponseHandler> handlerCaptor = ArgumentCaptor.forClass(HttpClientResponseHandler.class);
            verify(configClient, times(1)).execute(any(HttpGet.class), handlerCaptor.capture());

            // Verify our response handler is working as expected
            when(httpResponse.getEntity()).thenReturn(new BasicHttpEntity(new ByteArrayInputStream(response), response.length, ContentType.APPLICATION_XML));
            byte[] responseBytes = (byte[])handlerCaptor.getValue().handleResponse(httpResponse);
            assertArrayEquals(response, responseBytes);

            assertSite(configuration.getSites().get(0), "mdscc", "Madrid", -4.2480085, 40.2413554);
            assertSite(configuration.getSites().get(1), "gdscc", "Goldstone", -116.8895382, 35.2443523);
            assertSite(configuration.getSites().get(2), "cdscc", "Canberra", 148.9812673, -35.2209189);

            assertSpacecraft(configuration.getSpacecraft().get(0), "ace", "sc_ace", null, "Advanced Composition Explorer", true);
            assertSpacecraft(configuration.getSpacecraft().get(1), "apm1", "", null, "Astrobotic Peregrine 1 Mission", true);
            assertSpacecraft(configuration.getSpacecraft().get(2), "agm1", "", null, "Astrobotic Griffin Lander", true);

            assertSpacecraft(configuration.getSpacecraft().get(29), "em1", "", "art1", "Human Space Flight: Artemis I", true);
        }
    }

    @Test
    void shouldFetchDsnData() throws Exception {
        try (MockedStatic<HttpClients> staticHttpClient = mockStatic(HttpClients.class)) {
            staticHttpClient.when(HttpClients::createDefault).thenReturn(dsnClient);
            byte[] response = getBytes("dsn/dsn.xml");
            when(dsnClient.execute(any(HttpGet.class), any(HttpClientResponseHandler.class))).thenReturn(response);

            State state = client.fetchState();

            ArgumentCaptor<HttpClientResponseHandler> handlerCaptor = ArgumentCaptor.forClass(HttpClientResponseHandler.class);
            verify(dsnClient, times(1)).execute(any(HttpGet.class), handlerCaptor.capture());

            // Verify our response handler is working as expected
            when(httpResponse.getEntity()).thenReturn(new BasicHttpEntity(new ByteArrayInputStream(response), response.length, ContentType.APPLICATION_XML));
            byte[] responseBytes = (byte[])handlerCaptor.getValue().handleResponse(httpResponse);
            assertArrayEquals(response, responseBytes);

            assertStation(state.getStations().get(0), "gdscc", "Goldstone", Instant.ofEpochMilli(1770497799000L), ZoneOffset.ofTotalSeconds(-28800));

            assertDish(state.getDishes().get(0), "DSS14", 0, 90, null, false, false, false, "Antenna Unplanned Maintenance");
            assertTarget(state.getDishes().get(0).getTargets().get(0), "DSS", 99, -1, -1, -1);

            assertDish(state.getDishes().get(1), "DSS25", 251, 13, 8L, false, false, false, "DSN Very Long Baseline Interferometry");
            assertTarget(state.getDishes().get(1).getTargets().get(0), "DSN", 99, -1, -1, -1);

            assertDish(state.getDishes().get(2), "DSS24", 179, 18, 8L, false, false, false, "Spacecraft Telemetry, Tracking, and Command");
            assertSignal(state.getDishes().get(2).getUpSignals().get(0), true, "data", 0L, 0L, "S", 0.2, "MMS2", -109L);
            assertSignal(state.getDishes().get(2).getDownSignals().get(0), true, "data", 1250000L, 0L, "S", -110D, "MMS2", -109L);
            assertTarget(state.getDishes().get(2).getTargets().get(0), "MMS2", 109, 130000, 130000, 0.87);

            assertDish(state.getDishes().get(3), "DSS26", 180, 90, 8L, false, false, false, "Spacecraft Telemetry, Tracking, and Command");
            assertSignal(state.getDishes().get(3).getUpSignals().get(0), false, "none", 0L, 0L, "X", 0D, "SOHO", -21L);
            assertSignal(state.getDishes().get(3).getDownSignals().get(0), false, "none", 0L, 0L, "S", -480D, "SOHO", -21L);
            assertTarget(state.getDishes().get(3).getTargets().get(0), "SOHO", 21, 1640000, 1640000, 10.9);
        }
    }

    @Test
    void shouldMergeConfigAndDsnData() throws Exception {
        try (MockedStatic<HttpClients> staticHttpClient = mockStatic(HttpClients.class)) {
            staticHttpClient.when(HttpClients::createDefault)
                    .thenReturn(configClient)
                    .thenReturn(dsnClient);

            byte[] configResponse = getBytes("config/config.xml");
            when(configClient.execute(any(HttpGet.class), any(HttpClientResponseHandler.class))).thenReturn(configResponse);

            byte[] dsnResponse = getBytes("dsn/dsn.xml");
            when(dsnClient.execute(any(HttpGet.class), any(HttpClientResponseHandler.class))).thenReturn(dsnResponse);

            MergedData mergedData = client.fetchMergedData();

            verify(configClient, times(1)).execute(any(HttpGet.class), any(HttpClientResponseHandler.class));
            verify(dsnClient, times(1)).execute(any(HttpGet.class), any(HttpClientResponseHandler.class));

            assertMergedStation(
                    mergedData.getStations().get(0),
                    "mdscc", "Madrid", -4.2480085, 40.2413554, Instant.ofEpochMilli(1770497799000L),
                    ZoneOffset.ofTotalSeconds(3600)
            );

            assertMergedDish(mergedData.getStations().get(0).getDishes().get(0),
                    "DSS63", "DSS 63", "70M",  131L, 65L, 2L, false, false, false,
                    "Spacecraft Telemetry, Tracking, and Command"
            );
            assertMergedTarget(mergedData.getStations().get(0).getDishes().get(0).getTarget().get(0),
                    "JNO", 61L, 651000000L, 651000000L, 4350.0, "sc_juno", null, "Juno", true);

            assertMergedDish(mergedData.getStations().get(0).getDishes().get(1),
                    "DSS65", "DSS 65", "34MHEF",  null, null, null, null, null, null, null
            );

            assertMergedDish(mergedData.getStations().get(0).getDishes().get(2),
                    "DSS53", "DSS 53", "34M",  0L, 90L, null, false, false, false,
                    "Engineering Upgrades"
            );

            assertEquals(Instant.ofEpochMilli(1770497799000L), mergedData.getTimestamp());
        }
    }

    @Test
    void shouldMergeConfigAndDsnDataWithMissingStationData() throws Exception {
        try (MockedStatic<HttpClients> staticHttpClient = mockStatic(HttpClients.class)) {
            staticHttpClient.when(HttpClients::createDefault)
                    .thenReturn(configClient)
                    .thenReturn(dsnClient);

            byte[] configResponse = getBytes("config/config.xml");
            when(configClient.execute(any(HttpGet.class), any(HttpClientResponseHandler.class))).thenReturn(configResponse);

            byte[] dsnResponse = getBytes("dsn/dsnWithoutStations.xml");
            when(dsnClient.execute(any(HttpGet.class), any(HttpClientResponseHandler.class))).thenReturn(dsnResponse);

            MergedData mergedData = client.fetchMergedData();

            verify(configClient, times(1)).execute(any(HttpGet.class), any(HttpClientResponseHandler.class));
            verify(dsnClient, times(1)).execute(any(HttpGet.class), any(HttpClientResponseHandler.class));

            assertMergedStation(
                    mergedData.getStations().get(0),
                    "mdscc", "Madrid", -4.2480085, 40.2413554, null, null
            );

            assertMergedDish(mergedData.getStations().get(0).getDishes().get(0),
                    "DSS63", "DSS 63", "70M",  131L, 65L, 2L, false, false, false,
                    "Spacecraft Telemetry, Tracking, and Command"
            );

            assertMergedDish(mergedData.getStations().get(0).getDishes().get(1),
                    "DSS65", "DSS 65", "34MHEF",  null, null, null, null, null, null, null
            );

            assertMergedDish(mergedData.getStations().get(0).getDishes().get(2),
                    "DSS53", "DSS 53", "34M",  0L, 90L, null, false, false, false,
                    "Engineering Upgrades"
            );

            assertEquals(Instant.ofEpochMilli(1770497799000L), mergedData.getTimestamp());
        }
    }

    @Test
    void shouldNotFetchConfigurationIfAlreadyPopulatedWhenFetchingMergedData() throws Exception {
        try (MockedStatic<HttpClients> staticHttpClient = mockStatic(HttpClients.class)) {
            staticHttpClient.when(HttpClients::createDefault)
                    .thenReturn(configClient)
                    .thenReturn(dsnClient);

            byte[] configResponse = getBytes("config/config.xml");
            when(configClient.execute(any(HttpGet.class), any(HttpClientResponseHandler.class))).thenReturn(configResponse);

            byte[] dsnResponse = getBytes("dsn/dsn.xml");
            when(dsnClient.execute(any(HttpGet.class), any(HttpClientResponseHandler.class))).thenReturn(dsnResponse);

            client.fetchConfiguration();
            MergedData mergedData = client.fetchMergedData();

            verify(configClient, times(1)).execute(any(HttpGet.class), any(HttpClientResponseHandler.class));
            verify(dsnClient, times(1)).execute(any(HttpGet.class), any(HttpClientResponseHandler.class));

            assertMergedStation(
                    mergedData.getStations().get(0),
                    "mdscc", "Madrid", -4.2480085, 40.2413554, Instant.ofEpochMilli(1770497799000L),
                    ZoneOffset.ofTotalSeconds(3600)
            );

            assertMergedDish(mergedData.getStations().get(0).getDishes().get(0),
                    "DSS63", "DSS 63", "70M",  131L, 65L, 2L, false, false, false,
                    "Spacecraft Telemetry, Tracking, and Command"
            );

            assertMergedDish(mergedData.getStations().get(0).getDishes().get(1),
                    "DSS65", "DSS 65", "34MHEF",  null, null, null, null, null, null, null
            );

            assertMergedDish(mergedData.getStations().get(0).getDishes().get(2),
                    "DSS53", "DSS 53", "34M",  0L, 90L, null, false, false, false,
                    "Engineering Upgrades"
            );

            assertEquals(Instant.ofEpochMilli(1770497799000L), mergedData.getTimestamp());
        }
    }

    @Test
    void shouldFetchNewConfigWhenExistingConfigHasExpired() throws Exception {
        try (MockedStatic<HttpClients> staticHttpClient = mockStatic(HttpClients.class);
             MockedStatic<Instant> staticInstant = mockStatic(Instant.class)) {
            staticHttpClient.when(HttpClients::createDefault)
                    .thenReturn(configClient)
                    .thenReturn(dsnClient)
                    .thenReturn(configClient)
                    .thenReturn(dsnClient);

            byte[] configResponse = getBytes("config/config.xml");
            byte[] configResponse2 = getBytes("config/configWithOneSite.xml");
            when(configClient.execute(any(HttpGet.class), any(HttpClientResponseHandler.class)))
                    .thenReturn(configResponse)
                    .thenReturn(configResponse2);

            byte[] dsnResponse = getBytes("dsn/dsn.xml");
            when(dsnClient.execute(any(HttpGet.class), any(HttpClientResponseHandler.class))).thenReturn(dsnResponse);

            Instant firstAssignment = mock(Instant.class);

            Instant firstAssignmentPlusExpiry = mock(Instant.class);
            when(firstAssignment.plus(any(Duration.class))).thenReturn(firstAssignmentPlusExpiry);

            Instant firstComparison = mock(Instant.class);
            when(firstComparison.isAfter(firstAssignmentPlusExpiry)).thenReturn(false);

            Instant secondComparison = mock(Instant.class);
            when(secondComparison.isAfter(firstAssignmentPlusExpiry)).thenReturn(true);

            Instant secondAssignment = mock(Instant.class);

            staticInstant.when(Instant::now)
                    .thenReturn(firstAssignment) // Assignment in fetchConfiguration
                    .thenReturn(firstComparison) // Check for expiry (OK)
                    .thenReturn(secondComparison) // Check for expiry (Expired)
                    .thenReturn(secondAssignment); // Assignment in fetchConfiguration

            MergedData mergedData1 = client.fetchMergedData();
            MergedData mergedData2 = client.fetchMergedData();

            verify(configClient, times(2)).execute(any(HttpGet.class), any(HttpClientResponseHandler.class));
            verify(dsnClient, times(2)).execute(any(HttpGet.class), any(HttpClientResponseHandler.class));

            assertEquals(3, mergedData1.getStations().size());
            assertEquals(1, mergedData2.getStations().size());
        }
    }

    @Test
    void shouldHandleDsnError() throws Exception {
        try (MockedStatic<HttpClients> staticHttpClient = mockStatic(HttpClients.class)) {
            staticHttpClient.when(HttpClients::createDefault).thenReturn(dsnClient);
            when(dsnClient.execute(any(HttpGet.class), any(HttpClientResponseHandler.class))).thenThrow(new IOException());

            assertThrows(Exception.class, client::fetchState);
        }
    }

    @Test
    void shouldHandleConfigError() throws Exception {
        try (MockedStatic<HttpClients> staticHttpClient = mockStatic(HttpClients.class)) {
            staticHttpClient.when(HttpClients::createDefault).thenReturn(configClient);
            when(configClient.execute(any(HttpGet.class), any(HttpClientResponseHandler.class))).thenThrow(new IOException());

            assertThrows(Exception.class, client::fetchConfiguration);
        }
    }

    private static byte[] getBytes(String filePath) {
        try {
            InputStream stream = DeepSpaceNetworkClientTest.class.getClassLoader().getResourceAsStream(filePath);
            return IOUtils.toByteArray(stream);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static void assertStation(Station station, String name, String friendlyName, Instant time,
                                      ZoneOffset timeZone) {
        assertEquals(name, station.getName());
        assertEquals(friendlyName, station.getFriendlyName());
        assertEquals(time, station.getTime());
        assertEquals(timeZone, station.getTimeZoneOffset());
    }

    private static void assertSite(Site site, String name, String friendlyName, Double longitude, Double latitude) {
        assertEquals(name, site.getName());
        assertEquals(friendlyName, site.getFriendlyName());
        assertEquals(longitude, site.getLongitude());
        assertEquals(latitude, site.getLatitude());
    }

    private static void assertSpacecraft(Spacecraft craft, String name, String explorerName, String friendlyAcronym,
                                         String friendlyName, Boolean thumbnail) {
        assertEquals(name, craft.getName());
        assertEquals(explorerName, craft.getExplorerName());
        assertEquals(friendlyAcronym, craft.getFriendlyAcronym());
        assertEquals(friendlyName, craft.getFriendlyName());
        assertEquals(thumbnail, craft.getThumbnail());
    }

    private static void assertDish(Dish dish, String name, long azimuth, long elevation, Long windSpeed, Boolean mspa,
                                   Boolean array, Boolean ddor, String activity) {
        assertEquals(name, dish.getName());
        assertEquals(azimuth, dish.getAzimuth());
        assertEquals(elevation, dish.getElevation());
        assertEquals(windSpeed, dish.getWindSpeed());
        assertEquals(mspa, dish.getMultipleSpacecraftPerAperture());
        assertEquals(array, dish.getArray());
        assertEquals(ddor, dish.getDeltaDifferentialOneWayRanging());
        assertEquals(activity, dish.getActivity());
    }

    private static void assertTarget(Target target, String name, long id, long upleg, long downleg, double rtlt) {
        assertEquals(name, target.getName());
        assertEquals(id, target.getId());
        assertEquals(upleg, target.getUpLegRange());
        assertEquals(downleg, target.getDownLegRange());
        assertEquals(rtlt, target.getRoundTripLightTime());
    }

    private static void assertSignal(Signal signal, boolean active, String type, Long rate, Long frequency,
                                     String band, Double power, String spacecraft, Long spacecraftId) {
        assertEquals(active, signal.getActive());
        assertEquals(type, signal.getSignalType());
        assertEquals(rate, signal.getDataRate());
        assertEquals(frequency, signal.getFrequency());
        assertEquals(band, signal.getBand());
        assertEquals(power, signal.getPower());
        assertEquals(spacecraft, signal.getSpacecraft());
        assertEquals(spacecraftId, signal.getSpacecraftId());
    }

    private static void assertMergedStation(MergedStationData station, String name, String friendlyName,
                                            Double longitude, Double latitude, Instant time, ZoneOffset offset) {
        assertEquals(name, station.getName());
        assertEquals(friendlyName, station.getFriendlyName());
        assertEquals(longitude, station.getLongitude());
        assertEquals(latitude, station.getLatitude());
        assertEquals(time, station.getTime());
        assertEquals(offset, station.getTimeZoneOffset());
    }

    private static void assertMergedDish(MergedDishData dish, String name, String friendlyName, String type,
                                         Long azimuth, Long elevation, Long windSpeed, Boolean mspa, Boolean array,
                                         Boolean ddor, String activity) {
        assertEquals(name, dish.getName());
        assertEquals(friendlyName, dish.getFriendlyName());
        assertEquals(type, dish.getType());
        assertEquals(azimuth, dish.getAzimuth());
        assertEquals(elevation, dish.getElevation());
        assertEquals(windSpeed, dish.getWindSpeed());
        assertEquals(mspa, dish.getMultipleSpacecraftForAperture());
        assertEquals(array, dish.getArray());
        assertEquals(ddor, dish.getDeltaDifferentialOneWayRanging());
        assertEquals(activity, dish.getActivity());
    }

    private static void assertMergedTarget(MergedTargetData target, String name, Long id, Long upLegrange,
                                           Long downLegRange, Double rtlt, String explorerName, String acronym,
                                           String friendlyName, Boolean thumbnail) {
        assertEquals(name, target.getName());
        assertEquals(id, target.getId());
        assertEquals(upLegrange, target.getUpLegRange());
        assertEquals(downLegRange, target.getDownLegRange());
        assertEquals(rtlt, target.getRoundTripLightTime());
        assertEquals(explorerName, target.getExplorerName());
        assertEquals(acronym, target.getFriendlyAcronym());
        assertEquals(friendlyName, target.getFriendlyName());
        assertEquals(thumbnail, target.getThumbnail());
    }

}