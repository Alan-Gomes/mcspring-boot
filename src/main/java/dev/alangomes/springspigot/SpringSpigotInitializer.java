package dev.alangomes.springspigot;

import org.bukkit.plugin.java.JavaPlugin;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;

import java.util.Properties;

public class SpringSpigotInitializer implements
        ApplicationContextInitializer<ConfigurableApplicationContext> {

    private final JavaPlugin plugin;

    public SpringSpigotInitializer(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void initialize(ConfigurableApplicationContext context) {
        MutablePropertySources propertySources = context.getEnvironment().getPropertySources();
        propertySources.addFirst(new ConfigurationPropertySource(plugin.getConfig()));

        Properties props = new Properties();
        props.put("spigot.plugin", plugin.getName());
        props.put("spigot.messages.command_error", "&cAn internal error occurred while attemping to perform this command");
        props.put("spigot.messages.missing_parameter_error", "&cMissing parameter: %s");
        props.put("spigot.messages.parameter_error", "&cInvalid parameter: %s");
        propertySources.addLast(new PropertiesPropertySource("spring-bukkit", props));
    }

}