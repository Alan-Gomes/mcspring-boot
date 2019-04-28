package dev.alangomes.springspigot.picocli;

import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
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
        CommandLine commandLine = new CommandLine(beanName != null ? getBean(factory, beanName) : instance);
        subcommands.forEach((key, value) -> {
            if (value instanceof CommandLineDefinition) {
                commandLine.addSubcommand(key, ((CommandLineDefinition) value).build(factory));
            } else if (value instanceof String) {
                commandLine.addSubcommand(key, getBean(factory, (String) value));
            } else {
                commandLine.addSubcommand(key, value);
            }
        });
        return commandLine;
    }

    @SneakyThrows
    private Object getBean(BeanFactory factory, String name) {
        Object bean = factory.getBean(name);
        if (AopUtils.isAopProxy(bean)) {
            return ((Advised) bean).getTargetSource().getTarget();
        }
        return bean;
    }

    public Set<String> getCommandNames() {
        return Collections.unmodifiableSet(subcommands.keySet());
    }

}
