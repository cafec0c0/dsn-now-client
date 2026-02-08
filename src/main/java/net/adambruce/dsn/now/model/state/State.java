package net.adambruce.dsn.now.model.state;

import java.time.Instant;
import java.util.List;

/**
 * DSN state.
 *
 * @param stations the DSN stations
 * @param dishes the DSN dishes
 * @param timestamp the timestamp of the response
 */
public record State(
        List<Station> stations,
        List<Dish> dishes,
        Instant timestamp
) { }
