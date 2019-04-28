package dev.alangomes.test.util;

import dev.alangomes.springspigot.SpringSpigotInitializer;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;

import java.lang.reflect.Field;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SpringSpigotTestInitializer extends SpringSpigotInitializer {

    private static final String PLUGIN_NAME = "TestPlugin";

    public SpringSpigotTestInitializer() {
        super(mockPlugin());
    }

    private static Plugin mockPlugin() {
        Plugin plugin = mock(Plugin.class);
        when(plugin.getName()).thenReturn(PLUGIN_NAME);

        Server server = mockServer();
        when(plugin.getServer()).thenReturn(server);

        FileConfiguration config = mockConfig();
        when(plugin.getConfig()).thenReturn(config);

        when(server.getPluginManager().getPlugin(PLUGIN_NAME)).thenReturn(plugin);
        return plugin;
    }

    private static Server mockServer() {
        Server server = mock(Server.class);

        PluginManager pluginManager = mock(PluginManager.class);
        when(server.getPluginManager()).thenReturn(pluginManager);

        BukkitScheduler scheduler = mock(BukkitScheduler.class);
        when(server.getScheduler()).thenReturn(scheduler);

        setServer(server);
        return server;
    }

    private static FileConfiguration mockConfig() {
        FileConfiguration config = mock(FileConfiguration.class);
        when(config.get(any())).thenReturn(null);
        return config;
    }

    private static void setServer(Server server) {
        try {
            Field serverField = Bukkit.class.getDeclaredField("server");
            serverField.setAccessible(true);
            serverField.set(null, server);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}
