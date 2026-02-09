package net.adambruce.dsn.now.model.merged;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

/**
 * Merged ground station information from the DSN configuration and state endpoints.
 */
@Getter
@AllArgsConstructor
public class MergedStationData {
    /**
     * the name of the ground station
     * @return the name of the ground station
     */
    private String name;

    /**
     * the friendly name of the ground station
     * @return the friendly name of the ground station
     */
    private String friendlyName;

    /**
     * the longitude of the ground station
     * @return the longitude of the ground station
     */
    private Double longitude;

    /**
     * the latitude of the ground station
     * @return the latitude of the ground station
     */
    private Double latitude;

    /**
     * the time at the ground station
     * @return the time at the ground station
     */
    private Instant time;

    /**
     * the timezone offset relative to UTC
     * @return the timezone offset relative to UTC
     */
    private ZoneOffset timeZoneOffset;

    /**
     * the dishes located at this ground station
     * @return the dishes located at this ground station
     */
    private List<MergedDishData> dishes;
}