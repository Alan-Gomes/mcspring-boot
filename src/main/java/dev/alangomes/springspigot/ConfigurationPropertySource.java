package dev.alangomes.springspigot;

import org.bukkit.configuration.file.FileConfiguration;
import org.springframework.core.env.PropertySource;

class ConfigurationPropertySource extends PropertySource<FileConfiguration> {

    ConfigurationPropertySource(FileConfiguration source) {
        super("config", source);
    }

    @Override
    public Object getProperty(String s) {
        return source.get(s);
    }
}
