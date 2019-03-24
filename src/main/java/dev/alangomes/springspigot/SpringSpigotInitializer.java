package dev.alangomes.springspigot;

import lombok.val;
import org.bukkit.plugin.java.JavaPlugin;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.PropertiesPropertySource;

import java.util.Properties;

/**
 * Initializer that set core properties and adds config yml source
 */
public class SpringSpigotInitializer implements
        ApplicationContextInitializer<ConfigurableApplicationContext> {

    private final JavaPlugin plugin;
    private final boolean configAvailable;

    public SpringSpigotInitializer(JavaPlugin plugin, boolean configAvailable) {
        this.plugin = plugin;
        this.configAvailable = configAvailable;
    }

    public SpringSpigotInitializer(JavaPlugin plugin) {
        this(plugin, true);
    }

    public void initialize(ConfigurableApplicationContext context) {
        val propertySources = context.getEnvironment().getPropertySources();
        if (configAvailable) {
            propertySources.addFirst(new ConfigurationPropertySource(plugin.getConfig()));
        }

        val props = new Properties();
        props.put("spigot.plugin", plugin.getName());
        propertySources.addLast(new PropertiesPropertySource("spring-bukkit", props));
    }

}