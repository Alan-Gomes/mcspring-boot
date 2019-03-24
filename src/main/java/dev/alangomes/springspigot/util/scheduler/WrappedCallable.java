package dev.alangomes.springspigot.util.scheduler;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.val;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
class WrappedCallable<T> implements Callable<T> {

    private final Plugin plugin;

    private final BukkitScheduler scheduler;

    @EqualsAndHashCode.Include
    private final Callable<T> callable;

    @Override
    @SneakyThrows
    public T call() {
        val future = new CompletableFuture<T>();
        scheduler.scheduleSyncDelayedTask(plugin, () -> {
            try {
                future.complete(callable.call());
            } catch (Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        });
        return future.get(1, TimeUnit.MINUTES);
    }

}