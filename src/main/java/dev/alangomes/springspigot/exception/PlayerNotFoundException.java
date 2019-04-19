package dev.alangomes.springspigot.exception;

import lombok.Getter;

@Getter
public class PlayerNotFoundException extends RuntimeException {

    private final String name;

    public PlayerNotFoundException() {
        super("Player not found");
        this.name = null;
    }

    public PlayerNotFoundException(String name) {
        super(String.format("Player %s not found", name));
        this.name = name;
    }
}
