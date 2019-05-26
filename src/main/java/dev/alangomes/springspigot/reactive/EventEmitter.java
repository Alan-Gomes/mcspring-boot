package dev.alangomes.springspigot.reactive;

import dev.alangomes.springspigot.context.Context;
import dev.alangomes.springspigot.event.EventUtil;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class EventEmitter<T extends Event> implements ObservableOnSubscribe<T> {

    @Getter
    Listener listener = new Listener() {
    };

    Class<? extends Event> eventClazz;

    ObserveEvent observeEvent;

    Plugin plugin;

    Context context;

    @Override
    public void subscribe(ObservableEmitter<T> observableEmitter) {
        val pluginManager = plugin.getServer().getPluginManager();
        pluginManager.registerEvent(eventClazz, listener, observeEvent.priority(), (l, event) -> {
            if (eventClazz.isAssignableFrom(event.getClass())) {
                T emittedEvent = (T) event;
                context.runWithSender(EventUtil.getSender(emittedEvent), () -> observableEmitter.onNext(emittedEvent));
            }
        }, plugin, observeEvent.ignoreCancelled());
    }
}
