package dev.alangomes.springspigot.exception;

import lombok.Getter;
import org.bukkit.command.CommandException;

@Getter
public class PermissionDeniedException extends CommandException {

    private final String permission;

    public PermissionDeniedException(String permission, String message) {
        super(message != null ? message : "Sender didn't satisfied the condition: " + permission);
        this.permission = permission;
    }
}
