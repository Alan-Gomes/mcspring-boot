package dev.alangomes.springspigot.security;

/**
 * Defines a guard service which will be available in all {@link dev.alangomes.springspigot.security.Authorize Authorize}
 * evaluations via the {@code #guard} variable. Can be used to create more complex verifications.
 */
public interface GuardService {
}
