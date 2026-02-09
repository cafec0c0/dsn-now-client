package net.adambruce.dsn.now.model.merged;

import lombok.Value;

import java.time.Duration;

/**
 * Merged target / spacecraft information from the DSN configuration and state endpoints.
 */
@Value
public class MergedTargetData {
    /**
     * the name of the spacecraft
     * @return the name of the spacecraft
     */
    String name;

    /**
     * the ID of the target
     * @return the ID of the target
     */
    Long id;

    /**
     * the up leg range (m)
     * @return the up leg range (m)
     */
    Long upLegRange;

    /**
     * the down leg range (m)
     * @return the down leg range (m)
     */
    Long downLegRange;

    /**
     * the Round Trip Light Time (RTLT)
     * @return the RTLT
     */
    Duration roundTripLightTime;

    /**
     * the explorer name of the spacecraft
     * @return the explorer name of the spacecraft
     */
    String explorerName;

    /**
     * the friendly acronym of the spacecraft
     * @return the friendly acronym of the spacecraft
     */
    String friendlyAcronym;

    /**
     * the friendly name of the spacecraft
     * @return the friendly name of the spacecraft
     */
    String friendlyName;

    /**
     * whether the spacecraft has a thumbnail (used by DSN Now webpage)
     * @return true if the spacecraft has a thumbnail
     */
    Boolean thumbnail;
}