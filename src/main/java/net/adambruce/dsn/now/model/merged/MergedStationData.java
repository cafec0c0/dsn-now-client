package net.adambruce.dsn.now.model.merged;

import lombok.Value;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

/**
 * Merged ground station information from the DSN configuration and state endpoints.
 */
@Value
public class MergedStationData {
    /**
     * the name of the ground station
     * @return the name of the ground station
     */
    String name;

    /**
     * the friendly name of the ground station
     * @return the friendly name of the ground station
     */
    String friendlyName;

    /**
     * the longitude of the ground station
     * @return the longitude of the ground station
     */
    Double longitude;

    /**
     * the latitude of the ground station
     * @return the latitude of the ground station
     */
    Double latitude;

    /**
     * the time at the ground station
     * @return the time at the ground station
     */
    Instant time;

    /**
     * the timezone offset relative to UTC
     * @return the timezone offset relative to UTC
     */
    ZoneOffset timeZoneOffset;

    /**
     * the dishes located at this ground station
     * @return the dishes located at this ground station
     */
    List<MergedDishData> dishes;
}