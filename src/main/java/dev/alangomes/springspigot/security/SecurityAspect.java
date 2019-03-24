package dev.alangomes.springspigot.security;

import dev.alangomes.springspigot.context.Context;
import dev.alangomes.springspigot.exception.PermissionDeniedException;
import dev.alangomes.springspigot.exception.PlayerNotFoundException;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static jdk.nashorn.internal.runtime.JSType.toBoolean;

@Slf4j
@Aspect
@Component
@Scope("singleton")
class SecurityAspect implements Listener {

    @Autowired
    private Context context;

    private final Map<String, EvaluationContext> contextCache = new ConcurrentHashMap<>();

    private final Map<String, Expression> expressionCache = new ConcurrentHashMap<>();

    private final ExpressionParser parser = new SpelExpressionParser();

    @Order(0)
    @Around("@annotation(dev.alangomes.springspigot.security.Authorize) || @within(dev.alangomes.springspigot.security.Authorize)")
    public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        val sender = context.getSender();
        if (sender == null) {
            throw new PlayerNotFoundException();
        }
        val expressionSource = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(Authorize.class).value();

        val senderContext = contextCache.computeIfAbsent(sender.getName(), n -> new StandardEvaluationContext(sender));
        val expression = expressionCache.computeIfAbsent(expressionSource, parser::parseExpression);
        if (!toBoolean(expression.getValue(senderContext, Boolean.class))) {
            throw new PermissionDeniedException(expressionSource);
        }
        return joinPoint.proceed();
    }

    @Order(1)
    @Before("@annotation(dev.alangomes.springspigot.security.Audit) || @within(dev.alangomes.springspigot.security.Audit)")
    public void auditCall(JoinPoint joinPoint) {
        val sender = context.getSender();
        val method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        val audit = method.getAnnotation(Audit.class);
        if (sender != null || !audit.senderOnly()) {
            val signature = method.getDeclaringClass().getName() + "." + method.getName();
            val arguments = Arrays.stream(joinPoint.getArgs()).map(String::valueOf).collect(Collectors.joining(", "));
            if (sender != null) {
                log.info(String.format("Player %s invoked %s(%s)", sender.getName(), signature, arguments));
            } else {
                log.info(String.format("Server invoked %s(%s)", signature, arguments));
            }
        }
    }

    @EventHandler
    void onQuit(PlayerQuitEvent event) {
        contextCache.remove(event.getPlayer().getName());
    }

}
