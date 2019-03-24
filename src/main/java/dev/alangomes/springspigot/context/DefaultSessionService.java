package dev.alangomes.springspigot.context;

import dev.alangomes.springspigot.exception.PlayerNotFoundException;
import lombok.val;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Component
@Scope("singleton")
public class DefaultSessionService implements SessionService, Listener {

    @Autowired
    private Context context;

    private final Map<String, Map<String, Object>> sessions = new ConcurrentHashMap<>();

    @Override
    public void set(String key, Object value) {
        getSession().put(key, value);
    }

    @Override
    public Object get(String key) {
        return getSession().get(key);
    }

    @Override
    public void clear() {
        sessions.remove(context.getSenderId());
    }

    @Override
    public <V> V computeIfAbsent(String key, Function<String, ? extends V> function) {
        return (V) getSession().computeIfAbsent(key, function);
    }

    private Map<String, Object> getSession() {
        val senderId = context.getSenderId();
        if (senderId == null) {
            throw new PlayerNotFoundException();
        }
        return sessions.computeIfAbsent(senderId, k -> new ConcurrentHashMap<>());
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        context.runWithSender(event.getPlayer(), this::clear);
    }

}
