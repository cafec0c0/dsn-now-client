package net.adambruce.dsn.now.model.merged;

import java.time.Instant;
import java.util.List;

/**
 * Merged data from the DSN configuration and state endpoints.
 *
 * @param stations the DSN stations
 * @param timestamp the timestamp of the DSN state response
 */
public record MergedDeepSpaceNetworkData(
        List<MergedStationData> stations,
        Instant timestamp
) { }
