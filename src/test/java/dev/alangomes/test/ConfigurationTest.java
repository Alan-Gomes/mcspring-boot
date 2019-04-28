package dev.alangomes.test;

import dev.alangomes.springspigot.configuration.DynamicValue;
import dev.alangomes.springspigot.configuration.Instance;
import dev.alangomes.test.util.SpringSpigotTestInitializer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(
        classes = TestApplication.class,
        initializers = SpringSpigotTestInitializer.class
)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class ConfigurationTest {

    @DynamicValue("${command.message}")
    private Instance<String> commandMessage;

    @DynamicValue("${command.message_list}")
    private Instance<List<String>> commandMessageList;

    @Autowired
    private Plugin plugin;

    private FileConfiguration configuration;

    @Before
    public void setup() {
        configuration = plugin.getConfig();
    }

    @Test
    public void shouldRetrieveConfigurationFromPlugin() {
        when(configuration.get("command.message")).thenReturn("test message");

        String message = commandMessage.get();

        assertEquals("test message", message);
    }

    @Test
    public void shouldReevaluateConfigurationOnEachCall() {
        when(configuration.get("command.message")).thenReturn("test message");

        commandMessage.get();
        commandMessage.get();

        verify(configuration, times(2)).get("command.message");
    }

    @Test
    public void shouldConvertValueBasedOnGenericType() {
        when(configuration.get("command.message_list")).thenReturn("message1,message2");

        List<String> messages = commandMessageList.get();

        assertEquals(2, messages.size());
        assertEquals("message1", messages.get(0));
        assertEquals("message2", messages.get(1));
    }


}