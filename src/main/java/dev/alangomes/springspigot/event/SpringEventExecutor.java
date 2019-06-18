package dev.alangomes.springspigot.event;

import dev.alangomes.springspigot.context.Context;
import lombok.SneakyThrows;
import lombok.val;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
public class SpringEventExecutor {

    @Autowired
    private Context context;

    public EventExecutor create(Method method) {
        val eventType = method.getParameters()[0].getType();
        return (listener, event) -> {
            if (!eventType.isInstance(event)) return;
            context.runWithSender(EventUtil.getSender(event), () -> {
                triggerEvent(method, listener, event);
            });
        };
    }

    @SneakyThrows
    private void triggerEvent(Method method, Listener listener, Event event) {
        AopUtils.invokeJoinpointUsingReflection(listener, method, new Object[] {event});
    }
}
