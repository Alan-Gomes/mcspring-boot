package dev.alangomes.springspigot.extensions.vault;

import dev.alangomes.springspigot.extensions.EconomyException;
import dev.alangomes.springspigot.extensions.EconomyService;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;

@Lazy
@Service
@ConditionalOnClass(Economy.class)
@ConditionalOnProperty(prefix = "mcspring.vault.economy", name = "enabled", matchIfMissing = true)
class VaultEconomyServiceImpl implements EconomyService {

    @Autowired
    private Server server;

    private Economy economy;

    @PostConstruct
    void init() {
        economy = server.getServicesManager().getRegistration(Economy.class).getProvider();
    }

    @Override
    public void deposit(OfflinePlayer player, BigDecimal amount) {
        assertEconomyResponse(economy.depositPlayer(player, amount.doubleValue()));
    }

    @Override
    public void withdraw(OfflinePlayer player, BigDecimal amount) {
        assertEconomyResponse(economy.withdrawPlayer(player, amount.doubleValue()));
    }

    @Override
    public void transfer(OfflinePlayer origin, OfflinePlayer destination, BigDecimal amount) {
        withdraw(origin, amount);
        deposit(destination, amount);
    }

    @Override
    public boolean has(OfflinePlayer player, BigDecimal amount) {
        return economy.has(player, amount.doubleValue());
    }

    @Override
    public void createAccount(OfflinePlayer player) {
        economy.createPlayerAccount(player);
    }

    @Override
    public String format(BigDecimal amount) {
        return economy.format(amount.doubleValue());
    }

    @Override
    public BigDecimal getBalance(OfflinePlayer player) {
        return BigDecimal.valueOf(economy.getBalance(player));
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return economy.hasAccount(player);
    }

    void assertEconomyResponse(EconomyResponse response) {
        if (response.type != EconomyResponse.ResponseType.SUCCESS) {
            throw new EconomyException(response.errorMessage);
        }
    }

}
