package net.adambruce.dsn.now.model.config;

import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

/**
 * DSN ground station information.
 *
 * @param name the name of the ground station
 * @param friendlyName the friendly name of the ground station
 * @param longitude the longitude of the ground station
 * @param latitude the latitude of the ground station
 * @param dishes the dishes located at this ground station
 */
public record Site(
        String name,
        String friendlyName,
        Double longitude,
        Double latitude,

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "dish")
        List<Dish> dishes
) { }
