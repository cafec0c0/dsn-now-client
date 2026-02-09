package net.adambruce.dsn.now.model.merged;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Merged target / spacecraft information from the DSN configuration and state endpoints.
 */
@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class MergedTargetData {
    /**
     * the name of the spacecraft
     * @return the name of the spacecraft
     */
    private String name;

    /**
     * the ID of the target
     * @return the ID of the target
     */
    private Long id;

    /**
     * the up leg range (m)
     * @return the up leg range (m)
     */
    private Long upLegRange;

    /**
     * the down leg range (m)
     * @return the down leg range (m)
     */
    private Long downLegRange;

    /**
     * the Round Trip Light Time (RTLT) (s)
     * @return the RTLT (s)
     */
    private Double roundTripLightTime;

    /**
     * the explorer name of the spacecraft
     * @return the explorer name of the spacecraft
     */
    private String explorerName;

    /**
     * the friendly acronym of the spacecraft
     * @return the friendly acronym of the spacecraft
     */
    private String friendlyAcronym;

    /**
     * the friendly name of the spacecraft
     * @return the friendly name of the spacecraft
     */
    private String friendlyName;

    /**
     * whether the spacecraft has a thumbnail (used by DSN Now webpage)
     * @return true if the spacecraft has a thumbnail
     */
    private Boolean thumbnail;
}