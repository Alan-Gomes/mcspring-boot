package dev.alangomes.springspigot.exception;

public class PlayerNotFoundException extends RuntimeException {

    public PlayerNotFoundException() {
        super("Player not found");
    }
}
