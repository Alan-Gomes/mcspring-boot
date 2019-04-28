package dev.alangomes.springspigot.util.scheduler;

import dev.alangomes.springspigot.context.Context;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Scheduler {

    @Autowired
    private BukkitScheduler scheduler;

    @Autowired
    private Plugin plugin;

    @Autowired
    private Context context;

    public int scheduleSyncDelayedTask(Runnable runnable, long delay) {
        return scheduler.scheduleSyncDelayedTask(plugin, context.wrap(runnable), delay);
    }

    public int scheduleSyncDelayedTask(Runnable runnable) {
        return scheduler.scheduleSyncDelayedTask(plugin, context.wrap(runnable));
    }

    public int scheduleSyncRepeatingTask(Runnable runnable, long delay, long repeat) {
        return scheduler.scheduleSyncRepeatingTask(plugin, context.wrap(runnable), delay, repeat);
    }

    public void cancelTask(int id) {
        scheduler.cancelTask(id);
    }

    public boolean isCurrentlyRunning(int id) {
        return scheduler.isCurrentlyRunning(id);
    }

    public boolean isQueued(int id) {
        return scheduler.isQueued(id);
    }
}
