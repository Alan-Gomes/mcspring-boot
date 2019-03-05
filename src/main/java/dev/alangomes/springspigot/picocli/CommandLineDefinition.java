package dev.alangomes.springspigot.picocli;

import lombok.Getter;
import org.springframework.beans.factory.BeanFactory;
import picocli.CommandLine;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

@Getter
public class CommandLineDefinition {

    private final String beanName;
    private final Object instance;

    private final HashMap<String, Object> subcommands = new HashMap<>();

    CommandLineDefinition(Object instance) {
        this.beanName = instance instanceof String ? (String) instance : null;
        this.instance = !(instance instanceof String) ? instance : null;
    }

    void addSubcommand(String name, Object commandLine) {
        subcommands.put(name, commandLine);
    }

    public CommandLine build(BeanFactory factory) {
        CommandLine commandLine = new CommandLine(beanName != null ? factory.getBean(beanName) : instance);
        subcommands.forEach((key, value) -> {
            if (value instanceof CommandLineDefinition) {
                commandLine.addSubcommand(key, ((CommandLineDefinition) value).build(factory));
            } else if (value instanceof String) {
                commandLine.addSubcommand(key, factory.getBean((String) value));
            } else {
                commandLine.addSubcommand(key, value);
            }
        });
        return commandLine;
    }

    public Set<String> getCommandNames() {
        return Collections.unmodifiableSet(subcommands.keySet());
    }

}
