package net.adambruce.dsn.now.serde;

import net.adambruce.dsn.now.model.state.Dish;
import net.adambruce.dsn.now.model.state.State;
import net.adambruce.dsn.now.model.state.Signal;
import net.adambruce.dsn.now.model.state.Station;
import net.adambruce.dsn.now.model.state.Target;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.dataformat.xml.XmlMapper;

import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StateDeserializerTest {

    private static final ObjectMapper MAPPER = XmlMapper.builder().build();

    @Mock
    private DeserializationContext deserializationContext;

    private final StateDeserializer deserializer = new StateDeserializer();

    @Test
    void shouldDeserializeDsn() {
        State state = deserializer.deserialize(mockParserFromFile("dsn/dsn.xml"), deserializationContext);

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

        assertStation(state.stations().get(1), "mdscc", "Madrid", Instant.ofEpochMilli(1770497799000L), ZoneOffset.ofTotalSeconds(3600));

        assertDish(state.dishes().get(4), "DSS53", 0, 90, null, false, false, false, "Engineering Upgrades");
        assertTarget(state.dishes().get(4).targets().get(0), "DSN", 99, -1, -1, -1);

        assertDish(state.dishes().get(5), "DSS63", 131, 65, 2L, false, false, false, "Spacecraft Telemetry, Tracking, and Command");
        assertSignal(state.dishes().get(5).upSignals().get(0), true, "data", 0L, 0L, "X", 18D, "JNO", -61L);
        assertSignal(state.dishes().get(5).downSignals().get(0), true, "data", 200000L, 0L, "X", -130D, "JNO", -61L);
        assertTarget(state.dishes().get(5).targets().get(0), "JNO", 61, 651000000, 651000000, 4350);

        assertDish(state.dishes().get(6), "DSS54", 129, 63, 2L, false, false, false, "Spacecraft Telemetry, Tracking, and Command");
        assertSignal(state.dishes().get(6).upSignals().get(0), true, "data", 0L, 0L, "S", 4.8, "JWST", -170L);
        assertSignal(state.dishes().get(6).downSignals().get(0), true, "data", 28000000L, 0L, "K", -91D, "JWST", -170L);
        assertSignal(state.dishes().get(6).downSignals().get(1), true, "data", 40000L, 0L, "S", -120D, "JWST", -170L);
        assertTarget(state.dishes().get(6).targets().get(0), "JWST", 170, 1480000, 1480000, 9.89);

        assertStation(state.stations().get(2), "cdscc", "Canberra", Instant.ofEpochMilli(1770497799000L), ZoneOffset.ofTotalSeconds(39600));

        assertDish(state.dishes().get(7), "DSS35", 46, 54, 0L, false, false, false, "DSN Very Long Baseline Interferometry");
        assertTarget(state.dishes().get(7).targets().get(0), "DSN", 99, -1, -1, -1);

        assertDish(state.dishes().get(8), "DSS43", 96, 23, 0L, false, false, false, "Spacecraft Telemetry, Tracking, and Command");
        assertSignal(state.dishes().get(8).upSignals().get(0), true, "data", 0L, 0L, "S", 1.1, "MRO", -74L);
        assertSignal(state.dishes().get(8).downSignals().get(0), false, "none", 2969000L, 0L, "X", -150D, "MRO", -74L);
        assertTarget(state.dishes().get(8).targets().get(0), "MRO", 74, 355000000, 355000000, 2370);

        assertDish(state.dishes().get(9), "DSS36", 0, 90, null, false, false, false, "Spacecraft Telemetry, Tracking, and Command");
        assertSignal(state.dishes().get(9).downSignals().get(0), true, "data", 0L, 0L, "X", -170D, "VGR2", -32L);
        assertSignal(state.dishes().get(9).downSignals().get(1), true, "data", 160L, 0L, "X", -160D, "VGR2", -32L);
        assertTarget(state.dishes().get(9).targets().get(0), "VGR2", 32, 21400000000L, 21400000000L, 142000);

        assertDish(state.dishes().get(10), "DSS34", 141, 44, 0L, false, false, false, "Spacecraft Telemetry, Tracking, and Command");
        assertSignal(state.dishes().get(10).downSignals().get(0), true, "data", 0L, 0L, "X", -170D, "VGR2", -32L);
        assertTarget(state.dishes().get(10).targets().get(0), "VGR2", 32, 21400000000L, 21400000000L, 142000);

        assertEquals(Instant.ofEpochMilli(1770497799000L), state.timestamp());
    }

    @Test
    void shouldDeserializeDsnWithoutStations() {
        State state = deserializer.deserialize(mockParserFromFile("dsn/dsnWithoutStations.xml"), deserializationContext);

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

        assertDish(state.dishes().get(4), "DSS53", 0, 90, null, false, false, false, "Engineering Upgrades");
        assertTarget(state.dishes().get(4).targets().get(0), "DSN", 99, -1, -1, -1);

        assertDish(state.dishes().get(5), "DSS63", 131, 65, 2L, false, false, false, "Spacecraft Telemetry, Tracking, and Command");
        assertSignal(state.dishes().get(5).upSignals().get(0), true, "data", 0L, 0L, "X", 18D, "JNO", -61L);
        assertSignal(state.dishes().get(5).downSignals().get(0), true, "data", 200000L, 0L, "X", -130D, "JNO", -61L);
        assertTarget(state.dishes().get(5).targets().get(0), "JNO", 61, 651000000, 651000000, 4350);

        assertDish(state.dishes().get(6), "DSS54", 129, 63, 2L, false, false, false, "Spacecraft Telemetry, Tracking, and Command");
        assertSignal(state.dishes().get(6).upSignals().get(0), true, "data", 0L, 0L, "S", 4.8, "JWST", -170L);
        assertSignal(state.dishes().get(6).downSignals().get(0), true, "data", 28000000L, 0L, "K", -91D, "JWST", -170L);
        assertSignal(state.dishes().get(6).downSignals().get(1), true, "data", 40000L, 0L, "S", -120D, "JWST", -170L);
        assertTarget(state.dishes().get(6).targets().get(0), "JWST", 170, 1480000, 1480000, 9.89);

        assertDish(state.dishes().get(7), "DSS35", 46, 54, 0L, false, false, false, "DSN Very Long Baseline Interferometry");
        assertTarget(state.dishes().get(7).targets().get(0), "DSN", 99, -1, -1, -1);

        assertDish(state.dishes().get(8), "DSS43", 96, 23, 0L, false, false, false, "Spacecraft Telemetry, Tracking, and Command");
        assertSignal(state.dishes().get(8).upSignals().get(0), true, "data", 0L, 0L, "S", 1.1, "MRO", -74L);
        assertSignal(state.dishes().get(8).downSignals().get(0), false, "none", 2969000L, 0L, "X", -150D, "MRO", -74L);
        assertTarget(state.dishes().get(8).targets().get(0), "MRO", 74, 355000000, 355000000, 2370);

        assertDish(state.dishes().get(9), "DSS36", 0, 90, null, false, false, false, "Spacecraft Telemetry, Tracking, and Command");
        assertSignal(state.dishes().get(9).downSignals().get(0), true, "data", 0L, 0L, "X", -170D, "VGR2", -32L);
        assertSignal(state.dishes().get(9).downSignals().get(1), true, "data", 160L, 0L, "X", -160D, "VGR2", -32L);
        assertTarget(state.dishes().get(9).targets().get(0), "VGR2", 32, 21400000000L, 21400000000L, 142000);

        assertDish(state.dishes().get(10), "DSS34", 141, 44, 0L, false, false, false, "Spacecraft Telemetry, Tracking, and Command");
        assertSignal(state.dishes().get(10).downSignals().get(0), true, "data", 0L, 0L, "X", -170D, "VGR2", -32L);
        assertTarget(state.dishes().get(10).targets().get(0), "VGR2", 32, 21400000000L, 21400000000L, 142000);

        assertEquals(Instant.ofEpochMilli(1770497799000L), state.timestamp());
    }

    @Test
    void shouldDeserializeDsnWithoutDishes() {
        State state = deserializer.deserialize(mockParserFromFile("dsn/dsnWithoutDishes.xml"), deserializationContext);

        assertStation(state.stations().get(0), "gdscc", "Goldstone", Instant.ofEpochMilli(1770497799000L), ZoneOffset.ofTotalSeconds(-28800));
        assertStation(state.stations().get(1), "mdscc", "Madrid", Instant.ofEpochMilli(1770497799000L), ZoneOffset.ofTotalSeconds(3600));
        assertStation(state.stations().get(2), "cdscc", "Canberra", Instant.ofEpochMilli(1770497799000L), ZoneOffset.ofTotalSeconds(39600));

        assertEquals(Instant.ofEpochMilli(1770497799000L), state.timestamp());
    }

    @Test
    void shouldDeserializeNullTimestamp() {
        State state = deserializer.deserialize(mockParserFromFile("dsn/dsnWithoutTimestamp.xml"), deserializationContext);

        assertNull(state.timestamp());
    }

    @Test
    void shouldDeserializeDishWithMultipleTargets() {
        State state = deserializer.deserialize(mockParserFromFile("dsn/dsnWithMultipleTargets.xml"), deserializationContext);

        assertStation(state.stations().get(0), "gdscc", "Goldstone", Instant.ofEpochMilli(1770497799000L), ZoneOffset.ofTotalSeconds(-28800));

        assertDish(state.dishes().get(0), "DSS14", 0, 90, null, false, false, false, "Antenna Unplanned Maintenance");
        assertTarget(state.dishes().get(0).targets().get(0), "DSS", 99, -1, -1, -1);
        assertTarget(state.dishes().get(0).targets().get(1), "SOHO", 21, 1640000, 1640000, 10.9);

        assertEquals(Instant.ofEpochMilli(1770497799000L), state.timestamp());
    }

    @Test
    void shouldDeserializeDishWithoutTarget() {
        State state = deserializer.deserialize(mockParserFromFile("dsn/dsnWithoutTarget.xml"), deserializationContext);

        assertStation(state.stations().get(0), "gdscc", "Goldstone", Instant.ofEpochMilli(1770497799000L), ZoneOffset.ofTotalSeconds(-28800));

        assertDish(state.dishes().get(0), "DSS14", 0, 90, null, false, false, false, "Antenna Unplanned Maintenance");
        assertTrue(state.dishes().get(0).targets().isEmpty());

        assertEquals(Instant.ofEpochMilli(1770497799000L), state.timestamp());
    }

    @Test
    void shouldDeserializeStationWithoutTimeOrTimeZone() {
        State state = deserializer.deserialize(mockParserFromFile("dsn/dsnWithoutTimeOrTimeZone.xml"), deserializationContext);

        assertStation(state.stations().get(0), "gdscc", "Goldstone", null, null);

        assertDish(state.dishes().get(0), "DSS14", 0, 90, null, false, false, false, "Antenna Unplanned Maintenance");
        assertTarget(state.dishes().get(0).targets().get(0), "DSS", 99, -1, -1, -1);

        assertEquals(Instant.ofEpochMilli(1770497799000L), state.timestamp());
    }

    @Test
    void shouldDeserializeStationWithoutFriendlyName() {
        State state = deserializer.deserialize(mockParserFromFile("dsn/dsnWithoutFriendlyName.xml"), deserializationContext);

        assertStation(state.stations().get(0), "gdscc", null, Instant.ofEpochMilli(1770497799000L), ZoneOffset.ofTotalSeconds(-28800));

        assertDish(state.dishes().get(0), "DSS14", 0, 90, null, false, false, false, "Antenna Unplanned Maintenance");

        assertEquals(Instant.ofEpochMilli(1770497799000L), state.timestamp());
    }

    @Test
    void shouldDeserializeDishWithoutDDOR() {
        State state = deserializer.deserialize(mockParserFromFile("dsn/dsnWithoutDDOR.xml"), deserializationContext);

        assertStation(state.stations().get(0), "gdscc", "Goldstone", Instant.ofEpochMilli(1770497799000L), ZoneOffset.ofTotalSeconds(-28800));

        assertDish(state.dishes().get(0), "DSS14", 0, 90, null, false, false, null, "Antenna Unplanned Maintenance");

        assertEquals(Instant.ofEpochMilli(1770497799000L), state.timestamp());
    }

    private static JsonParser mockParserFromFile(String filePath) {
        try {
            byte[] configBytes = StateDeserializer.class.getClassLoader().getResourceAsStream(filePath).readAllBytes();
            JsonParser jsonParser = mock(JsonParser.class);
            when(jsonParser.readValueAsTree()).thenReturn(MAPPER.readTree(configBytes));
            return jsonParser;
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

}