package dev.alangomes.springspigot.picocli;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.util.ReflectionUtils;
import picocli.CommandLine;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author Thibaud Leprêtre
 *
 * Modified to support rebuild and to decouple from spring command line runner.
 * @see <a href="https://github.com/kakawait/picocli-spring-boot-starter">Spring boot Picocli starter</a>
 */
@Configuration
@Scope("singleton")
public class PicocliConfiguration {

    @Bean
    CommandLineDefinition picocliCommandLine(ApplicationContext applicationContext) {
        List<String> commands = new ArrayList<>(Arrays.asList(applicationContext.getBeanNamesForAnnotation(CommandLine.Command.class)));
        List<String> mainCommands = getMainCommands(commands, applicationContext);
        Object mainCommand = mainCommands.isEmpty() ? new BaseCommand() : mainCommands.get(0);
        commands.removeAll(mainCommands);
        CommandLineDefinition cli = new CommandLineDefinition(mainCommand);
        registerCommands(cli, commands, applicationContext);
        return cli;
    }

    private String getCommandName(String command, ApplicationContext applicationContext) {
        if (command == null) {
            return null;
        }
        return applicationContext.getType(command).getAnnotation(CommandLine.Command.class).name();
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
            Class<?> clazz = context.getType(candidate);
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
                .filter(o -> applicationContext.getType(o) != null)
                .sorted((o1, o2) -> {
                    int l1 = getNestedLevel(applicationContext.getType(o1));
                    int l2 = getNestedLevel(applicationContext.getType(o2));
                    return Integer.compare(l1, l2);
                })
                .forEach(o -> {
                    Class<?> clazz = applicationContext.getType(o);
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

    private void registerCommands(CommandLineDefinition cli, Collection<String> commands, ApplicationContext applicationContext) {
        CommandLineDefinition current = cli;
        Map<Class<?>, CommandLineDefinition> parents = new HashMap<>();
        for (Map.Entry<Node, List<String>> entry : findCommands(commands, applicationContext).entrySet()) {
            Node node = entry.getKey();
            if (node.getParent() != null && !node.getParent().equals(applicationContext.getType(current.getBeanName()))) {
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
                CommandLineDefinition sub = new CommandLineDefinition(command);
                current.addSubcommand(commandName, sub);
                for (String child : children) {
                    sub.addSubcommand(getCommandName(child, applicationContext), new CommandLineDefinition(child));
                }
                current = sub;
            }
            parents.put(node.getClazz(), current);
        }
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

@CommandLine.Command
class BaseCommand implements Runnable {

    @Override
    public void run() {
    }
}
