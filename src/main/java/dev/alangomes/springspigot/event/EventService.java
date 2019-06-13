package dev.alangomes.springspigot.event;

import lombok.val;
import org.bukkit.Server;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Stream;

@Service
public class EventService {

    @Autowired
    private SpringEventExecutor eventExecutor;

    @Autowired
    private Server server;

    @Autowired
    private Plugin plugin;

    public void registerEvents(Listener listener) {
        getListenerMethods(listener).forEach(method -> registerEvents(listener, method));
    }

    private void registerEvents(Listener listener, Method method) {
        val handler = method.getAnnotation(EventHandler.class);
        val eventType = (Class<? extends Event>) method.getParameters()[0].getType();
        server.getPluginManager().registerEvent(eventType, listener, handler.priority(), eventExecutor.create(method), plugin, handler.ignoreCancelled());
    }

    private Stream<Method> getListenerMethods(Listener listener) {
        val target = AopUtils.getTargetClass(listener);
        return Arrays.stream(ReflectionUtils.getAllDeclaredMethods(target))
                .filter(method -> method.isAnnotationPresent(EventHandler.class))
                .filter(method -> method.getParameters().length == 1)
                .filter(method -> Event.class.isAssignableFrom(method.getParameters()[0].getType()));
    }

}
