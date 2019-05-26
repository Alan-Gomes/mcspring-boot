package dev.alangomes.springspigot.util.scheduler;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
class WrappedRunnable implements Runnable {

    private final SchedulerService scheduler;

    @EqualsAndHashCode.Include
    private final Runnable runnable;

    @Override
    public void run() {
        scheduler.scheduleSyncDelayedTask(runnable);
    }

}