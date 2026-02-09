package net.adambruce.dsn.now.model.config;

import lombok.Data;

/**
 * DSN dish information.
 */
@Data
public class Dish {
    /**
     * the name of the dish
     * @param name the name of the dish
     * @return the name of the dish
     */
    private String name;

    /**
     * the friendly name of the dish
     * @param friendlyName the friendly name of the dish
     * @return the friendly name of the dish
     */
    private String friendlyName;

    /**
     * the type of dish
     * @param type the type of the dish
     * @return the type of the dish
     */
    private String type;
}