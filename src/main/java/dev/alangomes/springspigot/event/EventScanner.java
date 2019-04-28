package dev.alangomes.springspigot.event;

import lombok.val;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.springframework.aop.support.AopUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Stream;

@Component
class EventScanner {

    public Stream<Method> getListenerMethods(Listener listener) {
        val target = AopUtils.getTargetClass(listener);
        return Arrays.stream(target.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(EventHandler.class))
                .filter(method -> method.getParameters().length == 1)
                .filter(method -> Event.class.isAssignableFrom(method.getParameters()[0].getType()));
    }

}
