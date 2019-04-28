package dev.alangomes.test;

import dev.alangomes.springspigot.context.Context;
import dev.alangomes.springspigot.util.Synchronize;
import dev.alangomes.test.util.SpringSpigotTestInitializer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
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
    private Context context;

    @Autowired
    private Plugin plugin;

    @Captor
    private ArgumentCaptor<Runnable> runnableCaptor;

    @Mock
    private Player player;

    @Before
    public void setup() {
        when(player.getName()).thenReturn("test");
        when(server.getOnlineMode()).thenReturn(false);
        when(server.getPlayer("test")).thenReturn(player);
    }

    @Test
    public void shouldSynchronizeIfNotOnMainThread() {
        when(server.isPrimaryThread()).thenReturn(false);

        context.runWithSender(player, testService::doSomethingSynchronized);

        verify(scheduler).scheduleSyncDelayedTask(eq(plugin), any(Runnable.class), eq(0L));
    }

    @Test
    public void shouldNotSynchronizeIfOnMainThread() {
        when(server.isPrimaryThread()).thenReturn(true);

        context.runWithSender(player, testService::doSomethingSynchronized);

        verify(scheduler, never()).scheduleSyncDelayedTask(eq(plugin), any(Runnable.class), eq(0L));
    }

    @Test
    public void shouldNotSynchronizeWithoutAnnotation() {
        when(server.isPrimaryThread()).thenReturn(false);

        testService.doSomething();

        verify(scheduler, never()).scheduleSyncDelayedTask(eq(plugin), any(Runnable.class), eq(0L));
    }

    @Test
    public void shouldKeepContextOnSynchronize() {
        when(server.isPrimaryThread()).thenReturn(false);

        context.runWithSender(player, testService::doSomethingSynchronized);

        verify(scheduler).scheduleSyncDelayedTask(eq(plugin), runnableCaptor.capture(), eq(0L));
        Runnable runnable = runnableCaptor.getValue();
        runnable.run();

        verify(player).sendMessage("test");
    }

    @Service
    static class TestService {

        @Autowired
        private Context context;

        @Synchronize
        public void doSomethingSynchronized() {
            context.getPlayer().sendMessage("test");
        }

        public void doSomething() {
        }
    }

}