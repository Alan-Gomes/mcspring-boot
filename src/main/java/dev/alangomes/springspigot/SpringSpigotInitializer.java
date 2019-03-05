package dev.alangomes.springspigot;

import org.bukkit.plugin.java.JavaPlugin;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MutablePropertySources;
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
        MutablePropertySources propertySources = context.getEnvironment().getPropertySources();
        if (configAvailable) {
            propertySources.addFirst(new ConfigurationPropertySource(plugin.getConfig()));
        }

        Properties props = new Properties();
        props.put("spigot.plugin", plugin.getName());
        props.put("spigot.messages.command_error", "&cAn internal error occurred while attemping to perform this command");
        props.put("spigot.messages.missing_parameter_error", "&cMissing parameter: %s");
        props.put("spigot.messages.parameter_error", "&cInvalid parameter: %s");
        propertySources.addLast(new PropertiesPropertySource("spring-bukkit", props));
    }

}