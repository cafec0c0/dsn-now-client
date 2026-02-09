package net.adambruce.dsn.now.model.config;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.Setter;

import java.util.List;

/**
 * DSN Now configuration.
 * This class holds a list of Deep Space ground stations (sites) and a list of spacecraft tracked by the Deep Space
 * Network.
 */
@Data
public class Configuration {
        /**
         * the DSN ground stations
         * @param sites the list of ground stations
         * @return the list of ground stations
         */
        private List<Site> sites;

        /**
         * the spacecraft tracked by the DSN
         * @param spacecraft the list of spacecraft
         * @return the list of spacecraft
         */
        @Setter(onMethod_ = @JacksonXmlProperty(localName = "spacecraftMap"))
        private List<Spacecraft> spacecraft;
}