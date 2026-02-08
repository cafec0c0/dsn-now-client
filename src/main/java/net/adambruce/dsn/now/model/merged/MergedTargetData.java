package net.adambruce.dsn.now.model.merged;

/**
 * Merged target / spacecraft information from the DSN configuration and state endpoints.
 *
 * @param name the name of the spacecraft
 * @param id the ID of the target
 * @param upLegRange the up leg range (m)
 * @param downLegRange the down leg range (m)
 * @param roundTripLightTime the Round Trip Light Time (RTLT) (s)
 * @param explorerName the explorer name of the spacecraft
 * @param friendlyAcronym the friendly acronym of the spacecraft
 * @param friendlyName the friendly name of the spacecraft
 * @param thumbnail whether the spacecraft has a thumbnail (used by DSN Now webpage)
 */
public record MergedTargetData(
        String name,
        Long id,
        Long upLegRange,
        Long downLegRange,
        Double roundTripLightTime,
        String explorerName,
        String friendlyAcronym,
        String friendlyName,
        Boolean thumbnail
) { }
