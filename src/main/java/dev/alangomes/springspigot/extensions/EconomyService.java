package dev.alangomes.springspigot.extensions;

import org.bukkit.OfflinePlayer;

import java.math.BigDecimal;

/**
 * Defines a implementation-agnostic economy service (normally vault)
 */
public interface EconomyService {

    /**
     * Deposit an amount to a player
     *
     * @param player to deposit to
     * @param amount Amount to deposit
     */
    void deposit(OfflinePlayer player, BigDecimal amount);

    /**
     * Withdraw an amount from a player
     *
     * @param player to withdraw from
     * @param amount Amount to withdraw
     */
    void withdraw(OfflinePlayer player, BigDecimal amount);

    /**
     * Transfer balance from one player to another
     *
     * @param origin to withdraw from
     * @param destination to deposit to
     * @param amount Amount to transfer
     */
    void transfer(OfflinePlayer origin, OfflinePlayer destination, BigDecimal amount);

    /**
     * Checks if the player account has the amount
     *
     * @param player to check
     * @param amount to check for
     * @return True if {@param player} has {@param amount}, False else wise
     */
    boolean has(OfflinePlayer player, BigDecimal amount);

    /**
     * Attempts to create a player account for the given player
     *
     * @param player the player to create account
     */
    void createAccount(OfflinePlayer player);

    /**
     * Format amount into a human readable String This provides translation into economy specific formatting to improve consistency between plugins.
     *
     * @param amount to format
     * @return Human readable string describing amount
     */
    String format(BigDecimal amount);

    /**
     * Gets balance of a player
     *
     * @param player to get the balance
     * @return Amount currently held in players account
     */
    BigDecimal getBalance(OfflinePlayer player);

    /**
     * Checks if this player has an account on the server yet.
     * This will always return true if the player has joined the server at least once as all major economy plugins
     * auto-generate a player account when the player joins the server
     *
     * @param player to check
     * @return if the player has an account
     */
    boolean hasAccount(OfflinePlayer player);

}
