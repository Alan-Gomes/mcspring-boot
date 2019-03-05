package dev.alangomes.springspigot.context;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Scope("singleton")
public class ServerContext {

    @Value("${spigot.plugin}")
    private String pluginName;

    private final Map<Long, CommandSender> senderRefs = new ConcurrentHashMap<>();

    /**
     * Set the current sender in the thread context.
     * This is necessary to get the sender via dependency injection.
     *
     * Make sure the sender is set to null in the same context at the end.
     *
     * @param sender The new {@link org.bukkit.command.CommandSender} of the context.
     */
    public void setSender(CommandSender sender) {
        long threadId = Thread.currentThread().getId();
        if (sender == null) {
            senderRefs.remove(threadId);
            return;
        }
        senderRefs.put(threadId, sender);
    }

    /**
     * Utility method to retrieve the current {@param sender} of the context.
     * It is recommended to get this value via dependency injection.
     *
     * @return The current sender of the context.
     */
    public CommandSender getSender() {
        return senderRefs.get(Thread.currentThread().getId());
    }

    @Bean
    @Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
    CommandSender senderBean() {
        return getSender();
    }

    @Bean
    @Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
    Player playerBean(CommandSender sender) {
        return sender instanceof Player ? (Player) sender : null;
    }

    @Bean
    Server serverBean() {
        return Bukkit.getServer();
    }

    @Bean
    Plugin pluginBean(Server server) {
        return server.getPluginManager().getPlugin(pluginName);
    }

    @Bean
    BukkitScheduler schedulerBean(Server server) {
        return server.getScheduler();
    }

}
