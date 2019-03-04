package dev.alangomes.mcspring.hook.exception;

public class PlayerNotFoundException extends RuntimeException {

    public PlayerNotFoundException() {
        super("Player not found");
    }
}
