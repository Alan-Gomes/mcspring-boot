package dev.alangomes.springspigot.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Utility annotation to force method to be called in the Bukkit main thread.
 * All calls outside the main thread will be scheduled to the next server tick.
 *
 * If the method returns value, the value will be {@code null}.
 *
 * If the call originate from the main thread, it will be executed normally.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Synchronize {
}
