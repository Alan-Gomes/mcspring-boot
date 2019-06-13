package dev.alangomes.springspigot.picocli;

import dev.alangomes.springspigot.command.Subcommand;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import picocli.CommandLine;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

/**
 * @author Thibaud Leprêtre
 * <p>
 * Modified to support rebuild and to decouple from spring command line runner.
 * @see <a href="https://github.com/kakawait/picocli-spring-boot-starter">Spring boot Picocli starter</a>
 */
@Slf4j
@Configuration
@Scope(SCOPE_SINGLETON)
class PicocliConfiguration {

    @Bean
    CommandLineDefinition picocliCommandLine(ApplicationContext applicationContext, CommandLine.IFactory factory) {
        List<String> commands = Arrays.stream(applicationContext.getBeanNamesForAnnotation(CommandLine.Command.class))
                .filter(name -> !isSubcommand(applicationContext, name))
                .collect(Collectors.toList());
        List<String> mainCommands = getMainCommands(commands, applicationContext);
        Object mainCommand = mainCommands.isEmpty() ? new BaseCommand() : mainCommands.get(0);
        commands.removeAll(mainCommands);
        CommandLineDefinition cli = new CommandLineDefinition(mainCommand, factory);
        registerCommands(cli, commands, applicationContext, factory);
        return cli;
    }

    private boolean isSubcommand(BeanFactory beanFactory, String beanName) {
        Class<?> type = getType(beanFactory, beanName);
        return AnnotationUtils.findAnnotation(type, Subcommand.class) != null;
    }

    private String getCommandName(String command, ApplicationContext applicationContext) {
        if (command == null) {
            return null;
        }
        return getType(applicationContext, command).getAnnotation(CommandLine.Command.class).name();
    }

    private String getCommandName(Class<?> commandClass) {
        if (commandClass == null) {
            return null;
        }
        return commandClass.getAnnotation(CommandLine.Command.class).name();
    }

    private List<String> getMainCommands(Collection<String> candidates, ApplicationContext context) {
        List<String> mainCommands = new ArrayList<>();
        for (String candidate : candidates) {
            Class<?> clazz = getType(context, candidate);
            Method method = ReflectionUtils.findMethod(CommandLine.Command.class, "name");
            if (clazz.isAnnotationPresent(CommandLine.Command.class)
                    && method != null
                    && clazz.getAnnotation(CommandLine.Command.class).name().equals(method.getDefaultValue())) {
                mainCommands.add(candidate);
            }
        }
        return mainCommands;
    }

    private int getNestedLevel(Class clazz) {
        int level = 0;
        Class parent = clazz.getEnclosingClass();
        while (parent != null && parent.isAnnotationPresent(CommandLine.Command.class)) {
            parent = parent.getEnclosingClass();
            level += 1;
        }
        return level;
    }

    private Optional<Class> getParentClass(Class clazz) {
        Class parentClass = clazz.getEnclosingClass();
        if (parentClass == null || !parentClass.isAnnotationPresent(CommandLine.Command.class)) {
            return Optional.empty();
        }
        return Optional.of(parentClass);
    }

    private Map<Node, List<String>> findCommands(Collection<String> commands, ApplicationContext applicationContext) {
        Map<Node, List<String>> tree = new LinkedHashMap<>();

        commands.stream()
                .filter(o -> getType(applicationContext, o) != null)
                .sorted((o1, o2) -> {
                    int l1 = getNestedLevel(getType(applicationContext, o1));
                    int l2 = getNestedLevel(getType(applicationContext, o2));
                    return Integer.compare(l1, l2);
                })
                .forEach(o -> {
                    Class<?> clazz = getType(applicationContext, o);
                    Optional<Class> parentClass = getParentClass(clazz);
                    parentClass.ifPresent(c -> {
                        List<String> objects = tree.get(new Node(c, null, null));
                        if (objects != null) {
                            objects.add(o);
                        }
                    });
                    tree.put(new Node(clazz, o, parentClass.orElse(null)), new ArrayList<>());
                });

        return tree;
    }

    private void registerCommands(CommandLineDefinition cli, Collection<String> commands, ApplicationContext applicationContext, CommandLine.IFactory factory) {
        CommandLineDefinition current = cli;
        Map<Class<?>, CommandLineDefinition> parents = new HashMap<>();
        for (Map.Entry<Node, List<String>> entry : findCommands(commands, applicationContext).entrySet()) {
            Node node = entry.getKey();
            if (node.getParent() != null && !node.getParent().equals(getType(applicationContext, current.getBeanName()))) {
                continue;
            }
            List<String> children = entry.getValue();
            String command = node.getBeanName();
            String commandName = getCommandName(node.getClazz());
            if (StringUtils.isBlank(commandName)) {
                continue;
            }
            if (parents.containsKey(node.getParent())) {
                current = parents.get(node.getParent());
            } else if (node.getParent() == null) {
                current = cli;
            }
            if (children.isEmpty()) {
                current.addSubcommand(commandName, command);
            } else {
                CommandLineDefinition sub = new CommandLineDefinition(command, factory);
                current.addSubcommand(commandName, sub);
                for (String child : children) {
                    sub.addSubcommand(getCommandName(child, applicationContext), new CommandLineDefinition(child, factory));
                }
                current = sub;
            }
            parents.put(node.getClazz(), current);
        }
    }

    private Class<?> getType(BeanFactory beanFactory, String beanName) {
        return ClassUtils.getUserClass(beanFactory.getType(beanName));
    }

    private static class Node {
        private final Class<?> clazz;

        private final String beanName;

        private final Class<?> parent;

        Node(Class<?> clazz, String beanName, Class<?> parent) {
            this.clazz = clazz;
            this.beanName = beanName;
            this.parent = parent;
        }

        Class<?> getClazz() {
            return clazz;
        }

        String getBeanName() {
            return beanName;
        }

        Class<?> getParent() {
            return parent;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Node)) return false;

            Node node = (Node) o;

            return clazz != null ? clazz.equals(node.clazz) : node.clazz == null;
        }

        @Override
        public int hashCode() {
            return clazz != null ? clazz.hashCode() : 0;
        }
    }

}

@Component
@CommandLine.Command
class BaseCommand implements Runnable {

    @Override
    public void run() {
    }
}
