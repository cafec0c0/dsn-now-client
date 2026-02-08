package net.adambruce.dsn.now.model.merged;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

/**
 * Merged ground station information from the DSN configuration and state endpoints.
 *
 * @param name the name of the ground station
 * @param friendlyName the friendly name of the ground station
 * @param longitude the longitude of the ground station
 * @param latitude the latitude of the ground station
 * @param time the local time of the ground station
 * @param timeZoneOffset the timezone offset relative to UTC
 * @param dishes the dishes located at this ground station
 */
public record MergedStationData(
        String name,
        String friendlyName,
        Double longitude,
        Double latitude,
        Instant time,
        ZoneOffset timeZoneOffset,
        List<MergedDishData> dishes
) { }
