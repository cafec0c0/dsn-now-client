package net.adambruce.dsn.now.model.merged;

import lombok.Value;

import java.time.Instant;
import java.util.List;

/**
 * Merged data from the DSN configuration and state endpoints.
 */
@Value
public class MergedData {
    /**
     * the DSN stations.
     * @return the DSN stations
     */
    List<MergedStationData> stations;

    /**
     * the timestamp of the DSN state response
     * @return the timestamp of the DSN state response
     */
    Instant timestamp;
}