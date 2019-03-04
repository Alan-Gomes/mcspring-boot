package dev.alangomes.mcspring.hook;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicReference;

@Getter
@Component
@Scope("singleton")
public class ServerContext {

    private AtomicReference<CommandSender> senderRef = new AtomicReference<>();

    void setSender(CommandSender sender) {
        if (!Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("Invalid context");
        }
        senderRef.set(sender);
    }

    @Bean
    @Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
    CommandSender senderBean() {
        return senderRef.get();
    }

    @Bean
    @Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
    Player playerBean() {
        CommandSender sender = senderRef.get();
        return sender instanceof Player ? (Player) sender : null;
    }

    @Bean
    @Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
    Server serverBean() {
        return Bukkit.getServer();
    }

}
