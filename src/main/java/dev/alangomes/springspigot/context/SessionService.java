package dev.alangomes.springspigot.context;

import org.bukkit.command.CommandSender;

import java.util.Map;

/**
 * Service that provides a key-value storage for each sender.
 */
public interface SessionService {

    /**
     * Return the session of the current sender in the context
     * {@link dev.alangomes.springspigot.context.Context}
     *
     * @return the session of the sender in the context
     */
    Map<String, Object> current();

    /**
     * Return the current session of {@param sender}
     *
     * @param sender the {@link org.bukkit.command.CommandSender sender} to get the session from
     * @return the session of {@param sender}
     */
    Map<String, Object> of(CommandSender sender);

}
