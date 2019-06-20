package dev.alangomes.springspigot.picocli;

import dev.alangomes.springspigot.picocli.conversion.PicocliConversorInjector;
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

    private final HashMap<String, CommandLineDefinition> subcommands = new HashMap<>();

    CommandLineDefinition(Object instance, CommandLine.IFactory factory) {
        this.beanName = instance instanceof String ? (String) instance : null;
        this.instance = !(instance instanceof String) ? instance : null;
        this.commandFactory = factory;
    }

    void addSubcommand(String name, Object commandLine) {
        if (commandLine instanceof CommandLineDefinition) {
            subcommands.put(name, (CommandLineDefinition) commandLine);
        } else {
            subcommands.put(name, new CommandLineDefinition(commandLine, commandFactory));
        }
    }

    public CommandLine build(BeanFactory factory) {
        CommandLine commandLine = doBuild(factory);

        overrideHelpRenderers(commandLine);
        overrideConverters(factory, commandLine);
        return commandLine;
    }

    private CommandLine doBuild(BeanFactory factory) {
        CommandLine commandLine = new CommandLine(beanName != null ? getBean(factory, beanName) : instance, commandFactory);

        subcommands.forEach((key, value) -> commandLine.addSubcommand(key, value.doBuild(factory)));
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

    private void overrideConverters(BeanFactory beanFactory, CommandLine commandLine) {
        PicocliConversorInjector picocliConversorInjector = beanFactory.getBean(PicocliConversorInjector.class);
        picocliConversorInjector.injectConversor(commandLine);
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
