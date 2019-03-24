package dev.alangomes.springspigot.util;

import lombok.val;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

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
        if (!(sender instanceof Player)) {
            return CONSOLE_SENDER_ID;
        }
        val player = (Player) sender;
        return server.getOnlineMode() ? player.getUniqueId().toString() : player.getName().toLowerCase();
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
