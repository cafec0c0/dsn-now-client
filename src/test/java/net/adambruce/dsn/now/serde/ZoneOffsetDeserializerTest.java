package net.adambruce.dsn.now.serde;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ZoneOffsetDeserializerTest {


    @Mock
    DeserializationContext deserializationContext;

    @Mock
    JsonParser jsonParser;

    private final ZoneOffsetDeserializer zoneOffsetDeserializer = new ZoneOffsetDeserializer();

    @Test
    void shouldDeserializeZoneOffsetFromDouble() throws IOException {
        when(jsonParser.readValueAs(Double.class)).thenReturn(28800000.0);

        ZoneOffset result = zoneOffsetDeserializer.deserialize(jsonParser, deserializationContext);

        assertEquals(ZoneOffset.ofHours(8), result);
    }

    @Test
    void shouldDeserializeZoneOffsetFromNegativeDouble() throws IOException {
        when(jsonParser.readValueAs(Double.class)).thenReturn(-28800000.0);

        ZoneOffset result = zoneOffsetDeserializer.deserialize(jsonParser, deserializationContext);

        assertEquals(ZoneOffset.ofHours(-8), result);
    }

    @Test
    void shouldDeserializeZoneOffsetFromNullValue() throws IOException {
        when(jsonParser.readValueAs(Double.class)).thenReturn(null);

        ZoneOffset result = zoneOffsetDeserializer.deserialize(jsonParser, deserializationContext);

        assertNull(result);
    }

}