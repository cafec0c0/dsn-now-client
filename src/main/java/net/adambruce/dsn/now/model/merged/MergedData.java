package net.adambruce.dsn.now.model.merged;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.List;

/**
 * Merged data from the DSN configuration and state endpoints.
 */
@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class MergedData {
    /**
     * the DSN stations.
     * @return the DSN stations
     */
    private List<MergedStationData> stations;

    /**
     * the timestamp of the DSN state response
     * @return the timestamp of the DSN state response
     */
    private Instant timestamp;
}