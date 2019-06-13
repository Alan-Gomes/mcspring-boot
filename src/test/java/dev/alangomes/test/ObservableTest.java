package dev.alangomes.test;

import dev.alangomes.springspigot.context.Context;
import dev.alangomes.springspigot.reactive.ObserveEvent;
import dev.alangomes.test.util.SpringSpigotTestInitializer;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(
        classes = {TestApplication.class},
        initializers = SpringSpigotTestInitializer.class
)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class ObservableTest {

    @Autowired
    private Server server;

    @Autowired
    private Plugin plugin;

    @SpyBean
    private Context context;

    @Mock
    private Player player;

    @Captor
    private ArgumentCaptor<EventExecutor> eventExecutorCaptor;

    @ObserveEvent
    private Observable<TestEvent> testEventObservable;

    @Before
    public void setup() {
        when(player.getName()).thenReturn("player");
        when(server.getPlayer("player")).thenReturn(player);
    }

    @Test
    public void shouldRegisterEventsForTheObservable() {
        testEventObservable.subscribe();

        verify(server.getPluginManager()).registerEvent(eq(TestEvent.class), any(), any(), any(), eq(plugin), anyBoolean());
    }

    @Test
    public void shouldEmitEventsToTheObservable() throws EventException {
        TestObserver<TestEvent> testObserver = testEventObservable.test();
        verify(server.getPluginManager()).registerEvent(eq(TestEvent.class), any(), any(), eventExecutorCaptor.capture(), eq(plugin), anyBoolean());
        EventExecutor executor = eventExecutorCaptor.getValue();

        TestEvent event = new TestEvent(player);
        executor.execute(null, event);
        executor.execute(null, event);

        testObserver
                .assertValuesOnly(event, event)
                .assertNotTerminated();
    }

    @AllArgsConstructor
    @Getter
    static class TestEvent extends Event {

        private CommandSender commandSender;

        @Override
        public HandlerList getHandlers() {
            return null;
        }
    }

}