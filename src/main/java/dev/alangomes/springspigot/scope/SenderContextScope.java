package dev.alangomes.springspigot.scope;

import dev.alangomes.springspigot.context.Context;
import lombok.val;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.Permissible;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

@Component
@org.springframework.context.annotation.Scope(SCOPE_SINGLETON)
public class SenderContextScope implements Scope, Listener {

    private static final NullSender NULL_SENDER = new NullSender();

    private final Map<Permissible, Map<String, Object>> senderScope = new ConcurrentHashMap<>();

    private final Map<Permissible, Map<String, Runnable>> destructionCallbacks = new ConcurrentHashMap<>();

    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {
        if (Context.getInstance() == null) return null;
        val scope = getCurrentScope();
        return scope.computeIfAbsent(name, (n) -> objectFactory.getObject());
    }

    @Override
    public Object remove(String name) {
        if (Context.getInstance() == null) return null;
        val scope = getCurrentScope();
        val callbacks = getDestructionCallbacks();
        val removed = scope.remove(name);
        callbacks.remove(name).run();
        return removed;
    }

    @Override
    public void registerDestructionCallback(String name, Runnable callback) {
        if (Context.getInstance() == null) return;
        val callbacks = getDestructionCallbacks();
        callbacks.put(name, callback);
    }

    @Override
    public Object resolveContextualObject(String key) {
        return null;
    }

    @Override
    public String getConversationId() {
        return "sender";
    }

    private Map<String, Object> getCurrentScope() {
        val sender = getCurrentSender();
        return senderScope.computeIfAbsent(sender, (s) -> new ConcurrentHashMap<>());
    }

    private Map<String, Runnable> getDestructionCallbacks() {
        val sender = getCurrentSender();
        return destructionCallbacks.computeIfAbsent(sender, (s) -> new ConcurrentHashMap<>());
    }

    private Permissible getCurrentSender() {
        val sender = Context.getInstance().getSender();
        return sender != null ? sender : NULL_SENDER;
    }

    public void clear() {
        if (Context.getInstance() == null) return;
        val sender = getCurrentSender();
        senderScope.remove(sender);
        val callbacks = destructionCallbacks.remove(sender);
        if (callbacks != null) {
            callbacks.values().forEach(Runnable::run);
        }
    }

    @EventHandler
    void onQuit(PlayerQuitEvent event) {
        clear();
    }

}
