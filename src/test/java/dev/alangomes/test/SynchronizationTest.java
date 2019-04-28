package dev.alangomes.test;

import dev.alangomes.springspigot.util.Synchronize;
import dev.alangomes.test.util.SpringSpigotTestInitializer;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(
        classes = {TestApplication.class, SynchronizationTest.TestService.class},
        initializers = SpringSpigotTestInitializer.class
)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class SynchronizationTest {

    @Autowired
    private TestService testService;

    @Autowired
    private BukkitScheduler scheduler;

    @Autowired
    private Server server;

    @Autowired
    private Plugin plugin;

    @Test
    public void shouldSynchronizeIfNotOnMainThread() {
        when(server.isPrimaryThread()).thenReturn(false);

        testService.doSomethingSynchronized();

        verify(scheduler).scheduleSyncDelayedTask(eq(plugin), any(Runnable.class), eq(0L));
    }

    @Test
    public void shouldNotSynchronizeIfOnMainThread() {
        when(server.isPrimaryThread()).thenReturn(true);

        testService.doSomethingSynchronized();

        verify(scheduler, never()).scheduleSyncDelayedTask(eq(plugin), any(Runnable.class), eq(0L));
    }

    @Test
    public void shouldNotSynchronizeWithoutAnnotation() {
        when(server.isPrimaryThread()).thenReturn(false);

        testService.doSomething();

        verify(scheduler, never()).scheduleSyncDelayedTask(eq(plugin), any(Runnable.class), eq(0L));
    }

    @Service
    static class TestService {
        @Synchronize
        public void doSomethingSynchronized() {
        }

        public void doSomething() {
        }
    }

}