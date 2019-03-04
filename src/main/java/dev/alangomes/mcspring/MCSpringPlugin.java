package dev.alangomes.mcspring;

import dev.alangomes.mcspring.hook.SpringPluginHook;
import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;
import org.springframework.boot.SpringApplication;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

public class MCSpringPlugin extends JavaPlugin {

    private SpringPluginHook hook;

    @Override
    @SneakyThrows
    public void onEnable() {
        saveDefaultConfig();
        Class.forName("org.postgresql.Driver");
        ResourceLoader loader = new DefaultResourceLoader(getClassLoader());
        SpringApplication application = new SpringApplication(loader, ApplicationConfiguration.class);
        hook = SpringPluginHook.hook(application, this);
    }

    @Override
    public void onDisable() {
        hook.close();
        hook = null;
    }

}