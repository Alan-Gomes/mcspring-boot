package dev.alangomes.springspigot.command;

import dev.alangomes.springspigot.context.Context;
import dev.alangomes.springspigot.picocli.CommandLineDefinition;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.Set;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

@Slf4j
@Component
@Scope(SCOPE_SINGLETON)
@ConditionalOnBean(annotation = CommandLine.Command.class)
class CommandService {

    @Autowired
    private CommandLineDefinition commandLineDefinition;

    @Autowired
    private Context context;

    @Autowired
    private Plugin plugin;

    @Autowired
    private CommandExecutor commandExecutor;

    @Autowired
    private ResourceLoader resourceLoader;

    @Getter
    private boolean registered;

    private Class<?> bukkitClass;

    @PostConstruct
    void init() {
        try {
            Set<String> commandNames = commandLineDefinition.getCommandNames();
            val packageName = plugin.getServer().getClass().getPackage().getName();
            val version = packageName.substring(packageName.lastIndexOf('.') + 1);
            this.bukkitClass = Class.forName("org.bukkit.craftbukkit." + version + ".CraftServer", false, resourceLoader.getClassLoader());
            commandNames.forEach(this::registerCommand);
            log.debug("Succesfully registered {} commands", commandNames.size());
            registered = true;
        } catch (Throwable t) {
            log.debug("Failed to register commands natively, falling back to event listeners", t);
        }
    }

    @PreDestroy
    void destroy() {
        try {
            commandLineDefinition.getCommandNames().forEach(this::unregisterCommand);
        } catch (Throwable t) {
            log.debug("Failed to unregister commands natively", t);
        }
    }

    @SneakyThrows
    public void registerCommand(String commandName) {
        val commandMapField = bukkitClass.getDeclaredField("commandMap");
        commandMapField.setAccessible(true);
        val commandMap = (SimpleCommandMap) commandMapField.get(plugin.getServer());

        commandMap.register(plugin.getName().toLowerCase(), new WrappedCommand(commandName, context, commandExecutor));
    }

    @SneakyThrows
    public void unregisterCommand(String commandName) {
        val manager = (SimplePluginManager) plugin.getServer().getPluginManager();

        val commandMapField = SimplePluginManager.class.getDeclaredField("commandMap");
        commandMapField.setAccessible(true);
        val map = (CommandMap) commandMapField.get(manager);

        val knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
        knownCommandsField.setAccessible(true);
        val knownCommands = (Map<String, Command>) knownCommandsField.get(map);

        val command = knownCommands.get(commandName);
        if (command != null) {
            command.unregister(map);
            knownCommands.remove(commandName);
        }
    }

}
