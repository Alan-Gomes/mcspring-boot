package dev.alangomes.mcspring.hook.exception;

import lombok.Getter;

@Getter
public class PermissionDeniedException extends RuntimeException {

    private final String permission;

    public PermissionDeniedException(String permission) {
        super("Permission denied: " + permission);
        this.permission = permission;
    }
}
