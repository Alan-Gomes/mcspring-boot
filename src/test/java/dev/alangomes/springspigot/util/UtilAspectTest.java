package dev.alangomes.springspigot.util;

import org.aspectj.lang.ProceedingJoinPoint;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UtilAspectTest {

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private BukkitScheduler scheduler;

    @Mock
    private Plugin plugin;

    @Mock
    private Server server;

    @InjectMocks
    private UtilAspect utilAspect;

    @Captor
    private ArgumentCaptor<Runnable> runnableCaptor;

    @Test
    public void shouldIgnoreOnMainThread() throws Throwable {
        when(server.isPrimaryThread()).thenReturn(true);

        utilAspect.synchronizeCall(joinPoint);

        verify(joinPoint).proceed();
        verify(scheduler, never()).scheduleSyncDelayedTask(eq(plugin), any(Runnable.class), anyLong());
    }

    @Test
    public void shouldScheduleExecutionToNextTick() throws Throwable {
        when(server.isPrimaryThread()).thenReturn(false);

        utilAspect.synchronizeCall(joinPoint);

        verify(joinPoint, never()).proceed();
        verify(scheduler).scheduleSyncDelayedTask(eq(plugin), runnableCaptor.capture(), eq(0L));

        Runnable runnable = runnableCaptor.getValue();
        runnable.run();

        verify(joinPoint).proceed();
    }

}