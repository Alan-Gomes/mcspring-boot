package dev.alangomes.mcspring.hook.security;

import dev.alangomes.mcspring.hook.ServerContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.bukkit.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Scope("singleton")
public class PermissionAspect {

    @Autowired
    private ServerContext serverContext;

    @Around("@annotation(dev.alangomes.mcspring.hook.security.Authorize) || @within(dev.alangomes.mcspring.hook.security.Authorize)")
    public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        Player player = serverContext.getPlayer();
        if (player == null) {
            throw new PlayerNotFoundException();
        }

        String permission = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(Authorize.class).value();
        if (!player.hasPermission(permission)) {
            throw new PermissionDeniedException(permission);
        }
        return joinPoint.proceed();
    }

}
