package net.adambruce.dsn.now.model.config;

/**
 * DSN dish information.
 *
 * @param name the name of the dish
 * @param friendlyName the friendly name of the dish
 * @param type the type of dish
 */
public record Dish(
    String name,
    String friendlyName,
    String type
) { }
