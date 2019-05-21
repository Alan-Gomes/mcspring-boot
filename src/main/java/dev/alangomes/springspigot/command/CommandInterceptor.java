package dev.alangomes.springspigot.command;

import dev.alangomes.springspigot.context.Context;
import lombok.val;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

@Component
@ConditionalOnBean(annotation = CommandLine.Command.class)
class CommandInterceptor implements Listener {

    @Autowired
    private Context context;

    @Autowired
    private CommandExecutor commandExecutor;

    @Autowired
    private CommandService commandService;

    @EventHandler
    void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        if (event.isCancelled() || commandService.isRegistered()) return;
        val player = context.getPlayer();
        val result = commandExecutor.execute(event.getMessage().substring(1).split(" "));
        event.setCancelled(result.isExists());
        result.getOutput().forEach(player::sendMessage);
    }

    @EventHandler
    void onServerCommand(ServerCommandEvent event) {
        if (event.isCancelled() || commandService.isRegistered()) return;
        val sender = context.getSender();
        val result = commandExecutor.execute(event.getCommand().split(" "));
        event.setCancelled(result.isExists());
        result.getOutput().forEach(sender::sendMessage);
    }

}
