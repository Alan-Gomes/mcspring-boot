package dev.alangomes.springspigot.util.scheduler;

import dev.alangomes.springspigot.context.Context;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Wrapper around {@link org.bukkit.scheduler.BukkitScheduler BukkitScheduler} to remove the need of the plugin reference
 * as well as keep the context during the tasks.
 */
@Component
public class Scheduler {

    @Autowired
    private BukkitScheduler scheduler;

    @Autowired
    private Plugin plugin;

    @Autowired
    private Context context;

    /**
     * Schedules a once off task to occur after a delay.
     * <p>
     * This task will be executed by the main server thread.
     *
     * @param task  Task to be executed
     * @param delay Delay in server ticks before executing task
     * @return Task id number (-1 if scheduling failed)
     */
    public int scheduleSyncDelayedTask(Runnable task, long delay) {
        return scheduler.scheduleSyncDelayedTask(plugin, context.wrap(task), delay);
    }

    /**
     * Schedules a once off task to occur as soon as possible.
     * <p>
     * This task will be executed by the main server thread.
     *
     * @param task Task to be executed
     * @return Task id number (-1 if scheduling failed)
     */
    public int scheduleSyncDelayedTask(Runnable task) {
        return scheduler.scheduleSyncDelayedTask(plugin, context.wrap(task));
    }

    /**
     * Schedules a repeating task.
     * <p>
     * This task will be executed by the main server thread.
     *
     * @param task   Task to be executed
     * @param delay  Delay in server ticks before executing first repeat
     * @param period Period in server ticks of the task
     * @return Task id number (-1 if scheduling failed)
     */
    public int scheduleSyncRepeatingTask(Runnable task, long delay, long period) {
        return scheduler.scheduleSyncRepeatingTask(plugin, context.wrap(task), delay, period);
    }

    /**
     * Removes task from scheduler.
     *
     * @param taskId Id number of task to be removed
     */
    public void cancelTask(int taskId) {
        scheduler.cancelTask(taskId);
    }

    /**
     * Check if the task currently running.
     * <p>
     * A repeating task might not be running currently, but will be running in
     * the future. A task that has finished, and does not repeat, will not be
     * running ever again.
     * <p>
     * Explicitly, a task is running if there exists a thread for it, and that
     * thread is alive.
     *
     * @param taskId The task to check.
     *               <p>
     * @return If the task is currently running.
     */
    public boolean isCurrentlyRunning(int taskId) {
        return scheduler.isCurrentlyRunning(taskId);
    }

    /**
     * Check if the task queued to be run later.
     * <p>
     * If a repeating task is currently running, it might not be queued now
     * but could be in the future. A task that is not queued, and not running,
     * will not be queued again.
     *
     * @param taskId The task to check.
     *               <p>
     * @return If the task is queued to be run.
     */
    public boolean isQueued(int taskId) {
        return scheduler.isQueued(taskId);
    }
}
