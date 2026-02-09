package net.adambruce.dsn.now.model.state;

import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * DSN state information.
 */
@Data
public class State {

    /**
     * the DSN stations
     * @return the DSN stations
     */
    private final List<Station> stations = new ArrayList<>();

    /**
     * the DSN dishes
     * @return the DSN dishes
     */
    private final List<Dish> dishes = new ArrayList<>();

    /**
     * the timestamp of the DSN state response
     * @param timestamp the timestamp of the DSN state response
     * @return the timestamp of the DSN state response
     */
    private Instant timestamp;

    /**
     * the DSN stations. This method will append to the list.
     * @param station the DSN station
     */
    public void setStation(Station station) {
        this.stations.add(station);
    }

    /**
     * the DSN dishes. This method will append to the list.
     * @param dish the DSN dish
     */
    public void setDish(Dish dish) {
        this.dishes.add(dish);
    }
}
