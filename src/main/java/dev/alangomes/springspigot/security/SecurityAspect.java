package dev.alangomes.springspigot.security;

import dev.alangomes.springspigot.context.ServerContext;
import dev.alangomes.springspigot.exception.PermissionDeniedException;
import dev.alangomes.springspigot.exception.PlayerNotFoundException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static jdk.nashorn.internal.runtime.JSType.toBoolean;

@Aspect
@Component
@Scope("singleton")
class SecurityAspect implements Listener {

    @Autowired
    private ServerContext serverContext;

    private Logger logger = LoggerFactory.getLogger(SecurityAspect.class);

    private final Map<String, EvaluationContext> contextCache = new ConcurrentHashMap<>();

    private final Map<String, Expression> expressionCache = new ConcurrentHashMap<>();

    private final ExpressionParser parser = new SpelExpressionParser();

    @Order(0)
    @Around("@annotation(dev.alangomes.springspigot.security.Authorize) || @within(dev.alangomes.springspigot.security.Authorize)")
    public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        CommandSender sender = serverContext.getSender();
        if (sender == null) {
            throw new PlayerNotFoundException();
        }
        String expressionSource = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(Authorize.class).value();

        EvaluationContext senderContext = contextCache.computeIfAbsent(sender.getName(), n -> new StandardEvaluationContext(sender));
        Expression expression = expressionCache.computeIfAbsent(expressionSource, parser::parseExpression);
        if (!toBoolean(expression.getValue(senderContext, Boolean.class))) {
            throw new PermissionDeniedException(expressionSource);
        }
        return joinPoint.proceed();
    }

    @Order(1)
    @Before("@annotation(dev.alangomes.springspigot.security.Audict) || @within(dev.alangomes.springspigot.security.Audict)")
    public void audictCall(JoinPoint joinPoint) {
        CommandSender sender = serverContext.getSender();
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Audict audict = method.getAnnotation(Audict.class);
        if (sender != null || !audict.playerOnly()) {
            String signature = method.getDeclaringClass().getName() + "." + method.getName();
            String arguments = Arrays.stream(joinPoint.getArgs()).map(String::valueOf).collect(Collectors.joining(", "));
            if (sender != null) {
                logger.info(String.format("Player %s invoked %s(%s)", sender.getName(), signature, arguments));
            } else {
                logger.info(String.format("Server invoked %s(%s)", signature, arguments));
            }
        }
    }

    @EventHandler
    void onQuit(PlayerQuitEvent event) {
        contextCache.remove(event.getPlayer().getName());
    }

}
