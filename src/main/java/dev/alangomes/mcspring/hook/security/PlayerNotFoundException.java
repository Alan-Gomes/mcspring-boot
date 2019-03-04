package dev.alangomes.mcspring.hook.security;

public class PlayerNotFoundException extends RuntimeException {

    public PlayerNotFoundException() {
        super("Player not found");
    }
}
