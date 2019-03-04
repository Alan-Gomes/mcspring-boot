package dev.alangomes.mcspring;

import dev.alangomes.mcspring.hook.SpringPluginHook;
import org.bukkit.plugin.java.JavaPlugin;
import org.springframework.boot.SpringApplication;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

public class MCSpringPlugin extends JavaPlugin {

    private SpringPluginHook hook;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        ResourceLoader loader = new DefaultResourceLoader(getClassLoader());
        SpringApplication application = new SpringApplication(loader, ApplicationConfig.class);
        hook = SpringPluginHook.hook(application, this);
    }

    @Override
    public void onDisable() {
        hook.close();
        hook = null;
    }

}