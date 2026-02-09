package net.adambruce.dsn.now.model.merged;

import lombok.Value;
import net.adambruce.dsn.now.model.state.Signal;

import java.util.List;

/**
 * Merged dish information from the DSN configuration and state endpoints.
 */
@Value
public class MergedDishData {
    /**
     * the name of the dish
     * @return the name of the dish
     */
    String name;

    /**
     * the friendly name of the dish
     * @return the friendly name of the dish
     */
    String friendlyName;

    /**
     * the type of dish
     * @return the type of dish
     */
    String type;

    /**
     * the azimuth of the dish (degrees)
     * @return the azimuth of the dish (degrees)
     */
    Long azimuth;

    /**
     * the elevation of the dish (degrees)
     * @return the elevation of the dish (degrees)
     */
    Long elevation;

    /**
     * the wind speed at the dish (km/hr)
     * @return the wind speed at the dish (km/hr)
     */
    Long windSpeed;

    /**
     * the dish is configured for Multiple Spacecraft Per Aperture (MSPA)
     * @return whether the dish is configured for MSPA
     */
    Boolean multipleSpacecraftPerAperture;

    /**
     * the dish is configured as an array
     * @return whether the dish is configured as an array
     */
    Boolean array;

    /**
     * the dish is configured for Delta-Differential One-Way Ranging (DDOR)
     * @return whether the dish is configured for DDOR
     */
    Boolean deltaDifferentialOneWayRanging;

    /**
     * the activity of the dish
     * @return the activity of the dish
     */
    String activity;

    /**
     * the uplink signals transmitting from the dish
     * @return the uplink signals transmitting from the dish
     */
    List<Signal> upSignals;

    /**
     * the downlink signals received by the dish
     * @return the downlink signals received by the dish
     */
    List<Signal> downSignals;

    /**
     * the targets that the dish is tracking
     * @return the targets that the dish is tracking
     */
    List<MergedTargetData> target;
}