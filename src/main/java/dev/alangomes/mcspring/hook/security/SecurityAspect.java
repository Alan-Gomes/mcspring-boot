package dev.alangomes.mcspring.hook.security;

import dev.alangomes.mcspring.hook.ServerContext;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.bukkit.entity.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
@Component
@Scope("singleton")
class SecurityAspect {

    @Autowired
    private ServerContext serverContext;

    private Logger logger = LoggerFactory.getLogger(SecurityAspect.class);

    @Order(0)
    @Around("@annotation(dev.alangomes.mcspring.hook.security.Authorize) || @within(dev.alangomes.mcspring.hook.security.Authorize)")
    public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        Player player = serverContext.getSender();
        if (player == null) {
            throw new PlayerNotFoundException();
        }

        String permission = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(Authorize.class).value();
        if (!player.hasPermission(permission)) {
            throw new PermissionDeniedException(permission);
        }
        return joinPoint.proceed();
    }

    @Order(1)
    @Before("@annotation(dev.alangomes.mcspring.hook.security.Audict) || @within(dev.alangomes.mcspring.hook.security.Audict)")
    public void audictCall(JoinPoint joinPoint) {
        Player player = serverContext.getSender();
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Audict audict = method.getAnnotation(Audict.class);
        if (player != null || !audict.playerOnly()) {
            String signature = method.getDeclaringClass().getName() + "." + method.getName();
            String arguments = Arrays.stream(joinPoint.getArgs()).map(String::valueOf).collect(Collectors.joining(", "));
            if (player != null) {
                logger.info(String.format("Player %s invoked %s(%s)", player.getName(), signature, arguments));
            } else {
                logger.info(String.format("Server invoked %s(%s)", signature, arguments));
            }
        }
    }

}
