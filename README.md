# Spring Boot Spigot Starter

[![Maven Central](https://img.shields.io/maven-central/v/dev.alangomes/spigot-spring-boot-starter.svg)](https://search.maven.org/#artifactdetails%7Cdev.alangomes%7Cspigot-spring-boot-starter%7C0.6.0%7Cjar)
[![License](https://img.shields.io/github/license/Alan-Gomes/mcspring-boot.svg?style=popout)](https://github.com/Alan-Gomes/mcspring-boot/blob/master/LICENSE)

> A Spring boot starter for Bukkit/Spigot/PaperSpigot plugins

## Features

- Easy setup
- Full [Picocli](http://picocli.info/) `@Command` support (thanks to [picocli-spring-boot-starter](https://github.com/kakawait/picocli-spring-boot-starter)) 
- Secure calls with `@Authorize`
- Automatic `Listener` registration
- Full Spring's dependency injection support
- Easier Bukkit main thread synchronization via `@Synchronize`
- Support Spring scheduler on the bukkit main thread (`@Scheduled`)

## Getting started

Add the Spring boot starter to your project

```xml
<dependency>
  <groupId>dev.alangomes</groupId>
  <artifactId>spigot-spring-boot-starter</artifactId>
  <version>0.6.0</version>
</dependency>
```

Create a class to configure the Spring boot application

```java
@SpringBootApplication(scanBasePackages = "me.test.testplugin")
public class Application {

}
```

Then create the plugin main class using the standard spring initialization, just adding the `SpringSpigotInitializer` initializer.  

<a name="initialization"></a> 
```java
public class ExamplePlugin extends JavaPlugin {

    private ConfigurableApplicationContext context;

    @Override
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
@CommandLine.Command(name = "hello")
public class HelloCommand implements Callable<String> {

    @CommandLine.Parameters(index = "0", defaultValue = "world")
    private String name;

    @Override
    public String call() {
        return "hello " + name;
    }
}
```

If you need the sender of the command, you can also retrieve it in the `Context`:

```java
@Component
@CommandLine.Command(name = "heal")
public class HealCommand implements Runnable {

    @Autowired
    private Context context;

    @Override
    public void run() {
        Player player = context.getPlayer();
        player.setHealth(20);
    }
}
```

In addition, you can also inject the `Plugin` and `Server` instances via `@Autowired`

## Retrieving configuration

Is really easy to retrieve configuration properties, you can use the `@DynamicValue` annotation, it will automatically
lookup your `config.yml` to find the value, otherwise will fallback to the framework's properties.

Example:

```java
@DynamicValue("${command.delay}")
private Instance<Integer> commandDelay;
```

You can also use Spring built-in `@Value` annotation, which works the same way, but doesn't support configuration reloading. 

### Disabling support for configuration

Sometimes you want to make a simple plugin without any configuration, if so, the starter will complain about the null config.
To fix this you should pass a second `boolean` parameter to `SpringSpigotInitializer` during the [initialization](#initialization).
If the parameter is `false`, it means that your plugin does not need this support and the starter will make no attempt
to register it. Example:

```java
application.addInitializers(new SpringSpigotInitializer(this, false));
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

To circumvent this limitation and allow the identification of the sender, this starter implements the `Context`,
a bean that stores senders based on the current thread id. Since almost every execution is in the same thread
(the main server thread), it is safe to store a single user per time, if the execution is asynchronous, it will use another context.

To understand this better, let's take a look on this example:

```java
@Component
class PluginListeners implements Listener {
    
    @Autowired
    private Context context;
    
    @Autowired
    private MyService myService;

    @EventHandler
    void onJoin(PlayerJoinEvent event) {
        context.runWithSender(e.getPlayer(), () -> {
            myService.doSomething();
        });
        // or, if you are already familiar with Java 8 method references
        context.runWithSender(e.getPlayer(), myService::doSomething);
    }

}
```

As you can see, we set the sender (player) of the current context at the start of the event, and reset the value at the end.
With this setting, the `MyService` and all dependent services will be able to get the player via `@Autowired`, also,
every method containing `@Authorize` or `@Audit` will be able to detect the current player to apply the authorization
rules or to display in the log.

## Synchronization

In a regular Bukkit plugin, you don't need to care about threads and synchronization. But if you want to run something
outside the server main thread, like in `BukkitScheduler#runTaskAsynchronously​()`, every access to Bukkit API needs to be
synchronized to make sure you don't get a `IllegalStateException`.

To make this synchronization easier (without using schedulers and creating runnables), you can simply use the `@Synchronize`
annotation, which automatically schedules all calls under the hood if they are not in the main thread, passing through otherwise.

**Important things if the call get scheduled**:
- The return value of the method will **always** be `null`, so make sure your code is null-safe.
- The method will **not** run immediately, it will on the next server tick, so make sure your code don't rely on that.