package dev.alangomes.springspigot.util.scheduler;

import dev.alangomes.springspigot.context.Context;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;

@AllArgsConstructor
public class SpigotScheduler extends ThreadPoolTaskScheduler {

    private final SchedulerService scheduler;

    private final Context context;

    private Runnable wrapSync(Runnable task) {
        return new WrappedRunnable(scheduler, task);
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable task, Trigger trigger) {
        return super.schedule(wrapSync(task), trigger);
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable task, Date startTime) {
        return super.schedule(wrapSync(task), startTime);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Date startTime, long period) {
        return super.scheduleAtFixedRate(wrapSync(task), startTime, period);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long period) {
        return super.scheduleAtFixedRate(wrapSync(task), period);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Date startTime, long delay) {
        return super.scheduleWithFixedDelay(wrapSync(task), startTime, delay);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long delay) {
        return super.scheduleWithFixedDelay(wrapSync(task), delay);
    }

    @Override
    public void execute(Runnable task) {
        super.execute(context.wrap(task));
    }

    @Override
    public Future<?> submit(Runnable task) {
        return super.submit(context.wrap(task));
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return super.submit(context.wrap(task));
    }

    @Override
    public ListenableFuture<?> submitListenable(Runnable task) {
        return super.submitListenable(context.wrap(task));
    }

    @Override
    public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
        return super.submitListenable(context.wrap(task));
    }

}
