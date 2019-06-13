package dev.alangomes.springspigot.util;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Model.PositionalParamSpec;

import java.util.Map;
import java.util.Optional;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class CommandUtils {

    private CommandUtils() {
    }

    /**
     * Find all possible subcommand names for autocompletion
     *
     * @param commandSpec The {@link picocli.CommandLine.Model.CommandSpec CommandSpec} to be analyzed
     * @param args        The partial arguments to be used on analysis
     * @return All possible subcommands (names and aliases considered)
     */
    public static Stream<String> getPossibleSubcommands(CommandSpec commandSpec, String[] args) {
        return getPossibleSubcommands(commandSpec, args, 0);
    }

    /**
     * Find suggestions of positional arguments for autocompletion
     *
     * @param commandSpec The {@link picocli.CommandLine.Model.CommandSpec CommandSpec} to be analyzed
     * @param args        The partial arguments to be used on analysis
     * @return A stream of suggested values for the last argument (respecting
     * {@link PositionalParamSpec#completionCandidates() completionCandidates})
     */
    public static Stream<String> getPossibleArguments(CommandSpec commandSpec, String[] args) {
        int requestedIndex = args.length - 1;
        return getPossibleCommands(commandSpec, args, 0)
                .map(specPair -> specPair.getKey().positionalParameters().stream()
                        .filter(paramSpec -> paramSpec.index().contains(Math.max(0,
                                requestedIndex - (specPair.getValue() + 1))))
                        .findFirst())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .flatMap(CommandUtils::getSuggestedValues)
                .filter(value -> StringUtil.startsWithIgnoreCase(value, args[requestedIndex]));
    }

    private static Stream<String> getSuggestedValues(PositionalParamSpec paramSpec) {
        Iterable<String> completionCandidates = paramSpec.completionCandidates();
        if (completionCandidates != null) {
            return StreamSupport.stream(Spliterators.spliteratorUnknownSize(completionCandidates.iterator(), 0), false);
        } else {
            return getSuggestedValues(paramSpec.type());
        }
    }

    private static Stream<String> getSuggestedValues(Class<?> type) {
        if (CommandSender.class.isAssignableFrom(type)) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName);
        } else if (World.class.isAssignableFrom(type)) {
            return Bukkit.getWorlds().stream().map(World::getName);
        }
        return Stream.empty();
    }

    private static Stream<String> getPossibleSubcommands(CommandSpec spec, String[] args, int index) {
        if (args.length == 0) return Stream.empty();
        Stream<Map.Entry<String, CommandLine>> subcommandsStream = spec.subcommands().entrySet().stream();
        if (index + 1 == args.length) {
            return subcommandsStream
                    .filter(entry -> StringUtil.startsWithIgnoreCase(entry.getKey(), args[index]))
                    .flatMap(entry -> entry.getValue().getCommandSpec().names().stream());
        }
        return subcommandsStream
                .filter(entry -> entry.getKey().equalsIgnoreCase(args[index]))
                .flatMap(entry -> getPossibleSubcommands(entry.getValue().getCommandSpec(), args, index + 1));
    }

    private static Stream<Pair<CommandSpec, Integer>> getPossibleCommands(CommandSpec spec, String[] args, int index) {
        if (args.length <= 1) return Stream.of(Pair.of(spec, index));
        return spec.subcommands().entrySet().stream()
                .filter(entry -> entry.getKey().equalsIgnoreCase(args[index]))
                .flatMap(entry -> {
                    if (index + 2 >= args.length || !hasSubcommand(entry.getValue().getCommandSpec(),
                            args[index + 1])) {
                        return Stream.of(Pair.of(entry.getValue().getCommandSpec(), index));
                    } else {
                        return getPossibleCommands(entry.getValue().getCommandSpec(), args, index + 1);
                    }
                });
    }

    private static boolean hasSubcommand(CommandSpec spec, String subcommand) {
        return spec.subcommands().keySet().stream()
                .anyMatch(sub -> sub.equalsIgnoreCase(subcommand));
    }

}
