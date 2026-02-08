package net.adambruce.dsn.now.model.config;

/**
 * DSN spacecraft information.
 *
 * @param name the name of the spacecraft
 * @param explorerName the explorer name of the spacecraft
 * @param friendlyAcronym the friendly acronym of the spacecraft
 * @param friendlyName the friendly name of the spacecraft
 * @param thumbnail whether the spacecraft has a thumbnail (used by DSN Now webpage)
 */
public record Spacecraft(
    String name,
    String explorerName,
    String friendlyAcronym,
    String friendlyName,
    Boolean thumbnail
) { }
