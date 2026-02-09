package net.adambruce.dsn.now.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.adambruce.dsn.now.model.config.Configuration;
import net.adambruce.dsn.now.model.merged.MergedData;
import net.adambruce.dsn.now.model.merged.MergedDishData;
import net.adambruce.dsn.now.model.merged.MergedStationData;
import net.adambruce.dsn.now.model.merged.MergedTargetData;
import net.adambruce.dsn.now.model.config.Dish;
import net.adambruce.dsn.now.model.config.Site;
import net.adambruce.dsn.now.model.config.Spacecraft;
import net.adambruce.dsn.now.model.state.State;
import net.adambruce.dsn.now.model.state.Station;
import net.adambruce.dsn.now.model.state.Target;
import net.adambruce.dsn.now.serde.ZoneOffsetDeserializer;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Client for requesting information from the DSN Now API.
 */
public class DeepSpaceNetworkClient {

    private static final SimpleModule SERDE_MODULE = new SimpleModule()
            .addDeserializer(ZoneOffset.class, new ZoneOffsetDeserializer());

    private static final ObjectMapper MAPPER = XmlMapper.builder()
            .addModule(new JavaTimeModule())
            .addModule(SERDE_MODULE)
            .configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .build();

    private final AtomicReference<Configuration> CONFIG = new AtomicReference<>();

    private DeepSpaceNetworkClient() {

    }

    /**
     * Creates a new instance of the client.
     *
     * @return a new client
     */
    public static DeepSpaceNetworkClient newDeepSpaceNetworkClient() {
        return new DeepSpaceNetworkClient();
    }

    /**
     * Fetches the DSN Now configuration, and caches it for later use.
     *
     * @return the configuration
     * @throws Exception the network request failed, or the response could not be deserialized
     */
    public Configuration fetchConfiguration() throws Exception {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet("https://eyes.nasa.gov/apps/dsn-now/config.xml");
            byte[] response = httpClient.execute(request, classicHttpResponse -> EntityUtils.toByteArray(classicHttpResponse.getEntity()));
            Configuration configuration = MAPPER.readValue(response, Configuration.class);
            CONFIG.set(configuration);
            return configuration;
        }
    }

    /**
     * Fetches the current state of the DSN.
     *
     * @return the current DSN state
     * @throws Exception the network request failed, or the response could not be deserialized
     */
    public State fetchState() throws Exception {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet("https://eyes.nasa.gov/dsn/data/dsn.xml");
            byte[] response = httpClient.execute(request, classicHttpResponse -> EntityUtils.toByteArray(classicHttpResponse.getEntity()));
            return MAPPER.readValue(response, State.class);
        }
    }

    /**
     * Fetches the current state of the DSN and merges the response with the DSN Now configuration to
     * provide a complete representation of DSN's current state.
     *
     * @return the current DSN state merged with the DSN Now configuration
     * @throws Exception the network request failed, or the response could not be deserialized
     */
    public MergedData fetchMergedState() throws Exception {
        if (CONFIG.get() == null) {
            fetchConfiguration();
        }

        Configuration configuration = CONFIG.get();
        State state = fetchState();

        Map<String, Station> dsnStationByName = state.getStations().stream()
                .collect(Collectors.toMap(s -> s.getName().toLowerCase(), Function.identity()));

        Map<String, net.adambruce.dsn.now.model.state.Dish> dsnDishByName = state.getDishes().stream()
                .collect(Collectors.toMap(d -> d.getName().toLowerCase(), Function.identity()));

        Map<String, Spacecraft> configSpacecraftByName = configuration.getSpacecraft().stream()
                .collect(Collectors.toMap(s -> s.getName().toLowerCase(), Function.identity()));

        List<MergedStationData> stations = configuration.getSites().stream()
                .map(site -> getMergedStation(site, dsnStationByName, dsnDishByName, configSpacecraftByName))
                .collect(Collectors.toList());

        return new MergedData(
                stations,
                state.getTimestamp()
        );
    }

    private MergedTargetData getMergedTarget(Target target, Map<String, Spacecraft> spacecraftMap) {
        Spacecraft spacecraft = spacecraftMap.get(target.getName().toLowerCase());

        return new MergedTargetData(
                target.getName(),
                target.getId(),
                target.getUpLegRange(),
                target.getDownLegRange(),
                target.getRoundTripLightTime(),
                spacecraft != null ? spacecraft.getExplorerName() : null,
                spacecraft != null ? spacecraft.getFriendlyAcronym() : null,
                spacecraft != null ? spacecraft.getFriendlyName() : null,
                spacecraft != null ? spacecraft.getThumbnail() : null
        );
    }

    private MergedDishData getMergedDish(Dish configDish,
                                         Map<String, net.adambruce.dsn.now.model.state.Dish> dishMap,
                                         Map<String, Spacecraft> spacecraftMap) {
        net.adambruce.dsn.now.model.state.Dish dish = dishMap.get(configDish.getName().toLowerCase());

        return new MergedDishData(
                configDish.getName(),
                configDish.getFriendlyName(),
                configDish.getType(),
                dish != null ? dish.getAzimuth() : null,
                dish != null ? dish.getElevation() : null,
                dish != null ? dish.getWindSpeed() : null,
                dish != null ? dish.getMultipleSpacecraftPerAperture() : null,
                dish != null ? dish.getArray() : null,
                dish != null ? dish.getDeltaDifferentialOneWayRanging() : null,
                dish != null ? dish.getActivity() : null,
                dish != null ? dish.getUpSignals() : null,
                dish != null ? dish.getDownSignals() : null,
                dish != null
                        ? dish.getTargets().stream().map(t -> getMergedTarget(t, spacecraftMap))
                            .collect(Collectors.toList())
                        : Collections.emptyList()
        );
    }

    private MergedStationData getMergedStation(Site configSite,
                                               Map<String, Station> stationMap,
                                               Map<String, net.adambruce.dsn.now.model.state.Dish> dishMap,
                                               Map<String, Spacecraft> spacecraftMap) {
        Station station = stationMap.get(configSite.getName().toLowerCase());

        return new MergedStationData(
                configSite.getName(),
                configSite.getFriendlyName(),
                configSite.getLongitude(),
                configSite.getLatitude(),
                station != null ? station.getTime() : null,
                station != null ? station.getTimeZoneOffset() : null,
                configSite.getDishes().stream()
                        .map(dish -> getMergedDish(dish, dishMap, spacecraftMap))
                        .collect(Collectors.toList())
        );
    }

}
