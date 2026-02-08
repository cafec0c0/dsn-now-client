package net.adambruce.dsn.now.model.state;

import java.time.Instant;
import java.time.ZoneOffset;

/**
 * DSN ground station information.
 *
 * @param name the name of the ground station
 * @param friendlyName the friendly name of the ground station
 * @param time the local time of the ground station
 * @param timeZoneOffset the timezone offset relative to UTC
 */
public record Station(
    String name,
    String friendlyName,
    Instant time,
    ZoneOffset timeZoneOffset
) { }
