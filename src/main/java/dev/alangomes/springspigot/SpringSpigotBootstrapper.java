package dev.alangomes.springspigot;

import lombok.val;
import org.bukkit.plugin.java.JavaPlugin;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.switchyard.common.type.CompoundClassLoader;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public final class SpringSpigotBootstrapper {

    private SpringSpigotBootstrapper() {
    }

    public static ConfigurableApplicationContext initialize(JavaPlugin plugin, Class<?> applicationClass) throws ExecutionException, InterruptedException {
        CompoundClassLoader classLoader = new CompoundClassLoader(plugin.getClass().getClassLoader(), Thread.currentThread().getContextClassLoader());
        return initialize(plugin, classLoader, new SpringApplicationBuilder(applicationClass));
    }

    public static ConfigurableApplicationContext initialize(JavaPlugin plugin, SpringApplicationBuilder builder) throws ExecutionException, InterruptedException {
        CompoundClassLoader classLoader = new CompoundClassLoader(plugin.getClass().getClassLoader(), Thread.currentThread().getContextClassLoader());
        return initialize(plugin, classLoader, builder);
    }

    public static ConfigurableApplicationContext initialize(JavaPlugin plugin, ClassLoader classLoader, Class<?> applicationClass) throws ExecutionException, InterruptedException {
        return initialize(plugin, classLoader, new SpringApplicationBuilder(applicationClass));
    }

    public static ConfigurableApplicationContext initialize(JavaPlugin plugin, ClassLoader classLoader, SpringApplicationBuilder builder) throws ExecutionException, InterruptedException {
        val executor = Executors.newSingleThreadExecutor();
        try {
            Future<ConfigurableApplicationContext> contextFuture = executor.submit(() -> {
                Thread.currentThread().setContextClassLoader(classLoader);

                val props = new Properties();
                try {
                    props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("application-local.properties"));
                    props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("application-local.yml"));
                    props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("application-dev.properties"));
                    props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("application-dev.yml"));
                    props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("application-prod.properties"));
                    props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("application-prod.yml"));
                    props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("application-test.properties"));
                    props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("application-test.yml"));
                    props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties"));
                    props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("application.yml"));
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
            });
            return contextFuture.get();
        } finally {
            executor.shutdown();
        }
    }


}
