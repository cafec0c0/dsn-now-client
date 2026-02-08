package net.adambruce.dsn.now.model.state;

/**
 * DSN spacecraft (target) information.
 *
 * @param name the name of the target
 * @param id the ID of the target
 * @param upLegRange the up leg range (m)
 * @param downLegRange the down leg range (m)
 * @param roundTripLightTime the Round Trip Light Time (RTLT) (s)
 */
public record Target(
        String name,
        Long id,
        Long upLegRange,
        Long downLegRange,
        Double roundTripLightTime
) { }
