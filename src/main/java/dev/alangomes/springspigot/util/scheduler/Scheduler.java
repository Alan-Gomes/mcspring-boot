package dev.alangomes.springspigot.util.scheduler;

import dev.alangomes.springspigot.context.Context;
import dev.alangomes.springspigot.util.ServerUtil;
import lombok.val;
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

    @Autowired
    private ServerUtil serverUtil;

    private Runnable wrapContext(Runnable runnable) {
        val senderId = context.getSenderId();
        return () -> context.runWithSender(serverUtil.getSenderFromId(senderId), runnable);
    }

    public int scheduleSyncDelayedTask(Runnable runnable, long delay) {
        return scheduler.scheduleSyncDelayedTask(plugin, wrapContext(runnable), delay);
    }

    public int scheduleSyncDelayedTask(Runnable runnable) {
        return scheduler.scheduleSyncDelayedTask(plugin, wrapContext(runnable));
    }

    public int scheduleSyncRepeatingTask(Runnable runnable, long delay, long repeat) {
        return scheduler.scheduleSyncRepeatingTask(plugin, wrapContext(runnable), delay, repeat);
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
