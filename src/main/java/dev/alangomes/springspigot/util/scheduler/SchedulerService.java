package dev.alangomes.springspigot.util.scheduler;

/**
 * Wrapper around {@link org.bukkit.scheduler.BukkitScheduler BukkitScheduler} to remove the need of the plugin reference
 * as well as keep the context during the tasks.
 */
public interface SchedulerService {
    /**
     * Schedules a once off task to occur after a delay.
     * <p>
     * This task will be executed by the main server thread.
     *
     * @param task  Task to be executed
     * @param delay Delay in server ticks before executing task
     * @return Task id number (-1 if scheduling failed)
     */
    int scheduleSyncDelayedTask(Runnable task, long delay);

    /**
     * Schedules a once off task to occur as soon as possible.
     * <p>
     * This task will be executed by the main server thread.
     *
     * @param task Task to be executed
     * @return Task id number (-1 if scheduling failed)
     */
    int scheduleSyncDelayedTask(Runnable task);

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
    int scheduleSyncRepeatingTask(Runnable task, long delay, long period);

    /**
     * Removes task from scheduler.
     *
     * @param taskId Id number of task to be removed
     */
    void cancelTask(int taskId);

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
    boolean isCurrentlyRunning(int taskId);

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
    boolean isQueued(int taskId);
}
