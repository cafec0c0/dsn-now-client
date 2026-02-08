package net.adambruce.dsn.now.model.merged;

import net.adambruce.dsn.now.model.state.Signal;

import java.util.List;

/**
 * Merged dish information from the DSN configuration and state endpoints.
 *
 * @param name the name of the dish
 * @param friendlyName the friendly name of the dish
 * @param type the type of dish
 * @param azimuthAngle the azimuth of the dish (degrees)
 * @param elevationAngle the elevation of the dish (degrees)
 * @param windSpeed the wind speed at the dish (km/hr)
 * @param multipleSpacecraftForAperture the dish is configured for Multiple Spacecraft Per Aperture (MSPA)
 * @param array the dish is configured as an array
 * @param deltaDifferentialOneWayRanging the dish is configured for Delta-Differential One-Way Ranging (DDOR)
 * @param activity the activity of the dish
 * @param upSignals the uplink signals transmitting from the dish
 * @param downSignals the downlink signals received by the dish
 * @param targets the targets that the dish is tracking
 */
public record MergedDishData(
        String name,
        String friendlyName,
        String type,
        Long azimuthAngle,
        Long elevationAngle,
        Long windSpeed,
        Boolean multipleSpacecraftForAperture,
        Boolean array,
        Boolean deltaDifferentialOneWayRanging,
        String activity,
        List<Signal> upSignals,
        List<Signal> downSignals,
        List<MergedTargetData> targets
) { }
