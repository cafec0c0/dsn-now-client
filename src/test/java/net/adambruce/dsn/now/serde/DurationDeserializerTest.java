package net.adambruce.dsn.now.serde;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DurationDeserializerTest {


    @Mock
    DeserializationContext deserializationContext;

    @Mock
    JsonParser jsonParser;

    private final DurationDeserializer durationDeserializer = new DurationDeserializer();

    @Test
    void shouldDeserializeDurationFromDouble() throws IOException {
        when(jsonParser.readValueAs(Double.class)).thenReturn(2360D);

        Duration result = durationDeserializer.deserialize(jsonParser, deserializationContext);

        assertEquals(Duration.ofSeconds(2360), result);
    }

    @Test
    void shouldDeserializeDurationFromNegativeDouble() throws IOException {
        when(jsonParser.readValueAs(Double.class)).thenReturn(-1D);

        Duration result = durationDeserializer.deserialize(jsonParser, deserializationContext);

        assertNull(result);
    }

    @Test
    void shouldDeserializeZoneOffsetFromNullValue() throws IOException {
        when(jsonParser.readValueAs(Double.class)).thenReturn(null);

        Duration result = durationDeserializer.deserialize(jsonParser, deserializationContext);

        assertNull(result);
    }

}