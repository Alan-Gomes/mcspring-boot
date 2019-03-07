package dev.alangomes.springspigot;

import dev.alangomes.springspigot.context.Context;
import dev.alangomes.springspigot.picocli.CommandLineDefinition;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandException;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;
import picocli.CommandLine;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.Callable;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CommandInterceptorTest {

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private CommandLineDefinition commandLineDefinition;

    @Mock
    private Context context;

    @Mock
    private Runnable commandRunnable;

    @Mock
    private Callable commandCallable;

    @Mock
    private Player player;

    @Mock
    private CommandLine command, commandLine;

    @Mock
    private CommandLine.Model.ArgSpec argument;

    @InjectMocks
    private CommandInterceptor commandInterceptor;

    private PlayerCommandPreprocessEvent event;

    @Before
    public void setup() {
        when(commandLineDefinition.build(applicationContext)).thenReturn(commandLine);
        when(commandLine.parse(any())).thenReturn(Collections.singletonList(command));

        when(command.getCommand()).thenReturn(commandRunnable);

        when(argument.paramLabel()).thenReturn("<parameter>");

        event = new PlayerCommandPreprocessEvent(player, "/say hello", Collections.emptySet());
        doAnswer(i -> {
            ((Runnable) i.getArguments()[1]).run();
            return null;
        }).when(context).runWithSender(any(), any());
    }

    @Test
    public void shouldRunExistingCommandSuccessfully() {
        commandInterceptor.onPlayerCommand(event);

        assertTrue(event.isCancelled());
        verify(commandLine).parse("say", "hello");
        verify(commandRunnable).run();
    }

    @Test
    public void shouldRunExistingCommandSuccessfullyFromConsole() {
        ServerCommandEvent serverCommandEvent = new ServerCommandEvent(player, "say hello");
        commandInterceptor.onServerCommand(serverCommandEvent);

        assertTrue(serverCommandEvent.isCancelled());
        verify(commandLine).parse("say", "hello");
        verify(commandRunnable).run();
    }

    @Test
    public void shouldIgnoreUnknownCommand() {
        when(commandLine.parse(any())).thenReturn(Collections.emptyList());
        commandInterceptor.onPlayerCommand(event);

        assertFalse(event.isCancelled());
        verify(commandLine).parse("say", "hello");
    }

    @Test
    public void shouldIgnoreInvalidCommand() {
        when(commandLine.parse(any())).thenThrow(new CommandLine.UnmatchedArgumentException(commandLine, ""));

        commandInterceptor.onPlayerCommand(event);

        assertFalse(event.isCancelled());
        verify(commandLine).parse("say", "hello");
    }

    @Test
    public void shouldSendCallableStringOutput() throws Exception {
        when(commandCallable.call()).thenReturn("hello world");
        when(command.getCommand()).thenReturn(commandCallable);

        commandInterceptor.onPlayerCommand(event);

        assertTrue(event.isCancelled());
        verify(commandLine).parse("say", "hello");
        verify(commandCallable).call();
        verify(player).sendMessage("hello world");
    }

    @Test
    public void shouldSendCallableListOutput() throws Exception {
        when(commandCallable.call()).thenReturn(Arrays.asList("hello", "world"));
        when(command.getCommand()).thenReturn(commandCallable);

        commandInterceptor.onPlayerCommand(event);

        assertTrue(event.isCancelled());
        verify(commandLine).parse("say", "hello");
        verify(commandCallable).call();
        verify(player).sendMessage("hello");
        verify(player).sendMessage("world");
    }

    @Test
    public void shouldSendMissingParameterError() {
        commandInterceptor.setMissingParameterErrorMessage("&amissing parameter: %s");
        when(commandLine.parse(any())).thenThrow(new CommandLine.MissingParameterException(commandLine, argument, ""));

        commandInterceptor.onPlayerCommand(event);

        assertTrue(event.isCancelled());
        verify(commandLine).parse("say", "hello");
        verify(player).sendMessage(ChatColor.GREEN + "missing parameter: <parameter>");
    }

    @Test
    public void shouldSendInvalidParameterError() {
        commandInterceptor.setParameterErrorMessage("&binvalid parameter: %s");
        when(commandLine.parse(any())).thenThrow(new CommandLine.ParameterException(commandLine, "", argument, ""));

        commandInterceptor.onPlayerCommand(event);

        assertTrue(event.isCancelled());
        verify(commandLine).parse("say", "hello");
        verify(player).sendMessage(ChatColor.AQUA + "invalid parameter: <parameter>");
    }

    @Test
    public void shouldSendCommandErrorMessage() {
        commandInterceptor.setParameterErrorMessage("invalid parameter: %s");
        when(commandLine.parse(any())).thenThrow(new CommandException("generic error"));

        commandInterceptor.onPlayerCommand(event);

        assertTrue(event.isCancelled());
        verify(commandLine).parse("say", "hello");
        verify(player).sendMessage(ChatColor.RED + "generic error");
    }

    @Test
    public void shouldSendGenericErrorMessage() {
        commandInterceptor.setCommandErrorMessage("&cunexpected error");
        when(commandLine.parse(any())).thenThrow(new RuntimeException("ignored message"));

        commandInterceptor.onPlayerCommand(event);

        assertTrue(event.isCancelled());
        verify(commandLine).parse("say", "hello");
        verify(player).sendMessage(ChatColor.RED + "unexpected error");
    }

}