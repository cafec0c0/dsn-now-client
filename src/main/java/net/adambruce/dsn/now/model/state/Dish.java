package net.adambruce.dsn.now.model.state;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * DSN dish information.
 */
@Data
public class Dish {
    /**
     * the name of the dish
     * @param name the name of the dish
     * @return the name of the dish
     */
    private String name;

    /**
     * the azimuth of the dish (degrees)
     * @param azimuth the azimuth of the dish
     * @return the azimuth of the dish
     */
    @Setter(onMethod_ = @JacksonXmlProperty(localName = "azimuthAngle"))
    private Long azimuth;

    /**
     * the elevation of the dish (degrees)
     * @param elevation the elevation of the dish
     * @return the elevation of the dish
     */
    @Setter(onMethod_ = @JacksonXmlProperty(localName = "elevationAngle"))
    private Long elevation;

    /**
     * the wind speed at the dish (km/hr)
     * @param windSpeed the wind speed at the dish (km/hr)
     * @return the wind speed at the dish (km/hr)
     */
    private Long windSpeed;

    /**
     * the dish is configured for Multiple Spacecraft Per Aperture (MSPA)
     * @param multipleSpacecraftPerAperture whether the dish is configured for MSPA
     * @return whether the dish is configured for MSPA
     */
    @Setter(onMethod_ = @JacksonXmlProperty(localName = "isMSPA"))
    private Boolean multipleSpacecraftPerAperture;

    /**
     * the dish is configured as an array
     * @param array whether the dish is configured as an array
     * @return whether the dish is configured as an array
     */
    @Setter(onMethod_ = @JacksonXmlProperty(localName = "isArray"))
    private Boolean array;

    /**
     * the dish is configured for Delta-Differential One-Way Ranging (DDOR)
     * @param deltaDifferentialOneWayRanging whether the dish is configured for DDOR
     * @return whether the dish is configured for DDOR
     */
    @Setter(onMethod_ = @JacksonXmlProperty(localName = "isDDOR"))
    private Boolean deltaDifferentialOneWayRanging;

    /**
     * the activity of the dish
     * @param activity the activity of the dish
     * @return the activity of the dish
     */
    private String activity;

    /**
     * the uplink signals transmitting from the dish
     * @return the uplink signals transmitting from the dish
     */
    private final List<Signal> upSignals = new ArrayList<>();

    /**
     * the downlink signals received by the dish
     * @return the downlink signals transmitting from the dish
     */
    private final List<Signal> downSignals = new ArrayList<>();

    /**
     * the targets that the dish is tracking
     * @return the targets that the dish is tracking
     */
    private final List<Target> targets = new ArrayList<>();

    /**
     * the uplink signals transmitting from the dish. This method will append to the list.
     * @param upSignal the uplink signal transmitting from the dish
     */
    public void setUpSignal(Signal upSignal) {
        this.upSignals.add(upSignal);
    }

    /**
     * the downlink signals received by the dish. This method will append to the list.
     * @param downSignal the downlink signal received by the dish
     */
    public void setDownSignal(Signal downSignal) {
        this.downSignals.add(downSignal);
    }

    /**
     * the target that the dish is tracking. This method will append to the list.
     * @param target the target that the dish is tracking
     */
    public void setTarget(Target target) {
        this.targets.add(target);
    }
}
