package dev.alangomes.springspigot;

import dev.alangomes.springspigot.context.ServerContext;
import dev.alangomes.springspigot.picocli.CommandLineDefinition;
import lombok.AccessLevel;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

import java.util.List;
import java.util.concurrent.Callable;

@Component
@ConditionalOnClass(Bukkit.class)
class CommandInterceptor implements Listener {

    @Setter(AccessLevel.PACKAGE)
    @Value("${spigot.messages.command_error}")
    private String commandErrorMessage;

    @Setter(AccessLevel.PACKAGE)
    @Value("${spigot.messages.missing_parameter_error}")
    private String missingParameterErrorMessage;

    @Setter(AccessLevel.PACKAGE)
    @Value("${spigot.messages.parameter_error}")
    private String parameterErrorMessage;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private CommandLineDefinition cli;

    @Autowired
    private ServerContext serverContext;

    private final Logger logger = LoggerFactory.getLogger(CommandInterceptor.class);

    @EventHandler
    void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        serverContext.setSender(player);
        event.setCancelled(runCommand(player, event.getMessage().substring(1)));
        serverContext.setSender(null);
    }

    @EventHandler
    void onServerCommand(ServerCommandEvent event) {
        if (event.isCancelled()) return;
        CommandSender sender = event.getSender();
        serverContext.setSender(sender);
        event.setCancelled(runCommand(sender, event.getCommand()));
        serverContext.setSender(null);
    }

    private boolean runCommand(CommandSender sender, String commandText) {
        try {
            List<CommandLine> commands = cli.build(context).parse(commandText.split(" "));
            for (CommandLine commandLine : commands) {
                Object command = commandLine.getCommand();

                if (command instanceof Runnable) {
                    ((Runnable) command).run();
                } else if (command instanceof Callable) {
                    Object result = ((Callable) command).call();
                    outputResult(sender, result);
                }
            }
            return !commands.isEmpty();
        } catch (CommandLine.UnmatchedArgumentException ignored) {
        } catch (CommandLine.MissingParameterException ex) {
            String message = String.format(missingParameterErrorMessage, ex.getMissing().get(0).paramLabel());
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            return true;
        } catch (CommandLine.ParameterException ex) {
            String message = String.format(parameterErrorMessage, ex.getArgSpec().paramLabel());
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            return true;
        } catch (CommandException ex) {
            sender.sendMessage(ChatColor.RED + ex.getMessage());
            return true;
        } catch (Exception ex) {
            logger.error("Unexpected exception while running /" + commandText, ex);
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', commandErrorMessage));
            return true;
        }
        return false;
    }

    private void outputResult(CommandSender sender, Object result) {
        if (result instanceof String) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', (String) result));
        } else if (result instanceof Iterable) {
            ((Iterable<?>) result).forEach(res -> outputResult(sender, res));
        }
    }

}
