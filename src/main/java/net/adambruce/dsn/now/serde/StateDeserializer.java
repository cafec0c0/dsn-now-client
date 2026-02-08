package net.adambruce.dsn.now.serde;

import net.adambruce.dsn.now.model.state.Dish;
import net.adambruce.dsn.now.model.state.State;
import net.adambruce.dsn.now.model.state.Signal;
import net.adambruce.dsn.now.model.state.Station;
import net.adambruce.dsn.now.model.state.Target;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.node.JsonNodeType;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * Deserializer for the DSN state endpoint.
 * This endpoint uses an XML format that is not supported by Jackson annotations, thus requiring
 * a custom deserializer.
 */
public class StateDeserializer extends StdDeserializer<State> {

    /**
     * Construct a new instance of the deserializer.
     */
    public StateDeserializer() {
        super(State.class);
    }

    @Override
    public State deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
        JsonNode node = jsonParser.readValueAsTree();

        Long epochMilli = getLongValue(node, "timestamp");

        return new State(
                extractStationsFromRootNode(node),
                extractDishesFromRootNode(node),
                epochMilli == null ? null : Instant.ofEpochMilli(epochMilli)
        );
    }

    private static List<Station> extractStationsFromRootNode(JsonNode node) {
        if (!node.has("station")) {
            return Collections.emptyList();
        }

        return node.get("station").getNodeType() == JsonNodeType.ARRAY
                ? node.get("station").valueStream().map(StateDeserializer::createStationFromNode).toList()
                : List.of(createStationFromNode(node.get("station")));
    }

    private static List<Dish> extractDishesFromRootNode(JsonNode node) {
        if (!node.has("dish")) {
            return Collections.emptyList();
        }

        return node.get("dish").getNodeType() == JsonNodeType.ARRAY
                ? node.get("dish").valueStream().map(StateDeserializer::createDishFromNode).toList()
                : List.of(createDishFromNode(node.get("dish")));
    }

    private static Station createStationFromNode(JsonNode node) {
        Long timeUtc = getLongValue(node, "timeUTC");
        Double timeZoneMillis = getDoubleValue(node, "timeZoneOffset");

        return new Station(
                getStringValue(node, "name"),
                getStringValue(node, "friendlyName"),
                timeUtc == null ? null : Instant.ofEpochMilli(timeUtc),
                timeZoneMillis == null ? null : ZoneOffset.ofTotalSeconds((int)(timeZoneMillis / 1000))
        );
    }

    private static Dish createDishFromNode(JsonNode node) {
        return new Dish(
                getStringValue(node, "name"),
                getLongValue(node, "azimuthAngle"),
                getLongValue(node, "elevationAngle"),
                getLongValue(node, "windSpeed"),
                getBooleanValue(node, "isMSPA"),
                getBooleanValue(node, "isArray"),
                getBooleanValue(node, "isDDOR"),
                getStringValue(node, "activity"),
                getSignalsFromNode(node.get("upSignal")),
                getSignalsFromNode(node.get("downSignal")),
                getTargetsFromNode(node.get("target"))
        );
    }

    private static List<Signal> getSignalsFromNode(JsonNode node) {
        if (node == null) {
            return Collections.emptyList();
        }

        Stream<JsonNode> stream = node.getNodeType() == JsonNodeType.ARRAY
                ? node.valueStream()
                : Stream.of(node);

        return stream.map(StateDeserializer::createSignalFromNode).toList();
    }

    private static Signal createSignalFromNode(JsonNode node) {
        return new Signal(
                getBooleanValue(node, "active"),
                getStringValue(node, "signalType"),
                getLongValue(node, "dataRate"),
                getLongValue(node, "frequency"),
                getStringValue(node, "band"),
                getDoubleValue(node, "power"),
                getStringValue(node, "spacecraft"),
                getLongValue(node, "spacecraftID")
        );
    }

    private static List<Target> getTargetsFromNode(JsonNode node) {
        if (node == null) {
            return Collections.emptyList();
        }

        Stream<JsonNode> stream = node.getNodeType() == JsonNodeType.ARRAY
                ? node.valueStream()
                : Stream.ofNullable(node);

        return stream.map(StateDeserializer::createTargetFromNode).toList();
    }

    private static Target createTargetFromNode(JsonNode node) {
        return new Target(
                getStringValue(node, "name"),
                getLongValue(node, "id"),
                getLongValue(node, "uplegRange"),
                getLongValue(node, "downlegRange"),
                getDoubleValue(node, "rtlt")
        );
    }

    private static String getStringValue(JsonNode node, String name) {
        return isValidChildNode(node, name) ? node.get(name).asString() : null;
    }

    private static Long getLongValue(JsonNode node, String name) {
        return isValidChildNode(node, name) ? node.get(name).asLong() : null;
    }

    private static Double getDoubleValue(JsonNode node, String name) {
        return isValidChildNode(node, name) ? node.get(name).asDouble() : null;
    }

    private static Boolean getBooleanValue(JsonNode node, String name) {
        return isValidChildNode(node, name) ? node.get(name).asBoolean() : null;
    }

    private static boolean isValidChildNode(JsonNode node, String name) {
        return node.has(name) && !node.get(name).stringValue().isBlank();
    }

}
