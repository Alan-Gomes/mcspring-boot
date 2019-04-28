package dev.alangomes.springspigot;

import dev.alangomes.springspigot.event.EventService;
import org.bukkit.event.Listener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SpringSpigotAutoConfigurationTest {
    
    @Mock
    private ConfigurableApplicationContext context;

    @Mock
    private EventService eventService;

    @InjectMocks
    private SpringSpigotAutoConfiguration startupHook;

    @Before
    public void setup() {
        Map<String, Listener> beans = new HashMap<>();
        beans.put("b1", mock(Listener.class));
        beans.put("b2", mock(Listener.class));
        when(context.getBeansOfType(Listener.class)).thenReturn(beans);
        when(context.getBean(EventService.class)).thenReturn(eventService);
    }

    @Test
    public void shouldRegisterAllListeners() {
        startupHook.onStartup(null);

        verify(eventService, times(2)).registerEvents(notNull());
    }

}