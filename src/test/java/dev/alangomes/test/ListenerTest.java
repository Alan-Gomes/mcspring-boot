package dev.alangomes.test;

import dev.alangomes.springspigot.context.Context;
import dev.alangomes.springspigot.event.SpringEventExecutor;
import dev.alangomes.springspigot.security.Audit;
import dev.alangomes.test.util.SpringSpigotTestInitializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@ContextConfiguration(
        classes = {TestApplication.class, ListenerTest.TestListener.class},
        initializers = SpringSpigotTestInitializer.class
)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class ListenerTest {

    @Autowired
    private TestListener testListener;

    @Autowired
    private SpringEventExecutor springEventExecutor;

    @Autowired
    private Server server;

    @Autowired
    private Plugin plugin;

    @SpyBean
    private Context context;

    @Mock
    private Player player;

    @Test
    public void shouldRegisterAllEventsInTheListener() {
        verify(server.getPluginManager()).registerEvent(eq(PlayerJoinEvent.class), eq(testListener),
                eq(EventPriority.NORMAL), notNull(), eq(plugin), eq(true));
        verify(server.getPluginManager()).registerEvent(eq(PlayerQuitEvent.class), eq(testListener),
                eq(EventPriority.HIGHEST), notNull(), eq(plugin), eq(false));
    }

    @Test
    @SneakyThrows
    public void shouldExecuteEventOnListener() {
        springEventExecutor
                .create(TestListener.class.getDeclaredMethod("onJoin", PlayerJoinEvent.class))
                .execute(testListener, new PlayerJoinEvent(player, ""));

        verify(player).sendMessage("join");
        verify(player, never()).sendMessage("quit");
    }

    @Test
    @SneakyThrows
    public void shouldInferSenderFromGetters() {
        springEventExecutor
                .create(TestListener.class.getDeclaredMethod("onTest", TestEvent.class))
                .execute(testListener, new TestEvent(player));

        verify(context).runWithSender(eq(player), any(Runnable.class));
    }

    @Component
    @Audit
    static class TestListener implements Listener {

        @Autowired
        private Context context;

        @EventHandler(ignoreCancelled = true)
        public void onJoin(PlayerJoinEvent event) {
            context.getPlayer().sendMessage("join");
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onQuit(PlayerQuitEvent event) {
            context.getPlayer().sendMessage("quit");
        }

        @EventHandler
        public void onTest(TestEvent event) {

        }

    }

    @AllArgsConstructor
    @Getter
    static class TestEvent extends Event {

        private CommandSender commandSender;

        @Override
        public HandlerList getHandlers() {
            return null;
        }
    }

}