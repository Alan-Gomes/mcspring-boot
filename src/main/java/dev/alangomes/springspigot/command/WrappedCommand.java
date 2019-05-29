package dev.alangomes.springspigot.command;

import dev.alangomes.springspigot.context.Context;
import dev.alangomes.springspigot.util.CommandUtils;
import lombok.val;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import picocli.CommandLine.Model.CommandSpec;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class WrappedCommand extends Command {

    private final Context context;

    private final CommandExecutor commandExecutor;

    private final CommandSpec commandSpec;

    protected WrappedCommand(CommandSpec commandSpec, Context context, CommandExecutor commandExecutor) {
        super(commandSpec.name());
        this.commandSpec = commandSpec;
        this.context = context;
        this.commandExecutor = commandExecutor;
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        return context.runWithSender(sender, () -> {
            val command = prepend(args, label);
            val result = commandExecutor.execute(command);
            result.getOutput().forEach(sender::sendMessage);
            return result.isExists();
        });
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 0) return Collections.emptyList();
        return context.runWithSender(sender, () -> {
            Stream<String> possibleSubcommands = CommandUtils.getPossibleSubcommands(commandSpec, args);
            Stream<String> possibleArguments = CommandUtils.getPossibleArguments(commandSpec, args);
            return Stream.concat(possibleSubcommands, possibleArguments).collect(Collectors.toList());
        });
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList(commandSpec.aliases());
    }

    @Override
    public String getUsage() {
        return commandSpec.commandLine().getUsageMessage();
    }

    @Override
    public String getDescription() {
        return String.join("\n", commandSpec.usageMessage().description());
    }



    private static <T> T[] prepend(T[] oldArray, T item) {
        val newArray = (T[]) Array.newInstance(oldArray.getClass().getComponentType(), oldArray.length + 1);
        System.arraycopy(oldArray, 0, newArray, 1, oldArray.length);
        newArray[0] = item;
        return newArray;
    }
}
