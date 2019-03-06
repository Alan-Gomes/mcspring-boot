# Spring Boot Spigot Starter

[![Maven Central](https://img.shields.io/maven-central/v/dev.alangomes/spring-boot-starter-spigot.svg)](https://search.maven.org/#artifactdetails%7Cdev.alangomes%7Cspring-boot-starter-spigot%7C0.1.0%7Cjar)
[![license](https://img.shields.io/github/license/Alan-Gomes/mcspring-boot.svg)](https://github.com/Alan-Gomes/mcspring-boot/blob/master/LICENSE.md)

> A Spring boot starter for Bukkit/Spigot/PaperSpigot plugins

## Features

- Easy setup
- Full [Picocli](http://picocli.info/) `@Command` support (thanks to [picocli-spring-boot-starter](https://github.com/kakawait/picocli-spring-boot-starter)) 
- Secure calls with `@Authorize`
- Automatic `Listener` registration
- Full Spring's dependency injection support
- Easier Bukkit main thread synchronization via `@Synchronize`

## Getting started

Add the Spring boot starter to your project

```xml
<dependency>
  <groupId>dev.alangomes</groupId>
  <artifactId>spring-boot-starter-spigot</artifactId>
  <version>0.1.0</version>
</dependency>
```

Create a class to configure the Spring boot application

```java
@SpringBootApplication(scanBasePackages = "me.test.testplugin")
public class Application {

}
```

Then create the plugin main class using the standard spring initialization, just adding the `SpringSpigotInitializer` initializer.  

```java
public class ExamplePlugin extends JavaPlugin {

    private ConfigurableApplicationContext context;

    @Override
    @SneakyThrows
    public void onEnable() {
        saveDefaultConfig();
        ResourceLoader loader = new DefaultResourceLoader(getClassLoader());
        SpringApplication application = new SpringApplication(loader, Application.class);
        application.addInitializers(new SpringSpigotInitializer(this));
        context = application.run();
    }

    @Override
    public void onDisable() {
        context.close();
        context = null;
    }

}
```

And that's it! Your plugin is ready to use all features from Picocli and Spring Boot!

## Creating a simple command

All commands are based in the [Picocli's API](http://picocli.info/), the only difference is that the classes are
automatically registered at the start of the plugin.
To allow auto registration, you also need to annotate all classes with `@Component`.

Example of a command:

```java
@Component
public class PluginCommands {

    @Component
    @CommandLine.Command(name = "hello")
    public class HelloCommand implements Callable<String> {

        @CommandLine.Parameters(index = "0", defaultValue = "world")
        private String name;

        @Override
        public String call() {
            return "hello " + world;
        }
    }

}
```

If you need the sender of the command, you can also inject via `@Autowired`

```java
@Component
public class PluginCommands {

    @Component
    @CommandLine.Command(name = "heal")
    public class HealCommand implements Runnable {

        @Autowired
        private Player player;

        @Override
        public void run() {
            player.setHealth(20);
        }
    }

}
```

This starter provides some useful beans that you can inject and write code easier:

- `Player` / `CommandSender`: the current sender in the context (see [context](#context))
- `Server`: the server instance
- `Plugin`: your plugin
- `Scheduler`: wrapper around the `BukkitScheduler`

## Retrieving configuration

Is really easy to retrieve configuration properties, you can use Spring's `@Value` annotation, it will automatically
lookup your `config.yml` find the value, otherwise will fallback to the framework's properties.

Example:

```java
@Value("${command.delay}")
private Integer commandDelay;
```

## Securing methods

> "I love writing authentication and authorization code." ~ No Developer Ever.

Creating authorization/permission checking code is really boring, to make it easier, this starter implements the `@Authorize`
annotation, which allows you to define rules to prevent some method to be ran by some players.

The annotation expects a expression in [Spring Expression Language (SpEL)](https://docs.spring.io/spring/docs/3.0.x/reference/expressions.html#expressions-language-ref)
which will be evaluated over the player in the current [context](#context). If the expression evaluates to `false` or there's
no player in the context, the method will automatically throw a `PermissionDeniedException` and `PlayerNotFoundException`, respectively.

Example:

```java
@Authorize("hasPermission('myplugin.dosomething') or getHealth() > 10")
public void doSomething() {
    
}
```

## <a name="context"></a> Understanding contexts

Different from a regular web application, a Bukkit server does not have a concept of session,
all command and event executions are (almost) in the same thread without any definition of the sender in context
(aka who triggered the event/command).

To circumvent this limitation and allow the identification of the sender, this starter implements the `ServerContext`,
a bean that stores senders based on the current thread id. Since almost every execution is in the same thread
(the main server thread), it is safe to store a single user per time, if the execution is asynchronous, it will use another context.

To understand this better, let's take a look on this example:

```java
@Component
class PluginListeners implements Listener {
    
    @Autowired
    private ServerContext serverContext;
    
    @Autowired
    private MyService myService;

    @EventHandler
    void onJoin(PlayerJoinEvent event) {
        serverContext.setSender(event.getPlayer());
        try {
            myService.doSomething();
        } catch (Exception exception) {
            event.getPlayer().sendMessage("failed to do something");
        }
        serverContext.setSender(null);
    }

}
```

As you can see, we set the sender (player) of the current context at the start of the event, and reset the value at the end.
With this setting, the `MyService` and all dependent services will be able to get the player via `@Autowired`, also,
every method containing `@Authorize` or `@Audit` will be able to detect the current player to apply the authorization
rules or to display in the log.

**Important**: the sender of the context must be always set to `null` at the end of the method, otherwise it can lead to
security issues.