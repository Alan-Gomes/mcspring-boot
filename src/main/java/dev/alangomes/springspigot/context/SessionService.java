package dev.alangomes.springspigot.context;

import java.util.Map;

/**
 * Service that provides a key-value storage for the current player in the context.
 * {@link dev.alangomes.springspigot.context.Context}
 */
public interface SessionService extends Map<String, Object> {

}
