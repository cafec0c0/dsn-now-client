package net.adambruce.dsn.now.model.config;

import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

/**
 * DSN Now configuration.
 * This class holds a list of Deep Space ground stations (sites) and a list of spacecraft tracked by the Deep Space
 * Network.
 *
 * @param sites the DSN sites
 * @param spacecraft the spacecraft tracked by the DSN
 */
public record Configuration(
        List<Site> sites,

        @JacksonXmlProperty(localName = "spacecraftMap")
        List<Spacecraft> spacecraft
) { }
