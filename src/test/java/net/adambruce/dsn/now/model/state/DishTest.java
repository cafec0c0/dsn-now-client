package net.adambruce.dsn.now.model.state;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class DishTest {

    @Test
    void shouldAppendUpSignalToList() {
        Signal signal1 = mock(Signal.class);
        Signal signal2 = mock(Signal.class);

        Dish dish = new Dish();
        dish.setUpSignal(signal1);
        dish.setUpSignal(signal2);

        assertEquals(signal1, dish.getUpSignals().get(0));
        assertEquals(signal2, dish.getUpSignals().get(1));
    }

    @Test
    void shouldAppendDownSignalToList() {
        Signal signal1 = mock(Signal.class);
        Signal signal2 = mock(Signal.class);

        Dish dish = new Dish();
        dish.setDownSignal(signal1);
        dish.setDownSignal(signal2);

        assertEquals(signal1, dish.getDownSignals().get(0));
        assertEquals(signal2, dish.getDownSignals().get(1));
    }

    @Test
    void shouldAppendTargetToList() {
        Target target1 = mock(Target.class);
        Target target2 = mock(Target.class);

        Dish dish = new Dish();
        dish.setTarget(target1);
        dish.setTarget(target2);

        assertEquals(target1, dish.getTargets().get(0));
        assertEquals(target2, dish.getTargets().get(1));
    }

}
