package dev.alangomes.springspigot.context;

import java.util.function.Function;

/**
 * Service that provides a key-value storage for the current player in the context.
 * {@link dev.alangomes.springspigot.context.Context}
 */
public interface SessionService {

    /**
     * Set a value in the player session
     *
     * @param key   key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     */
    void set(String key, Object value);

    /**
     * Returns the value to which the specified key is set, or null if the session contains no mapping for the key
     *
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or null if the session contains no mapping for the key
     */
    Object get(String key);

    /**
     * Clear the current session of the player.
     * Note: This method does not destroy all sessions, only the session of the player in context.
     */
    void clear();

    /**
     * If the specified key is not already associated with a value (or is mapped to null),
     * attempts to compute its value using the given mapping function and enters it into the session unless null.
     *
     * @param key      key with which the specified value is to be associated
     * @param function the function to compute a value
     * @return the current (existing or computed) value associated with the specified key, or null if the computed value is null
     */
    default <V> V computeIfAbsent(String key, Function<String, ? extends V> function) {
        Object value = get(key);
        if (value == null) {
            V computed = function.apply(key);
            if (computed != null) {
                set(key, computed);
            }
            return computed;
        }
        return (V) value;
    }

}
