package dev.alangomes.springspigot;

import lombok.val;
import org.bukkit.plugin.Plugin;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.PropertiesPropertySource;

import java.util.Properties;

/**
 * Initializer that set core properties and adds config yml source
 */
public class SpringSpigotInitializer implements
        ApplicationContextInitializer<ConfigurableApplicationContext> {

    private final Plugin plugin;

    public SpringSpigotInitializer(Plugin plugin) {
        this.plugin = plugin;
    }

    public void initialize(ConfigurableApplicationContext context) {
        val propertySources = context.getEnvironment().getPropertySources();
        propertySources.addLast(new ConfigurationPropertySource(plugin.getConfig()));

        val props = new Properties();
        props.put("spigot.plugin", plugin.getName());
        propertySources.addLast(new PropertiesPropertySource("spring-bukkit", props));
    }

}