package dev.alangomes.springspigot;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@Scope("singleton")
class SpringSpigotStartupHook {

    @Value("${spigot.plugin}")
    private String pluginName;

    @Autowired
    private Server server;

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    private boolean initialized = false;

    @EventListener
    void onStartup(ContextRefreshedEvent event) {
        if (initialized) return;
        initialized = true;
        Plugin plugin = server.getPluginManager().getPlugin(pluginName);
        Collection<Listener> beans = applicationContext.getBeansOfType(Listener.class).values();
        beans.forEach(bean -> Bukkit.getPluginManager().registerEvents(bean, plugin));
    }

    @Bean
    public static BeanFactoryPostProcessor scopeBeanFactoryPostProcessor() {
        return new ScopePostProcessor();
    }
}