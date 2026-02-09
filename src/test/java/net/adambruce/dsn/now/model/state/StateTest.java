package net.adambruce.dsn.now.model.state;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class StateTest {

    @Test
    void shouldAppendStationToList() {
        Station station1 = mock(Station.class);
        Station station2 = mock(Station.class);

        State state = new State();
        state.setStation(station1);
        state.setStation(station2);

        assertEquals(station1, state.getStations().get(0));
        assertEquals(station2, state.getStations().get(1));
    }

    @Test
    void shouldAppendDishToList() {
        Dish dish1 = mock(Dish.class);
        Dish dish2 = mock(Dish.class);

        State state = new State();
        state.setDish(dish1);
        state.setDish(dish2);

        assertEquals(dish1, state.getDishes().get(0));
        assertEquals(dish2, state.getDishes().get(1));
    }

}
