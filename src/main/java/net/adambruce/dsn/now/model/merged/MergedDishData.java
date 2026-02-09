package net.adambruce.dsn.now.model.merged;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.adambruce.dsn.now.model.state.Signal;

import java.util.List;

/**
 * Merged dish information from the DSN configuration and state endpoints.
 */
@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class MergedDishData {
    /**
     * the name of the dish
     * @return the name of the dish
     */
    private String name;

    /**
     * the friendly name of the dish
     * @return the friendly name of the dish
     */
    private String friendlyName;

    /**
     * the type of dish
     * @return the type of dish
     */
    private String type;

    /**
     * the azimuth of the dish (degrees)
     * @return the azimuth of the dish (degrees)
     */
    private Long azimuth;

    /**
     * the elevation of the dish (degrees)
     * @return the elevation of the dish (degrees)
     */
    private Long elevation;

    /**
     * the wind speed at the dish (km/hr)
     * @return the wind speed at the dish (km/hr)
     */
    private Long windSpeed;

    /**
     * the dish is configured for Multiple Spacecraft Per Aperture (MSPA)
     * @return whether the dish is configured for MSPA
     */
    private Boolean multipleSpacecraftPerAperture;

    /**
     * the dish is configured as an array
     * @return whether the dish is configured as an array
     */
    private Boolean array;

    /**
     * the dish is configured for Delta-Differential One-Way Ranging (DDOR)
     * @return whether the dish is configured for DDOR
     */
    private Boolean deltaDifferentialOneWayRanging;

    /**
     * the activity of the dish
     * @return the activity of the dish
     */
    private String activity;

    /**
     * the uplink signals transmitting from the dish
     * @return the uplink signals transmitting from the dish
     */
    private List<Signal> upSignals;

    /**
     * the downlink signals received by the dish
     * @return the downlink signals received by the dish
     */
    private List<Signal> downSignals;

    /**
     * the targets that the dish is tracking
     * @return the targets that the dish is tracking
     */
    private List<MergedTargetData> target;
}