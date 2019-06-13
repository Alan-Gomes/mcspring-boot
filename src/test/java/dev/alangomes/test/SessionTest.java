package dev.alangomes.test;

import dev.alangomes.springspigot.context.Context;
import dev.alangomes.springspigot.context.DefaultSessionService;
import dev.alangomes.springspigot.context.SessionService;
import dev.alangomes.springspigot.event.SpringEventExecutor;
import dev.alangomes.test.util.SpringSpigotTestInitializer;
import lombok.SneakyThrows;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.EventExecutor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(
        classes = TestApplication.class,
        initializers = SpringSpigotTestInitializer.class
)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class SessionTest {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private Context context;

    @Autowired
    private SpringEventExecutor springEventExecutor;

    @Autowired
    private Server server;

    @Mock
    private Player player1, player2;

    private EventExecutor eventExecutor;

    @Before
    @SneakyThrows
    public void setup() {
        when(server.getOnlineMode()).thenReturn(false);
        when(player1.getName()).thenReturn("player1");
        when(player2.getName()).thenReturn("player2");
        when(server.getPlayer("player1")).thenReturn(player1);
        when(server.getPlayer("player2")).thenReturn(player2);
        eventExecutor = springEventExecutor.create(DefaultSessionService.class.getDeclaredMethod("onQuit", PlayerQuitEvent.class));
    }

    @Test
    public void shouldStoreDifferentValuesForEachPlayer() {
        context.runWithSender(player1, () -> sessionService.current().put("key.test", "value for player 1"));
        context.runWithSender(player2, () -> sessionService.current().put("key.test", "value for player 2"));

        String value1 = context.runWithSender(player1, () -> (String) sessionService.current().get("key.test"));
        String value2 = context.runWithSender(player2, () -> (String) sessionService.current().get("key.test"));
        assertEquals("value for player 1", value1);
        assertEquals("value for player 2", value2);
    }

    @Test
    @SneakyThrows
    public void shouldClearPlayerSessionOnQuit() {
        context.runWithSender(player1, () -> sessionService.current().put("key.test", "value for player 1"));
        context.runWithSender(player2, () -> sessionService.current().put("key.test", "value for player 2"));

        eventExecutor.execute((Listener) sessionService, new PlayerQuitEvent(player2, ""));

        String value1 = context.runWithSender(player1, () -> (String) sessionService.current().get("key.test"));
        String value2 = context.runWithSender(player2, () -> (String) sessionService.current().get("key.test"));
        assertEquals("value for player 1", value1);
        assertNull(value2);
    }

    @Test
    public void shouldClearPlayerSessionManually() {
        context.runWithSender(player1, () -> sessionService.current().put("key.test", "value for player 1"));
        context.runWithSender(player2, () -> sessionService.current().put("key.test", "value for player 2"));

        context.runWithSender(player1, () -> sessionService.current().clear());

        String value1 = context.runWithSender(player1, () -> (String) sessionService.current().get("key.test"));
        String value2 = context.runWithSender(player2, () -> (String) sessionService.current().get("key.test"));
        assertNull(value1);
        assertEquals("value for player 2", value2);
    }

}