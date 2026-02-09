package net.adambruce.dsn.now.serde;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.ZoneOffset;

/**
 * Deserializes ZoneOffest from milliseconds
 */
public class ZoneOffsetDeserializer extends StdDeserializer<ZoneOffset> {

    public ZoneOffsetDeserializer() {
        super(ZoneOffset.class);
    }

    @Override
    public ZoneOffset deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        Double zoneOffset = p.readValueAs(Double.class);
        return zoneOffset != null
                ? ZoneOffset.ofTotalSeconds(((int)((double)zoneOffset)) / 1000)
                : null;
    }
}
