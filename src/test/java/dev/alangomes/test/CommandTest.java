package dev.alangomes.test;

import dev.alangomes.springspigot.command.CommandExecutor;
import dev.alangomes.springspigot.command.CommandResult;
import dev.alangomes.springspigot.context.Context;
import dev.alangomes.springspigot.security.Audit;
import dev.alangomes.test.util.SpringSpigotTestInitializer;
import org.bukkit.entity.Player;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import picocli.CommandLine;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(
        classes = {TestApplication.class, CommandTest.TestCommand.class, CommandTest.MoneyCommand.class},
        initializers = SpringSpigotTestInitializer.class
)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class CommandTest {

    @Autowired
    private CommandExecutor executor;

    @Autowired
    private Context context;

    @Mock
    private Player player;

    @Test
    public void shouldExecuteCommandCorrectly() {
        CommandResult result = context.runWithSender(player, () -> executor.execute("test", "hello"));

        assertTrue(result.isExists());
        assertFalse(result.isErrored());
        List<String> messages = result.getOutput();
        assertEquals(2, messages.size());
        assertEquals("test", messages.get(0));
        assertEquals("hello", messages.get(1));
    }

    @Test
    public void shouldExecuteBaseCommandCorrectly() {
        CommandResult result = context.runWithSender(player, () -> executor.execute("money"));

        assertTrue(result.isExists());
        assertFalse(result.isErrored());
        List<String> messages = result.getOutput();
        assertEquals(1, messages.size());
        assertEquals("base", messages.get(0));
    }

    @Test
    public void shouldExecuteSubcommandCorrectly() {
        CommandResult result = context.runWithSender(player, () -> executor.execute("money", "add", "test"));

        assertTrue(result.isExists());
        assertFalse(result.isErrored());
        List<String> messages = result.getOutput();
        assertEquals(2, messages.size());
        assertEquals("add", messages.get(0));
        assertEquals("test", messages.get(1));
    }

    @Component
    @CommandLine.Command(name = "test")
    static class TestCommand implements Callable<List<String>> {

        @CommandLine.Parameters(index = "0", defaultValue = "world")
        private String parameter;

        @Override
        @Audit
        public List<String> call() {
            return Arrays.asList("test", parameter);
        }
    }

    @Component
    @CommandLine.Command(name = "money")
    static class MoneyCommand implements Callable<String> {

        @Override
        public String call() {
            return "base";
        }

        @Component
        @CommandLine.Command(name = "add")
        static class AddCommand implements Callable<List<String>> {

            @CommandLine.Parameters(index = "0", defaultValue = "world")
            private String parameter;

            @Override
            public List<String> call() {
                return Arrays.asList("add", parameter);
            }
        }

    }

}