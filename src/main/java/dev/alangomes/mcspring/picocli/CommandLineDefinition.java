package dev.alangomes.mcspring.picocli;

import lombok.Getter;
import org.springframework.beans.factory.BeanFactory;
import picocli.CommandLine;

import java.util.HashMap;

@Getter
public class CommandLineDefinition {

    private final String beanName;
    private final Object instance;

    private final HashMap<String, Object> subcommands = new HashMap<>();

    public CommandLineDefinition(Object instance) {
        this.beanName = instance instanceof String ? (String) instance : null;
        this.instance = !(instance instanceof String) ? instance : null;
    }

    public void addSubcommand(String name, Object commandLine) {
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

}
