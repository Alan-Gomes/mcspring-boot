package dev.alangomes.springspigot.command;

import dev.alangomes.springspigot.context.Context;
import lombok.val;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Array;

class WrappedCommand extends Command {

    private final Context context;

    private final CommandExecutor commandExecutor;

    protected WrappedCommand(String name, Context context, CommandExecutor commandExecutor) {
        super(name);
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

    private static <T> T[] prepend(T[] oldArray, T item) {
        val newArray = (T[]) Array.newInstance(oldArray.getClass().getComponentType(), oldArray.length + 1);
        System.arraycopy(oldArray, 0, newArray, 1, oldArray.length);
        newArray[0] = item;
        return newArray;
    }
}
