package dev.alangomes.springspigot.util.scheduler;

import dev.alangomes.springspigot.context.Context;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

@Service
@Scope(SCOPE_SINGLETON)
class SchedulerServiceDefaultImpl implements SchedulerService, Scheduler {

    @Autowired
    private BukkitScheduler scheduler;

    @Autowired
    private Plugin plugin;

    @Autowired
    private Context context;

    @Override
    public int scheduleSyncDelayedTask(Runnable task, long delay) {
        return scheduler.scheduleSyncDelayedTask(plugin, context.wrap(task), delay);
    }

    @Override
    public int scheduleSyncDelayedTask(Runnable task) {
        return scheduler.scheduleSyncDelayedTask(plugin, context.wrap(task));
    }

    @Override
    public int scheduleSyncRepeatingTask(Runnable task, long delay, long period) {
        return scheduler.scheduleSyncRepeatingTask(plugin, context.wrap(task), delay, period);
    }

    @Override
    public void cancelTask(int taskId) {
        scheduler.cancelTask(taskId);
    }

    @Override
    public boolean isCurrentlyRunning(int taskId) {
        return scheduler.isCurrentlyRunning(taskId);
    }

    @Override
    public boolean isQueued(int taskId) {
        return scheduler.isQueued(taskId);
    }
}
