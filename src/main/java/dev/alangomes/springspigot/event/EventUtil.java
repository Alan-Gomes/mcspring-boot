package dev.alangomes.springspigot.event;

import lombok.SneakyThrows;
import lombok.val;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class EventUtil {

    private EventUtil() {
    }

    public static CommandSender getSender(Event event) {
        if (event instanceof PlayerEvent) {
            return ((PlayerEvent) event).getPlayer();
        } else if (event instanceof ServerCommandEvent) {
            return ((ServerCommandEvent) event).getSender();
        } else if (event instanceof EntityEvent) {
            val entityEvent = (EntityEvent) event;
            return entityEvent.getEntity() instanceof Player ? entityEvent.getEntity() : null;
        }
        return getInferredSender(event);
    }

    private static CommandSender getInferredSender(Event event) {
        return Arrays.stream(ReflectionUtils.getAllDeclaredMethods(event.getClass()))
                .filter(method -> method.getName().startsWith("get"))
                .filter(method -> method.getParameters().length == 0)
                .filter(method -> CommandSender.class.isAssignableFrom(method.getReturnType()))
                .filter(method -> Modifier.isPublic(method.getModifiers()))
                .findFirst()
                .map(method -> (CommandSender) getValue(event, method))
                .orElse(null);
    }

    @SneakyThrows
    private static Object getValue(Object instance, Method method) {
        method.setAccessible(true);
        return method.invoke(instance);
    }

}
