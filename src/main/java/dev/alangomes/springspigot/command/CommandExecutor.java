package dev.alangomes.springspigot.command;

public interface CommandExecutor {

    CommandResult execute(String... command);

}
