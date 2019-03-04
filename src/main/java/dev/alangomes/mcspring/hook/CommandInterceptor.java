package dev.alangomes.mcspring.hook;

import dev.alangomes.mcspring.picocli.CommandLineDefinition;
import lombok.SneakyThrows;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

import java.util.List;
import java.util.concurrent.Callable;

@Component
@Scope("singleton")
class CommandInterceptor implements Listener {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private CommandLineDefinition cli;

    @Autowired
    private ServerContext serverContext;

    @EventHandler
    @SneakyThrows
    private void onCommand(PlayerCommandPreprocessEvent event) {
        serverContext.setPlayer(event.getPlayer());
        try {
            List<CommandLine> commands;
            commands = cli.build(context).parse(event.getMessage().substring(1).split(" "));
            if (!commands.isEmpty()) {
                event.setCancelled(true);
            }
            for (CommandLine commandLine : commands) {
                Object command = commandLine.getCommand();

                if (command instanceof Runnable) {
                    ((Runnable) command).run();
                } else if (command instanceof Callable) {
                    ((Callable) command).call();
                }
            }
        } catch (CommandLine.UnmatchedArgumentException ignored) {
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        serverContext.setPlayer(null);
    }

}
