package dev.alangomes.springspigot.context;

import dev.alangomes.springspigot.exception.PlayerNotFoundException;
import lombok.val;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Scope("singleton")
public class DefaultSessionService implements SessionService, Listener {

    @Autowired
    private Context context;

    private final Map<String, Map<String, Object>> sessions = new ConcurrentHashMap<>();

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

    @Override
    public int size() {
        return getSession().size();
    }

    @Override
    public boolean isEmpty() {
        return getSession().isEmpty();
    }

    @Override
    public boolean containsKey(Object o) {
        return getSession().containsKey(o);
    }

    @Override
    public boolean containsValue(Object o) {
        return getSession().containsValue(o);
    }

    @Override
    public Object get(Object o) {
        return getSession().get(o);
    }

    @Override
    public Object put(String s, Object o) {
        return getSession().put(s, o);
    }

    @Override
    public Object remove(Object o) {
        return getSession().remove(o);
    }

    @Override
    public void putAll(Map<? extends String, ?> map) {
        getSession().putAll(map);
    }

    @Override
    public void clear() {
        getSession().clear();
    }

    @Override
    public Set<String> keySet() {
        return getSession().keySet();
    }

    @Override
    public Collection<Object> values() {
        return getSession().values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return getSession().entrySet();
    }

}
