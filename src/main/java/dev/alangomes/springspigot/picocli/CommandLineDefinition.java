package dev.alangomes.springspigot.picocli;

import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanFactory;
import picocli.CommandLine;
import picocli.CommandLine.IHelpSectionRenderer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public class CommandLineDefinition {

    private final String beanName;
    private final Object instance;
    private final CommandLine.IFactory commandFactory;

    private final HashMap<String, Object> subcommands = new HashMap<>();

    CommandLineDefinition(Object instance, CommandLine.IFactory factory) {
        this.beanName = instance instanceof String ? (String) instance : null;
        this.instance = !(instance instanceof String) ? instance : null;
        this.commandFactory = factory;
    }

    void addSubcommand(String name, Object commandLine) {
        subcommands.put(name, commandLine);
    }

    public CommandLine build(BeanFactory factory) {
        CommandLine commandLine = new CommandLine(beanName != null ? getBean(factory, beanName) : instance, commandFactory);

        subcommands.forEach((key, value) -> {
            if (value instanceof CommandLineDefinition) {
                commandLine.addSubcommand(key, ((CommandLineDefinition) value).build(factory));
            } else if (value instanceof String) {
                commandLine.addSubcommand(key, getBean(factory, (String) value));
            } else {
                commandLine.addSubcommand(key, value);
            }
        });

        overrideHelpRenderers(commandLine);
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

    private void overrideHelpRenderers(CommandLine commandLine) {
        Map<String, IHelpSectionRenderer> renderers = commandLine.getHelpSectionMap().keySet()
                .stream()
                .collect(Collectors.toMap(Function.identity(), (k) -> overrideRenderer(commandLine.getHelpSectionMap().get(k))));
        commandLine.setHelpSectionMap(renderers);
    }

    private IHelpSectionRenderer overrideRenderer(IHelpSectionRenderer renderer) {
        // strip carriage returns when running inside a windows server
        return (h) -> renderer.render(h).replaceAll("\\r", "");
    }

}
