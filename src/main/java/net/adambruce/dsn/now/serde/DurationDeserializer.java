package net.adambruce.dsn.now.serde;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.Duration;

public class DurationDeserializer extends StdDeserializer<Duration> {

    public DurationDeserializer() {
        super(Duration.class);
    }

    @Override
    public Duration deserialize(JsonParser p, DeserializationContext deserializationContext) throws IOException {
        Double time = p.readValueAs(Double.class);
        return (time != null) && time >= 0
                ? Duration.ofSeconds(time.longValue())
                : null;
    }
}
