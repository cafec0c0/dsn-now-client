package net.adambruce.dsn.now.client;

import net.adambruce.dsn.now.model.config.Configuration;
import net.adambruce.dsn.now.model.merged.MergedDishData;
import net.adambruce.dsn.now.model.merged.MergedDeepSpaceNetworkData;
import net.adambruce.dsn.now.model.merged.MergedStationData;
import net.adambruce.dsn.now.model.merged.MergedTargetData;
import net.adambruce.dsn.now.model.config.Dish;
import net.adambruce.dsn.now.model.config.Site;
import net.adambruce.dsn.now.model.config.Spacecraft;
import net.adambruce.dsn.now.model.state.State;
import net.adambruce.dsn.now.model.state.Station;
import net.adambruce.dsn.now.model.state.Target;
import net.adambruce.dsn.now.serde.StateDeserializer;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.dataformat.xml.XmlMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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
            .addDeserializer(State.class, new StateDeserializer());

    private static final ObjectMapper MAPPER = XmlMapper.builder()
            .addModule(SERDE_MODULE)
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
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("https://eyes.nasa.gov/apps/dsn-now/config.xml"))
                .build();

        HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
        Configuration configuration = MAPPER.readValue(response.body(), Configuration.class);
        CONFIG.set(configuration);
        return configuration;
    }

    /**
     * Fetches the current state of the DSN.
     *
     * @return the current DSN state
     * @throws Exception the network request failed, or the response could not be deserialized
     */
    public State fetchDeepSpaceNetworkState() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("https://eyes.nasa.gov/dsn/data/dsn.xml"))
                .build();

        HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
        return MAPPER.readValue(response.body(), State.class);
    }

    /**
     * Fetches the current state of the DSN and merges the response with the DSN Now configuration to
     * provide a complete representation of DSN's current state.
     *
     * @return the current DSN state merged with the DSN Now configuration
     * @throws Exception the network request failed, or the response could not be deserialized
     */
    public MergedDeepSpaceNetworkData fetchMergedDeepSpaceNetworkData() throws Exception {
        if (CONFIG.get() == null) {
            fetchConfiguration();
        }

        Configuration configuration = CONFIG.get();
        State state = fetchDeepSpaceNetworkState();

        Map<String, Station> dsnStationByName = state.stations().stream()
                .collect(Collectors.toMap(s -> s.name().toLowerCase(), Function.identity()));

        Map<String, net.adambruce.dsn.now.model.state.Dish> dsnDishByName = state.dishes().stream()
                .collect(Collectors.toMap(d -> d.name().toLowerCase(), Function.identity()));

        Map<String, Spacecraft> configSpacecraftByName = configuration.spacecraft().stream()
                .collect(Collectors.toMap(s -> s.name().toLowerCase(), Function.identity()));

        List<MergedStationData> stations = configuration.sites().stream()
                .map(site -> getMergedStation(site, dsnStationByName, dsnDishByName, configSpacecraftByName))
                .toList();

        return new MergedDeepSpaceNetworkData(
                stations,
                state.timestamp()
        );
    }

    private MergedTargetData getMergedTarget(Target target, Map<String, Spacecraft> spacecraftMap) {
        Spacecraft spacecraft = spacecraftMap.get(target.name().toLowerCase());

        return new MergedTargetData(
                target.name(),
                target.id(),
                target.upLegRange(),
                target.downLegRange(),
                target.roundTripLightTime(),
                spacecraft != null ? spacecraft.explorerName() : null,
                spacecraft != null ? spacecraft.friendlyAcronym() : null,
                spacecraft != null ? spacecraft.friendlyName() : null,
                spacecraft != null ? spacecraft.thumbnail() : null
        );
    }

    private MergedDishData getMergedDish(Dish configDish,
                                         Map<String, net.adambruce.dsn.now.model.state.Dish> dishMap,
                                         Map<String, Spacecraft> spacecraftMap) {
        net.adambruce.dsn.now.model.state.Dish dish = dishMap.get(configDish.name().toLowerCase());

        return new MergedDishData(
                configDish.name(),
                configDish.friendlyName(),
                configDish.type(),
                dish != null ? dish.azimuthAngle() : null,
                dish != null ? dish.elevationAngle() : null,
                dish != null ? dish.windSpeed() : null,
                dish != null ? dish.multipleSpacecraftPerAperture() : null,
                dish != null ? dish.array() : null,
                dish != null ? dish.deltaDifferentialOneWayRanging() : null,
                dish != null ? dish.activity() : null,
                dish != null ? dish.upSignals() : null,
                dish != null ? dish.downSignals() : null,
                dish != null ? dish.targets().stream()
                        .map(t -> getMergedTarget(t, spacecraftMap)).toList() : Collections.emptyList()
        );
    }

    private MergedStationData getMergedStation(Site configSite,
                                               Map<String, Station> stationMap,
                                               Map<String, net.adambruce.dsn.now.model.state.Dish> dishMap,
                                               Map<String, Spacecraft> spacecraftMap) {
        Station station = stationMap.get(configSite.name().toLowerCase());

        return new MergedStationData(
                configSite.name(),
                configSite.friendlyName(),
                configSite.longitude(),
                configSite.latitude(),
                station != null ? station.time() : null,
                station != null ? station.timeZoneOffset() : null,
                configSite.dishes().stream().map(dish -> getMergedDish(dish, dishMap, spacecraftMap)).toList()
        );
    }

}
