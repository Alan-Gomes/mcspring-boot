package dev.alangomes.springspigot;

import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.bukkit.plugin.java.JavaPlugin;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.switchyard.common.type.CompoundClassLoader;

import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Log4j2
public final class SpringSpigotBootstrapper {

    private SpringSpigotBootstrapper() {
    }

    public static ConfigurableApplicationContext initialize(String serverExecutor, JavaPlugin plugin, Class<?> applicationClass) throws ExecutionException, InterruptedException {
        CompoundClassLoader classLoader = new CompoundClassLoader(plugin.getClass().getClassLoader(), Thread.currentThread().getContextClassLoader());
        return initialize(serverExecutor, plugin, classLoader, new SpringApplicationBuilder(applicationClass));
    }

    public static ConfigurableApplicationContext initialize(String serverExecutor, JavaPlugin plugin, SpringApplicationBuilder builder) throws ExecutionException, InterruptedException {
        CompoundClassLoader classLoader = new CompoundClassLoader(plugin.getClass().getClassLoader(), Thread.currentThread().getContextClassLoader());
        return initialize(serverExecutor, plugin, classLoader, builder);
    }

    public static ConfigurableApplicationContext initialize(String serverExecutor, JavaPlugin plugin, ClassLoader classLoader, Class<?> applicationClass) throws ExecutionException, InterruptedException {
        return initialize(serverExecutor, plugin, classLoader, new SpringApplicationBuilder(applicationClass));
    }

    public static ConfigurableApplicationContext initialize(String serverExecutor, JavaPlugin plugin, ClassLoader classLoader, SpringApplicationBuilder builder) throws ExecutionException, InterruptedException {

        switch (serverExecutor
                .toLowerCase(Locale.ROOT)) {
            case "spigot" -> {
                log.info("using spigot server executor");
                val executor = Executors.newSingleThreadExecutor();
                try {
                    Future<ConfigurableApplicationContext> contextFuture = executor.submit(() -> initializeByExecutor(plugin, classLoader, builder));
                    return contextFuture.get();
                } finally {
                    executor.shutdown();
                }
            }
            case "paper" -> {
                log.info("using paper server executor");
                return initializeByExecutor(plugin, classLoader, builder);
            }
            default -> {
                log.error("Server executor not found, using default server executer");
                return initializeByExecutor(plugin, classLoader, builder);
            }
        }

    }

    private static ConfigurableApplicationContext initializeByExecutor(JavaPlugin plugin, ClassLoader classLoader, SpringApplicationBuilder builder) {

        Thread.currentThread().setContextClassLoader(classLoader);

        val props = new Properties();
        try {
            props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties"));
        } catch (Exception ignored) {
        }

        if (builder.application().getResourceLoader() == null) {
            val loader = new DefaultResourceLoader(classLoader);
            builder.resourceLoader(loader);
        }
        return builder
                .properties(props)
                .initializers(new SpringSpigotInitializer(plugin))
                .run();
    }


}
