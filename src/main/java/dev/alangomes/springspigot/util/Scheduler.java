package dev.alangomes.springspigot.util;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Wrapper around {@link org.bukkit.scheduler.BukkitScheduler BukkitScheduler} to make scheduling easier to write and test.
 *
 * @see org.bukkit.scheduler.BukkitScheduler
 */
@Component
@Scope("singleton")
public class Scheduler {

    @Autowired
    private BukkitScheduler scheduler;

    @Autowired
    private Plugin plugin;

    public int scheduleSyncDelayedTask(Runnable task, long delay) {
        return scheduler.scheduleSyncDelayedTask(plugin, task, delay);
    }

    public int scheduleSyncRepeatingTask(Runnable task, long delay, long period) {
        return scheduler.scheduleSyncRepeatingTask(plugin, task, delay, period);
    }

    public void cancelTask(int taskId) {
        scheduler.cancelTask(taskId);
    }

    public boolean isCurrentlyRunning(int taskId) {
        return scheduler.isCurrentlyRunning(taskId);
    }

    public boolean isQueued(int taskId) {
        return scheduler.isQueued(taskId);
    }
}
