package net.adambruce.dsn.now.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
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
import net.adambruce.dsn.now.serde.DurationDeserializer;
import net.adambruce.dsn.now.serde.ZoneOffsetDeserializer;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.time.Duration;
import java.time.Instant;
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
@Slf4j
public class DeepSpaceNetworkClient {

    private static final String DSN_CONFIG_URL = "https://eyes.nasa.gov/apps/dsn-now/config.xml";
    private static final String DSN_STATE_URL = "https://eyes.nasa.gov/dsn/data/dsn.xml";

    private static final SimpleModule SERDE_MODULE = new SimpleModule()
            .addDeserializer(ZoneOffset.class, new ZoneOffsetDeserializer())
            .addDeserializer(Duration.class, new DurationDeserializer());

    private static final ObjectMapper MAPPER = XmlMapper.builder()
            .addModule(new JavaTimeModule())
            .addModule(SERDE_MODULE)
            .configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .build();

    private final AtomicReference<Configuration> CONFIG = new AtomicReference<>();
    private final AtomicReference<Instant> LAST_CONFIG_REFRESH = new AtomicReference<>();

    private final Duration maxConfigAge;

    private DeepSpaceNetworkClient(Duration maxConfigurationAge) {
        this.maxConfigAge = maxConfigurationAge;
    }

    /**
     * Creates a new instance of the client with the default max configuration age (30 mins).
     *
     * @return a new client
     */
    public static DeepSpaceNetworkClient newDeepSpaceNetworkClient() {
        return newDeepSpaceNetworkClient(Duration.ofMinutes(30));
    }

    /**
     * Creates a new instance of the client.
     *
     * @param maxConfigurationAge the maximum configuration age before {@link #fetchMergedData()} will trigger refresh
     * @return a new client
     */
    public static DeepSpaceNetworkClient newDeepSpaceNetworkClient(Duration maxConfigurationAge) {
        return new DeepSpaceNetworkClient(maxConfigurationAge);
    }

    /**
     * Fetches the DSN Now configuration, and caches it for later use.
     *
     * @return the configuration
     * @throws Exception the network request failed, or the response could not be deserialized
     */
    public Configuration fetchConfiguration() throws Exception {
        log.debug("fetching configuration from {}", DSN_CONFIG_URL);
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(DSN_CONFIG_URL);
            byte[] response = httpClient.execute(request, classicHttpResponse -> EntityUtils.toByteArray(classicHttpResponse.getEntity()));
            Configuration configuration = MAPPER.readValue(response, Configuration.class);
            CONFIG.set(configuration);
            LAST_CONFIG_REFRESH.set(Instant.now());
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
        log.debug("fetching state from {}", DSN_STATE_URL);
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(DSN_STATE_URL);
            byte[] response = httpClient.execute(request, classicHttpResponse -> EntityUtils.toByteArray(classicHttpResponse.getEntity()));
            return MAPPER.readValue(response, State.class);
        }
    }

    /**
     * Fetches the current state of the DSN and merges the response with the DSN Now configuration to
     * provide a complete representation of DSN's current state.
     * If the configuration is uninitialized or expired, a new configuration will be fetched.
     *
     * @return the current DSN state merged with the DSN Now configuration
     * @throws Exception the network request failed, or the response could not be deserialized
     */
    public MergedData fetchMergedData() throws Exception {
        if (CONFIG.get() == null) {
            log.debug("configuration has not yet been set, updating configuration before fetching state");
            fetchConfiguration();
        }
        if (Instant.now().isAfter(LAST_CONFIG_REFRESH.get().plus(maxConfigAge))) {
            log.debug("configuration has expired, updating configuration before fetching state");
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
