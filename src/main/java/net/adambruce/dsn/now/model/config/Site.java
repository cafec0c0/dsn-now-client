package net.adambruce.dsn.now.model.config;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.Setter;

import java.util.List;

/**
 * DSN ground station information.
 */
@Data
public class Site {
        /**
         * the name of the ground station
         * @param name the name of the ground station
         * @return the name of the ground station
         */
        private String name;

        /**
         * the friendly name of the ground station
         * @param friendlyName the friendly name of the ground station
         * @return the friendly name of the ground station
         */
        private String friendlyName;

        /**
         * the longitude of the ground station
         * @param longitude the longitude of the ground station
         * @return the longitude of the ground station
         */
        private Double longitude;

        /**
         * the latitude of the ground station
         * @param latitude the latitude of the ground station
         * @return the latitude of the ground station
         */
        private Double latitude;

        /**
         * the dishes located at this ground station
         * @param dishes the dishes located at this ground station
         * @return the dishes located at this ground station
         */
        @Setter(onMethod_ = {
                @JacksonXmlElementWrapper(useWrapping = false),
                @JacksonXmlProperty(localName = "dish")
        })
        private List<Dish> dishes;
}