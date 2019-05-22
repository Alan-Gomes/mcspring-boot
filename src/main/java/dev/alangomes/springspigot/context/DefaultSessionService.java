package dev.alangomes.springspigot.context;

import dev.alangomes.springspigot.util.ServerUtil;
import lombok.val;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

@Component
@Scope(SCOPE_SINGLETON)
public class DefaultSessionService implements SessionService, Listener {

    @Autowired
    private Context context;

    @Autowired
    private ServerUtil serverUtil;

    private final Map<String, Map<String, Object>> sessions = new ConcurrentHashMap<>();

    @Override
    public Map<String, Object> current() {
        return of(context.getSender());
    }

    @Override
    public Map<String, Object> of(CommandSender sender) {
        val senderId = serverUtil.getSenderId(sender);
        if (senderId == null) {
            return null;
        }
        return sessions.computeIfAbsent(senderId, k -> new ConcurrentHashMap<>());
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        val senderId = serverUtil.getSenderId(event.getPlayer());
        sessions.remove(senderId);
    }

}
