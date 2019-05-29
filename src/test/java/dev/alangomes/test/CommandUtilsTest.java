package dev.alangomes.test;

import dev.alangomes.springspigot.command.Subcommand;
import dev.alangomes.springspigot.picocli.CommandLineDefinition;
import dev.alangomes.springspigot.util.CommandUtils;
import dev.alangomes.test.util.SpringSpigotTestInitializer;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(
        classes = {TestApplication.class, CommandUtilsTest.BaseCommand.class, CommandUtilsTest.ValueSuggestions.class,
                CommandUtilsTest.ExternalSubcommand.class, CommandUtilsTest.NestedSubcommand.class},
        initializers = SpringSpigotTestInitializer.class
)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class CommandUtilsTest {

    @Autowired
    private CommandLineDefinition commandLineDefinition;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Server server;

    private CommandSpec commandSpec;

    @Before
    public void init() {
        commandSpec =
                commandLineDefinition.build(applicationContext).getSubcommands().values().iterator().next().getCommandSpec();

        World firstWorld = mock(World.class);
        when(firstWorld.getName()).thenReturn("first_world");
        World secondWorld = mock(World.class);
        when(secondWorld.getName()).thenReturn("second_world");
        when(server.getWorlds()).thenReturn(Arrays.asList(firstWorld, secondWorld));

        Player player1 = mock(Player.class);
        when(player1.getName()).thenReturn("some_player");
        Player player2 = mock(Player.class);
        when(player2.getName()).thenReturn("other_player");
        doReturn(Arrays.asList(player1, player2)).when(server).getOnlinePlayers();
    }

    @Test
    public void shouldSuggestAllFirstLevelSubcommands() {
        List<String> suggestions =
                CommandUtils.getPossibleSubcommands(commandSpec, new String[]{""}).collect(Collectors.toList());

        assertEquals(2, suggestions.size());
        assertThat(suggestions, containsInAnyOrder("inner", "external"));
    }

    @Test
    public void shouldSuggestNestedSubcommands() {
        List<String> suggestions =
                CommandUtils.getPossibleSubcommands(commandSpec, new String[]{"exTernal", ""}).collect(Collectors.toList());

        assertEquals(1, suggestions.size());
        assertThat(suggestions, containsInAnyOrder("nested"));
    }

    @Test
    public void shouldSuggestFirstLevelSubcommandsWhichStartsWithValue() {
        List<String> suggestions =
                CommandUtils.getPossibleSubcommands(commandSpec, new String[]{"eXt"}).collect(Collectors.toList());

        assertEquals(1, suggestions.size());
        assertThat(suggestions, containsInAnyOrder("external"));
    }

    @Test
    public void shouldNotSuggestUnknownSubcommands() {
        List<String> suggestions =
                CommandUtils.getPossibleSubcommands(commandSpec, new String[]{"unk"}).collect(Collectors.toList());

        assertTrue(suggestions.isEmpty());
    }

    @Test
    public void shouldSuggestRootPositionalArgument() {
        List<String> suggestions =
                CommandUtils.getPossibleArguments(commandSpec, new String[]{""}).collect(Collectors.toList());

        assertEquals(2, suggestions.size());
        assertThat(suggestions, containsInAnyOrder("some_player", "other_player"));
    }

    @Test
    public void shouldSuggestRootPositionalArgumentWhichStartsWithValue() {
        List<String> suggestions =
                CommandUtils.getPossibleArguments(commandSpec, new String[]{"sOm"}).collect(Collectors.toList());

        assertEquals(1, suggestions.size());
        assertThat(suggestions, containsInAnyOrder("some_player"));
    }

    @Test
    public void shouldSuggestInnerPositionalArgument() {
        List<String> suggestions =
                CommandUtils.getPossibleArguments(commandSpec, new String[]{"iNner", ""}).collect(Collectors.toList());

        assertEquals(2, suggestions.size());
        assertThat(suggestions, containsInAnyOrder("first_world", "second_world"));
    }

    @Test
    public void shouldSuggestInnerPositionalArgumentThatStartsWithValue() {
        List<String> suggestions =
                CommandUtils.getPossibleArguments(commandSpec, new String[]{"iNner", "fiR"}).collect(Collectors.toList());

        assertEquals(1, suggestions.size());
        assertThat(suggestions, containsInAnyOrder("first_world"));
    }

    @Test
    public void shouldSuggestSecondInnerPositionalArgument() {
        List<String> suggestions =
                CommandUtils.getPossibleArguments(commandSpec, new String[]{"iNner", "first_woRld", ""}).collect(Collectors.toList());

        assertEquals(2, suggestions.size());
        assertThat(suggestions, containsInAnyOrder("some_player", "other_player"));
    }

    @Test
    public void shouldRespectCompletionCandidates() {
        List<String> suggestions =
                CommandUtils.getPossibleArguments(commandSpec, new String[]{"external", "nested", ""}).collect(Collectors.toList());

        assertEquals(2, suggestions.size());
        assertThat(suggestions, containsInAnyOrder("first_suggestion", "second_suggestion"));
    }

    @Test
    public void shouldSuggestionsCompletionCandidatesWhichStartsWithValue() {
        List<String> suggestions =
                CommandUtils.getPossibleArguments(commandSpec, new String[]{"external", "nested", "firSt"}).collect(Collectors.toList());

        assertEquals(1, suggestions.size());
        assertThat(suggestions, containsInAnyOrder("first_suggestion"));
    }

    @Test
    public void shouldNotSuggestArgumentsForUnknownSubcommands() {
        List<String> suggestions =
                CommandUtils.getPossibleArguments(commandSpec, new String[]{"unk"}).collect(Collectors.toList());

        assertTrue(suggestions.isEmpty());
    }

    @Component
    @CommandLine.Command(
            name = "command",
            subcommands = {ExternalSubcommand.class}
    )
    static class BaseCommand {

        @CommandLine.Parameters(index = "0")
        private Player parameter;

        @Component
        @CommandLine.Command(name = "inner")
        static class InnerSubcommand {

            @CommandLine.Parameters(index = "0")
            private World parameter;

            @CommandLine.Parameters(index = "1")
            private Player parameter2;

        }

    }

    @Subcommand
    @CommandLine.Command(name = "external", subcommands = {NestedSubcommand.class})
    static class ExternalSubcommand {

        @CommandLine.Parameters(index = "0")
        private Player parameter;

    }

    @Subcommand
    @CommandLine.Command(name = "nested")
    static class NestedSubcommand {

        @CommandLine.Parameters(index = "0", completionCandidates = ValueSuggestions.class)
        private String parameter;

    }

    @Component
    static class ValueSuggestions implements Iterable<String> {

        @Override
        public Iterator<String> iterator() {
            return Arrays.asList("first_suggestion", "second_suggestion").iterator();
        }
    }

}