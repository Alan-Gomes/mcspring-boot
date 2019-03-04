package dev.alangomes.mcspring.hook;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Getter
@Component
@Scope("singleton")
public class ServerContext {

    private Player player;

    void setPlayer(Player player) {
        if (!Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("Invalid context");
        }
        this.player = player;
    }

    @Bean
    @Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
    Player playerBean() {
        return player;
    }

    @Bean
    @Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
    Server serverBean() {
        return Bukkit.getServer();
    }

}
