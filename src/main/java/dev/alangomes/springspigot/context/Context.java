package dev.alangomes.springspigot.context;

import dev.alangomes.springspigot.util.ServerUtil;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.val;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

@Component
@Scope(SCOPE_SINGLETON)
public class Context {

    @Getter
    private static Context instance;

    @Autowired
    private ServerUtil serverUtil;

    private final Map<Long, CommandSender> senderRefs = new ConcurrentHashMap<>();

    @PostConstruct
    void init() {
        instance = this;
    }

    @PreDestroy
    void destroy() {
        instance = null;
    }

    /**
     * Set the current sender in the thread context.
     *
     * @param sender The new {@link org.bukkit.command.CommandSender} of the context.
     */
    void setSender(CommandSender sender) {
        val threadId = Thread.currentThread().getId();
        if (sender == null) {
            senderRefs.remove(threadId);
            return;
        }
        senderRefs.put(threadId, sender);
    }

    /**
     * Convenience method to return the current sender as a {@link org.bukkit.entity.Player}
     *
     * @return the current {@link org.bukkit.entity.Player} in the context if present, {@code null} otherwise
     */
    public Player getPlayer() {
        val sender = getSender();
        return sender instanceof Player ? (Player) sender : null;
    }

    /**
     * Retrieve the current {@param sender} of the context.
     *
     * @return The current sender of the context.
     */
    public CommandSender getSender() {
        return senderRefs.get(Thread.currentThread().getId());
    }

    /**
     * Get the most unique id available for the player in the context.
     * see {@link dev.alangomes.springspigot.util.ServerUtil#getSenderId}
     *
     * @return the sender id
     */
    public String getSenderId() {
        return serverUtil.getSenderId(getSender());
    }

    /**
     * Run a {@param function} with a specific {@param sender} in the context
     *
     * @param sender   The sender to be set at the context
     * @param function The code to be executed
     * @return the value returned by the function
     */
    public <T, S extends CommandSender> T runWithSender(S sender, Function<S, T> function) {
        val oldSender = getSender();
        setSender(sender);
        try {
            return function.apply(sender);
        } finally {
            setSender(oldSender);
        }
    }

    /**
     * Run a {@param function} with a specific {@param sender} in the context
     *
     * @param sender   The sender to be set at the context
     * @param supplier The code to be executed
     * @return the value returned by the function
     */
    public <T, S extends CommandSender> T runWithSender(S sender, Supplier<T> supplier) {
        return runWithSender(sender, (Function<S, T>) (s) -> supplier.get());
    }


    /**
     * Run a {@param function} with a specific {@param sender} in the context
     *
     * @param sender   The sender to be set at the context
     * @param function The code to be executed
     */
    public <S extends CommandSender> void runWithSender(S sender, Consumer<S> function) {
        runWithSender(sender, (s) -> {
            function.accept(s);
            return null;
        });
    }

    /**
     * Run a {@param runnable} with a specific {@param sender} in the context
     *
     * @param sender   The sender to be set at the context
     * @param runnable The code to be executed
     */
    public <S extends CommandSender> void runWithSender(S sender, Runnable runnable) {
        runWithSender(sender, (s) -> {
            runnable.run();
            return null;
        });
    }

    /**
     * Wrap a {@param supplier} to keep the current context
     *
     * @param supplier The supplier to be wrapped
     * @return The wrapped supplier
     */
    public <T> Supplier<T> wrap(Supplier<T> supplier) {
        val senderId = getSenderId();
        return () -> runWithSender(serverUtil.getSenderFromId(senderId), supplier);
    }

    /**
     * Wrap a {@param callable} to keep the current context
     *
     * @param callable The callable to be wrapped
     * @return The wrapped callable
     */
    public <T> Callable<T> wrap(Callable<T> callable) {
        val senderId = getSenderId();
        return () -> runWithSender(serverUtil.getSenderFromId(senderId), () -> call(callable));
    }

    @SneakyThrows
    private <V> V call(Callable<V> callable) {
        return callable.call();
    }

    /**
     * Wrap a {@param runnable} to keep the current context
     *
     * @param runnable The runnable to be wrapped
     * @return The wrapped runnable
     */
    public Runnable wrap(Runnable runnable) {
        val senderId = getSenderId();
        return () -> runWithSender(serverUtil.getSenderFromId(senderId), runnable);
    }

    /**
     * Wrap a {@param consumer} to keep the current context
     *
     * @param consumer The consumer to be wrapped
     * @return The wrapped consumer
     */
    public <T> Consumer<T> wrap(Consumer<T> consumer) {
        val senderId = getSenderId();
        return (v) -> runWithSender(serverUtil.getSenderFromId(senderId), () -> consumer.accept(v));
    }

    /**
     * Wrap a {@param function} to keep the current context
     *
     * @param function The function to be wrapped
     * @return The wrapped function
     */
    public <T, R> Function<T, R> wrap(Function<T, R> function) {
        val senderId = getSenderId();
        return (v) -> runWithSender(serverUtil.getSenderFromId(senderId), () -> function.apply(v));
    }

}
