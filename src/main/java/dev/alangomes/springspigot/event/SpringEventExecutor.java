package dev.alangomes.springspigot.event;

import dev.alangomes.springspigot.context.Context;
import lombok.SneakyThrows;
import org.bukkit.Server;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
public class SpringEventExecutor implements EventExecutor {

    @Autowired
    private EventScanner eventScanner;

    @Autowired
    private Context context;

    @Autowired
    private Server server;

    @Override
    public void execute(Listener listener, Event event) {
        context.runWithSender(EventUtil.getSender(event), () -> {
            eventScanner.getListenerMethods(listener)
                    .filter(method -> method.getParameters()[0].getType().isInstance(event))
                    .forEach(method -> triggerEvent(method, listener, event));
        });
    }

    @SneakyThrows
    private void triggerEvent(Method method, Listener listener, Event event) {
        AopUtils.invokeJoinpointUsingReflection(listener, method, new Object[] {event});
    }
}
