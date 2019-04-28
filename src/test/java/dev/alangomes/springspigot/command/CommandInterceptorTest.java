package dev.alangomes.springspigot.command;

import dev.alangomes.springspigot.context.Context;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CommandInterceptorTest {

    @Mock
    private Context context;

    @Mock
    private CommandExecutor commandExecutor;

    @Mock
    private Player player;

    @InjectMocks
    private CommandInterceptor commandInterceptor;

    @Before
    public void setup() {
        when(context.getPlayer()).thenReturn(player);
        when(context.getSender()).thenReturn(player);
        when(commandExecutor.execute(any())).thenReturn(new CommandResult(Arrays.asList("message1", "message2")));
    }

    @Test
    public void shouldDelegatePlayerCommandToExecutorWithContext() {
        PlayerCommandPreprocessEvent event = new PlayerCommandPreprocessEvent(player, "/say hello", new HashSet<>());
        commandInterceptor.onPlayerCommand(event);

        assertTrue(event.isCancelled());
        verify(commandExecutor).execute("say", "hello");
        verify(player).sendMessage("message1");
        verify(player).sendMessage("message2");
    }

    @Test
    public void shouldDelegateServerCommandToExecutorWithContext() {
        ServerCommandEvent event = new ServerCommandEvent(player, "say hello");
        commandInterceptor.onServerCommand(event);

        assertTrue(event.isCancelled());
        verify(commandExecutor).execute("say", "hello");
        verify(player).sendMessage("message1");
        verify(player).sendMessage("message2");
    }

}