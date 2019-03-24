package dev.alangomes.springspigot.util.scheduler;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
class WrappedRunnable implements Runnable {

    private final Plugin plugin;

    private final BukkitScheduler scheduler;

    @EqualsAndHashCode.Include
    private final Runnable runnable;

    @Override
    public void run() {
        scheduler.scheduleSyncDelayedTask(plugin, runnable);
    }

}