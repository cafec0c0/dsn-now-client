package net.adambruce.dsn.now.model.state;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.Setter;

import java.time.Instant;
import java.time.ZoneOffset;

/**
 * DSN station information.
 */
@Data
public class Station {
    /**
     * the name of the ground station
     * @param name the name of the ground station
     * @return the name of the ground station
     */
    private String name;

    /**
     * the friendly name of the ground station
     * @param friendlyName the friendly name of the ground station
     * @return the friendly name of the ground station
     */
    private String friendlyName;

    /**
     * the time at the ground station
     * @param time the time at the ground station
     * @return the time at the ground station
     */
    @Setter(onMethod_ = @JacksonXmlProperty(localName = "timeUTC"))
    private Instant time;

    /**
     * the timezone offset relative to UTC
     * @param timeZoneOffset the timezone offset relative to UTC
     * @return the timezone offset relative to UTC
     */
    private ZoneOffset timeZoneOffset;
}
