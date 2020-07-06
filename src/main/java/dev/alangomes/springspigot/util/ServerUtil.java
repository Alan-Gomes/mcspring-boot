package dev.alangomes.springspigot.util;

import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Utilities to get sender id by the object and vice-versa, since storing the instance is not a good practice
 * (can cause memory issues if the sender quits and the object keep alive for too long)
 */
@Component
public class ServerUtil {

    private static final String CONSOLE_SENDER_ID = "*console*";

    @Autowired
    private Server server;

    /**
     * Get the most unique id available for the {@param sender}.
     * If the server is in online mode, it will return the {@param sender} UUID, otherwise will return the player username in lower case.
     *
     * @param sender the sender to get the id from
     * @return the sender id, null if null sender input
     */
    public String getSenderId(CommandSender sender) {
        if (sender == null) {
            return null;
        }
        if (!(sender instanceof OfflinePlayer)) {
            return CONSOLE_SENDER_ID;
        }
        val player = (OfflinePlayer) sender;
        return server.getOnlineMode() ? player.getUniqueId().toString() : StringUtils.lowerCase(player.getName());
    }

    /**
     * Return the {@link org.bukkit.command.CommandSender} associated to the {@param id}, normally used with {@link dev.alangomes.springspigot.util.ServerUtil#getSenderId}
     *
     * @param id the id of the sender
     * @return the sender associated with {@param id}, null if null id input
     */
    public CommandSender getSenderFromId(String id) {
        if (id == null) {
            return null;
        }
        if (CONSOLE_SENDER_ID.equals(id)) {
            return server.getConsoleSender();
        }
        if (id.length() <= 16) {
            return server.getPlayer(id);
        }
        return server.getPlayer(UUID.fromString(id));
    }

}
