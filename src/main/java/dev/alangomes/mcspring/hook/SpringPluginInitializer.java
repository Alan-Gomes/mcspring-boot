package dev.alangomes.mcspring.hook;

import org.bukkit.plugin.java.JavaPlugin;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

class SpringPluginInitializer implements
        ApplicationContextInitializer<ConfigurableApplicationContext> {

    private final JavaPlugin plugin;

    SpringPluginInitializer(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void initialize(ConfigurableApplicationContext applicationContext) {
        applicationContext.getEnvironment().getPropertySources().addFirst(new ConfigurationPropertySource(plugin.getConfig()));
    }
}