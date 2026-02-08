package net.adambruce.dsn.now.model.state;

import java.util.List;

/**
 * DSN dish information.
 *
 * @param name the name of the dish
 * @param azimuthAngle the azimuth of the dish (degrees)
 * @param elevationAngle the elevation of the dish (degrees)
 * @param windSpeed the wind speed at the dish (km/hr)
 * @param multipleSpacecraftPerAperture the dish is configured for Multiple Spacecraft Per Aperture (MSPA)
 * @param array the dish is configured as an array
 * @param deltaDifferentialOneWayRanging the dish is configured for Delta-Differential One-Way Ranging (DDOR)
 * @param activity the activity of the dish
 * @param upSignals the uplink signals transmitting from the dish
 * @param downSignals the downlink signals received by the dish
 * @param targets the targets that the dish is tracking
 */
public record Dish(
        String name,
        Long azimuthAngle,
        Long elevationAngle,
        Long windSpeed,
        Boolean multipleSpacecraftPerAperture,
        Boolean array,
        Boolean deltaDifferentialOneWayRanging,
        String activity,
        List<Signal> upSignals,
        List<Signal> downSignals,
        List<Target> targets
) { }
