package dev.alangomes.test;

import dev.alangomes.springspigot.context.Context;
import dev.alangomes.springspigot.exception.PermissionDeniedException;
import dev.alangomes.springspigot.exception.PlayerNotFoundException;
import dev.alangomes.springspigot.security.Authorize;
import dev.alangomes.test.util.SpringSpigotTestInitializer;
import org.bukkit.entity.Player;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(
        classes = {TestApplication.class, AuthorizationTest.TestService.class},
        initializers = SpringSpigotTestInitializer.class
)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class AuthorizationTest {

    @Autowired
    private TestService testService;

    @Autowired
    private Context context;

    @Mock
    private Player player;

    @Test(expected = PlayerNotFoundException.class)
    public void shouldThrowExceptionIfNoPlayerInContext() {
        testService.sum(2, 2);
    }

    @Test(expected = PermissionDeniedException.class)
    public void shouldThrowExceptionIfPermissionWasDenied() {
        when(player.hasPermission("server.kill")).thenReturn(false);

        context.runWithSender(player, () -> testService.sum(2, 2));
    }

    @Test
    public void shouldPassCallThroughIfPermissionWasGranted() {
        when(player.hasPermission("server.kill")).thenReturn(true);

        int result = context.runWithSender(player, () -> testService.sum(2, 2));
        assertEquals("The call was not successful", 4, result);
    }

    @Test
    public void shouldEvaluateMethodParameters() {
        when(player.hasPermission(any(String.class))).thenReturn(false);
        when(player.hasPermission("resource.test.create")).thenReturn(true);

        String result = context.runWithSender(player, () -> testService.create("test"));
        assertEquals("The call was not successful", "test", result);
    }

    @Test
    public void shouldPassCallThroughIfMethodIsNotAnnotated() {
        when(player.hasPermission("server.kill")).thenReturn(false);

        int result = context.runWithSender(player, () -> testService.multiply(2, 5));
        assertEquals("The call was not successful", 10, result);
    }

    @Service
    static class TestService {
        @Authorize("hasPermission('server.kill')")
        public int sum(int num1, int num2) {
            return num1 + num2;
        }

        @Authorize("hasPermission('resource.' + #arg0 + '.create')")
        public String create(String resourceName) {
            return resourceName;
        }

        public int multiply(int num1, int num2) {
            return num1 * num2;
        }
    }

}