package dev.alangomes.springspigot.context;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Scope("singleton")
public class ServerContext {

    private final Map<Long, CommandSender> senderRefs = new ConcurrentHashMap<>();

    public void setSender(CommandSender sender) {
        long threadId = Thread.currentThread().getId();
        if (sender == null) {
            senderRefs.remove(threadId);
            return;
        }
        senderRefs.put(threadId, sender);
    }

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
    Player playerBean() {
        CommandSender sender = getSender();
        return sender instanceof Player ? (Player) sender : null;
    }

    @Bean
    @Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
    Server serverBean() {
        return Bukkit.getServer();
    }

}
