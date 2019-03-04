package dev.alangomes.mcspring.hook;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Collection;

public class SpringPluginHook {

    private final JavaPlugin plugin;
    private final SpringApplication application;
    private ConfigurableApplicationContext context;

    private SpringPluginHook(SpringApplication application, JavaPlugin plugin) {
        this.plugin = plugin;
        this.application = application;
    }

    public static SpringPluginHook hook(SpringApplication application, JavaPlugin plugin) {
        SpringPluginHook hook = new SpringPluginHook(application, plugin);
        hook.init();
        return hook;
    }

    private void init() {
        application.addInitializers(new SpringPluginInitializer(plugin));
        context = application.run();
        Collection<Listener> beans = context.getBeansOfType(Listener.class).values();
        beans.forEach(bean -> Bukkit.getPluginManager().registerEvents(bean, plugin));
    }

    public void close() {
        context.close();
        HandlerList.unregisterAll(plugin);
    }

}
