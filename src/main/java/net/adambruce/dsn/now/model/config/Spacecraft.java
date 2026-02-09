package net.adambruce.dsn.now.model.config;

import lombok.Data;

/**
 * DSN spacecraft information.
 */
@Data
public class Spacecraft {
    /**
     * the name of the spacecraft
     * @param name the name of the spacecraft
     * @return the name of the spacecraft
     */
    private String name;

    /**
     * the explorer name of the spacecraft
     * @param explorerName the explorer name of the spacecraft
     * @return the explorer name of the spacecraft
     */
    private String explorerName;

    /**
     * the friendly acronym of the spacecraft
     * @param friendlyAcronym the friendly acronym of the spacecraft
     * @return the friendly acronym of the spacecraft
     */
    private String friendlyAcronym;

    /**
     * the friendly name of the spacecraft
     * @param friendlyName the friendly name of the spacecraft
     * @return the friendly name of the spacecraft
     */
    private String friendlyName;

    /**
     * whether the spacecraft has a thumbnail (used by DSN Now webpage)
     * @param thumbnail whether the spacecraft has a thumbnail
     * @return true if the spacecraft has a thumbnail
     */
    private Boolean thumbnail;
}