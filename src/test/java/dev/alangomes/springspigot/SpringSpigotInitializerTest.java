package dev.alangomes.springspigot;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SpringSpigotInitializerTest {

    private static final String PLUGIN_NAME = "TestPlugin";

    @Mock
    private Plugin plugin;

    @Mock
    private ConfigurableApplicationContext context;

    @Mock
    private MutablePropertySources propertySources;

    @Captor
    private ArgumentCaptor<PropertiesPropertySource> propertySourceCaptor;

    private SpringSpigotInitializer initializer;

    @Before
    public void setup() {
        initializer = new SpringSpigotInitializer(plugin);

        ConfigurableEnvironment environment = mock(ConfigurableEnvironment.class);
        when(context.getEnvironment()).thenReturn(environment);
        when(environment.getPropertySources()).thenReturn(propertySources);

        when(plugin.getConfig()).thenReturn(mock(FileConfiguration.class));
        when(plugin.getName()).thenReturn(PLUGIN_NAME);
    }

    @Test
    public void shouldRegisterPluginConfigurationPropertySource() {
        initializer.initialize(context);

        verify(propertySources).addLast(any(ConfigurationPropertySource.class));
    }

    @Test
    public void shouldRegisterHookProperties() {
        initializer.initialize(context);

        verify(propertySources, times(2)).addLast(propertySourceCaptor.capture());

        PropertiesPropertySource propertySource = propertySourceCaptor.getValue();
        Map<String, Object> props = propertySource.getSource();

        assertEquals(PLUGIN_NAME, props.get("spigot.plugin"));
    }

}