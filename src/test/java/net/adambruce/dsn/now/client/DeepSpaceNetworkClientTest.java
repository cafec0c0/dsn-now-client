package net.adambruce.dsn.now.client;

import net.adambruce.dsn.now.model.config.Configuration;
import net.adambruce.dsn.now.model.config.Site;
import net.adambruce.dsn.now.model.config.Spacecraft;
import net.adambruce.dsn.now.model.state.Dish;
import net.adambruce.dsn.now.model.state.State;
import net.adambruce.dsn.now.model.state.Signal;
import net.adambruce.dsn.now.model.state.Station;
import net.adambruce.dsn.now.model.state.Target;
import net.adambruce.dsn.now.model.merged.MergedDishData;
import net.adambruce.dsn.now.model.merged.MergedDeepSpaceNetworkData;
import net.adambruce.dsn.now.model.merged.MergedStationData;
import net.adambruce.dsn.now.serde.StateDeserializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeepSpaceNetworkClientTest {

    @Mock
    private HttpClient configClient;

    @Mock
    private HttpClient dsnClient;

    private final DeepSpaceNetworkClient client = DeepSpaceNetworkClient.newDeepSpaceNetworkClient();

    @Test
    void shouldFetchConfiguration() throws Exception {
        try (MockedStatic<HttpClient> staticHttpClient = mockStatic(HttpClient.class)) {
            staticHttpClient.when(HttpClient::newHttpClient).thenReturn(configClient);
            HttpResponse response = mockHttpResponseFromFile("config/config.xml");
            when(configClient.send(any(), any())).thenReturn(response);

            Configuration configuration = client.fetchConfiguration();

            verify(configClient, times(1)).send(any(), any());

            assertSite(configuration.sites().get(0), "mdscc", "Madrid", -4.2480085, 40.2413554);
            assertSite(configuration.sites().get(1), "gdscc", "Goldstone", -116.8895382, 35.2443523);
            assertSite(configuration.sites().get(2), "cdscc", "Canberra", 148.9812673, -35.2209189);

            assertSpacecraft(configuration.spacecraft().get(0), "ace", "sc_ace", null, "Advanced Composition Explorer", true);
            assertSpacecraft(configuration.spacecraft().get(1), "apm1", "", null, "Astrobotic Peregrine 1 Mission", true);
            assertSpacecraft(configuration.spacecraft().get(2), "agm1", "", null, "Astrobotic Griffin Lander", true);

            assertSpacecraft(configuration.spacecraft().get(29), "em1", "", "art1", "Human Space Flight: Artemis I", true);
        }
    }

    @Test
    void shouldFetchDsnData() throws Exception {
        try (MockedStatic<HttpClient> staticHttpClient = mockStatic(HttpClient.class)) {
            staticHttpClient.when(HttpClient::newHttpClient).thenReturn(dsnClient);
            HttpResponse response = mockHttpResponseFromFile("dsn/dsn.xml");
            when(dsnClient.send(any(), any())).thenReturn(response);

            State state = client.fetchDeepSpaceNetworkState();

            verify(dsnClient, times(1)).send(any(), any());

            assertStation(state.stations().get(0), "gdscc", "Goldstone", Instant.ofEpochMilli(1770497799000L), ZoneOffset.ofTotalSeconds(-28800));

            assertDish(state.dishes().get(0), "DSS14", 0, 90, null, false, false, false, "Antenna Unplanned Maintenance");
            assertTarget(state.dishes().get(0).targets().get(0), "DSS", 99, -1, -1, -1);

            assertDish(state.dishes().get(1), "DSS25", 251, 13, 8L, false, false, false, "DSN Very Long Baseline Interferometry");
            assertTarget(state.dishes().get(1).targets().get(0), "DSN", 99, -1, -1, -1);

            assertDish(state.dishes().get(2), "DSS24", 179, 18, 8L, false, false, false, "Spacecraft Telemetry, Tracking, and Command");
            assertSignal(state.dishes().get(2).upSignals().get(0), true, "data", 0L, 0L, "S", 0.2, "MMS2", -109L);
            assertSignal(state.dishes().get(2).downSignals().get(0), true, "data", 1250000L, 0L, "S", -110D, "MMS2", -109L);
            assertTarget(state.dishes().get(2).targets().get(0), "MMS2", 109, 130000, 130000, 0.87);

            assertDish(state.dishes().get(3), "DSS26", 180, 90, 8L, false, false, false, "Spacecraft Telemetry, Tracking, and Command");
            assertSignal(state.dishes().get(3).upSignals().get(0), false, "none", 0L, 0L, "X", 0D, "SOHO", -21L);
            assertSignal(state.dishes().get(3).downSignals().get(0), false, "none", 0L, 0L, "S", -480D, "SOHO", -21L);
            assertTarget(state.dishes().get(3).targets().get(0), "SOHO", 21, 1640000, 1640000, 10.9);

        }
    }

    @Test
    void shouldMergeConfigAndDsnData() throws Exception {
        try (MockedStatic<HttpClient> staticHttpClient = mockStatic(HttpClient.class)) {
            staticHttpClient.when(HttpClient::newHttpClient)
                    .thenReturn(configClient)
                    .thenReturn(dsnClient);

            HttpResponse configResponse = mockHttpResponseFromFile("config/config.xml");
            when(configClient.send(any(), any())).thenReturn(configResponse);

            HttpResponse dsnResponse = mockHttpResponseFromFile("dsn/dsn.xml");
            when(dsnClient.send(any(), any())).thenReturn(dsnResponse);

            MergedDeepSpaceNetworkData mergedData = client.fetchMergedDeepSpaceNetworkData();

            verify(dsnClient, times(1)).send(any(), any());
            verify(configClient, times(1)).send(any(), any());

            assertMergedStation(
                    mergedData.stations().get(0),
                    "mdscc", "Madrid", -4.2480085, 40.2413554, Instant.ofEpochMilli(1770497799000L),
                    ZoneOffset.ofTotalSeconds(3600)
            );

            assertMergedDish(mergedData.stations().get(0).dishes().get(0),
                    "DSS63", "DSS 63", "70M",  131L, 65L, 2L, false, false, false,
                    "Spacecraft Telemetry, Tracking, and Command"
            );

            assertMergedDish(mergedData.stations().get(0).dishes().get(1),
                    "DSS65", "DSS 65", "34MHEF",  null, null, null, null, null, null, null
            );

            assertMergedDish(mergedData.stations().get(0).dishes().get(2),
                    "DSS53", "DSS 53", "34M",  0L, 90L, null, false, false, false,
                    "Engineering Upgrades"
            );

            assertEquals(Instant.ofEpochMilli(1770497799000L), mergedData.timestamp());
        }
    }

    @Test
    void shouldMergeConfigAndDsnDataWithMissingStationData() throws Exception {
        try (MockedStatic<HttpClient> staticHttpClient = mockStatic(HttpClient.class)) {
            staticHttpClient.when(HttpClient::newHttpClient)
                    .thenReturn(configClient)
                    .thenReturn(dsnClient);

            HttpResponse configResponse = mockHttpResponseFromFile("config/config.xml");
            when(configClient.send(any(), any())).thenReturn(configResponse);

            HttpResponse dsnResponse = mockHttpResponseFromFile("dsn/dsnWithoutStations.xml");
            when(dsnClient.send(any(), any())).thenReturn(dsnResponse);

            MergedDeepSpaceNetworkData mergedData = client.fetchMergedDeepSpaceNetworkData();

            verify(dsnClient, times(1)).send(any(), any());
            verify(configClient, times(1)).send(any(), any());

            assertMergedStation(
                    mergedData.stations().get(0),
                    "mdscc", "Madrid", -4.2480085, 40.2413554, null, null
            );

            assertMergedDish(mergedData.stations().get(0).dishes().get(0),
                    "DSS63", "DSS 63", "70M",  131L, 65L, 2L, false, false, false,
                    "Spacecraft Telemetry, Tracking, and Command"
            );

            assertMergedDish(mergedData.stations().get(0).dishes().get(1),
                    "DSS65", "DSS 65", "34MHEF",  null, null, null, null, null, null, null
            );

            assertMergedDish(mergedData.stations().get(0).dishes().get(2),
                    "DSS53", "DSS 53", "34M",  0L, 90L, null, false, false, false,
                    "Engineering Upgrades"
            );

            assertEquals(Instant.ofEpochMilli(1770497799000L), mergedData.timestamp());
        }
    }

    @Test
    void shouldNotFetchConfigurationIfAlreadyPopulatedWhenFetchingMergedData() throws Exception {
        try (MockedStatic<HttpClient> staticHttpClient = mockStatic(HttpClient.class)) {
            staticHttpClient.when(HttpClient::newHttpClient)
                    .thenReturn(configClient)
                    .thenReturn(dsnClient);

            HttpResponse configResponse = mockHttpResponseFromFile("config/config.xml");
            when(configClient.send(any(), any())).thenReturn(configResponse);

            HttpResponse dsnResponse = mockHttpResponseFromFile("dsn/dsn.xml");
            when(dsnClient.send(any(), any())).thenReturn(dsnResponse);

            client.fetchConfiguration();
            MergedDeepSpaceNetworkData mergedData = client.fetchMergedDeepSpaceNetworkData();

            verify(configClient, times(1)).send(any(), any());
            verify(dsnClient, times(1)).send(any(), any());

            assertMergedStation(
                    mergedData.stations().get(0),
                    "mdscc", "Madrid", -4.2480085, 40.2413554, Instant.ofEpochMilli(1770497799000L),
                    ZoneOffset.ofTotalSeconds(3600)
            );

            assertMergedDish(mergedData.stations().get(0).dishes().get(0),
                    "DSS63", "DSS 63", "70M",  131L, 65L, 2L, false, false, false,
                    "Spacecraft Telemetry, Tracking, and Command"
            );

            assertMergedDish(mergedData.stations().get(0).dishes().get(1),
                    "DSS65", "DSS 65", "34MHEF",  null, null, null, null, null, null, null
            );

            assertMergedDish(mergedData.stations().get(0).dishes().get(2),
                    "DSS53", "DSS 53", "34M",  0L, 90L, null, false, false, false,
                    "Engineering Upgrades"
            );

            assertEquals(Instant.ofEpochMilli(1770497799000L), mergedData.timestamp());
        }
    }

    private static HttpResponse<byte[]> mockHttpResponseFromFile(String filePath) {
        try {
            byte[] configBytes = StateDeserializer.class.getClassLoader().getResourceAsStream(filePath).readAllBytes();
            HttpResponse<byte[]> response = mock(HttpResponse.class);
            when(response.body()).thenReturn(configBytes);
            return response;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static void assertStation(Station station, String name, String friendlyName, Instant time,
                                      ZoneOffset timeZone) {
        assertEquals(name, station.name());
        assertEquals(friendlyName, station.friendlyName());
        assertEquals(time, station.time());
        assertEquals(timeZone, station.timeZoneOffset());
    }

    private static void assertSite(Site site, String name, String friendlyName, Double longitude, Double latitude) {
        assertEquals(name, site.name());
        assertEquals(friendlyName, site.friendlyName());
        assertEquals(longitude, site.longitude());
        assertEquals(latitude, site.latitude());
    }

    private static void assertSpacecraft(Spacecraft craft, String name, String explorerName, String friendlyAcronym,
                                         String friendlyName, Boolean thumbnail) {
        assertEquals(name, craft.name());
        assertEquals(explorerName, craft.explorerName());
        assertEquals(friendlyAcronym, craft.friendlyAcronym());
        assertEquals(friendlyName, craft.friendlyName());
        assertEquals(thumbnail, craft.thumbnail());
    }

    private static void assertDish(Dish dish, String name, long azimuth, long elevation, Long windSpeed, Boolean mspa,
                                   Boolean array, Boolean ddor, String activity) {
        assertEquals(name, dish.name());
        assertEquals(azimuth, dish.azimuthAngle());
        assertEquals(elevation, dish.elevationAngle());
        assertEquals(windSpeed, dish.windSpeed());
        assertEquals(mspa, dish.multipleSpacecraftPerAperture());
        assertEquals(array, dish.array());
        assertEquals(ddor, dish.deltaDifferentialOneWayRanging());
        assertEquals(activity, dish.activity());
    }

    private static void assertTarget(Target target, String name, long id, long upleg, long downleg, double rtlt) {
        assertEquals(name, target.name());
        assertEquals(id, target.id());
        assertEquals(upleg, target.upLegRange());
        assertEquals(downleg, target.downLegRange());
        assertEquals(rtlt, target.roundTripLightTime());
    }

    private static void assertSignal(Signal signal, boolean active, String type, Long rate, Long frequency,
                                     String band, Double power, String spacecraft, Long spacecraftId) {
        assertEquals(active, signal.active());
        assertEquals(type, signal.signalType());
        assertEquals(rate, signal.dataRate());
        assertEquals(frequency, signal.frequency());
        assertEquals(band, signal.band());
        assertEquals(power, signal.power());
        assertEquals(spacecraft, signal.spacecraft());
        assertEquals(spacecraftId, signal.spacecraftId());
    }

    private static void assertMergedStation(MergedStationData station, String name, String friendlyName,
                                            Double longitude, Double latitude, Instant time, ZoneOffset offset) {
        assertEquals(name, station.name());
        assertEquals(friendlyName, station.friendlyName());
        assertEquals(longitude, station.longitude());
        assertEquals(latitude, station.latitude());
        assertEquals(time, station.time());
        assertEquals(offset, station.timeZoneOffset());
    }

    private static void assertMergedDish(MergedDishData dish, String name, String friendlyName, String type,
                                         Long azimuth, Long elevation, Long windSpeed, Boolean mspa, Boolean array,
                                         Boolean ddor, String activity) {
        assertEquals(name, dish.name());
        assertEquals(friendlyName, dish.friendlyName());
        assertEquals(type, dish.type());
        assertEquals(azimuth, dish.azimuthAngle());
        assertEquals(elevation, dish.elevationAngle());
        assertEquals(windSpeed, dish.windSpeed());
        assertEquals(mspa, dish.multipleSpacecraftForAperture());
        assertEquals(array, dish.array());
        assertEquals(ddor, dish.deltaDifferentialOneWayRanging());
        assertEquals(activity, dish.activity());
    }

}