package dev.alangomes.springspigot.security;

import dev.alangomes.springspigot.context.Context;
import dev.alangomes.springspigot.context.SessionService;
import dev.alangomes.springspigot.exception.PermissionDeniedException;
import dev.alangomes.springspigot.exception.PlayerNotFoundException;
import dev.alangomes.springspigot.util.AopAnnotationUtil;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static jdk.nashorn.internal.runtime.JSType.toBoolean;
import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

@Slf4j
@Aspect
@Component
@Scope(SCOPE_SINGLETON)
class SecurityAspect {

    @Autowired
    private Context context;

    @Autowired
    private SessionService sessionService;

    private final Map<String, Expression> expressionCache = new ConcurrentHashMap<>();

    private final ExpressionParser parser = new SpelExpressionParser();

    @Order(0)
    @Around("within(@(@dev.alangomes.springspigot.security.Authorize *) *) " +
            "|| execution(@(@dev.alangomes.springspigot.security.Authorize *) * *(..)) " +
            "|| @within(dev.alangomes.springspigot.security.Authorize)" +
            "|| execution(@dev.alangomes.springspigot.security.Authorize * *(..))")
    public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        val sender = context.getSender();
        if (sender == null) {
            throw new PlayerNotFoundException();
        }
        val method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        val senderContext = new StandardEvaluationContext(sender);
        val parameters = method.getParameters();
        IntStream.range(0, parameters.length)
                .forEach(i -> senderContext.setVariable(parameters[i].getName(), joinPoint.getArgs()[i]));
        senderContext.setVariable("session", sessionService.current());

        AopAnnotationUtil.getAppliableAnnotations(method, Authorize.class).forEach(authorize -> {
            val expressionSource = authorize.value();
            val expression = expressionCache.computeIfAbsent(expressionSource, parser::parseExpression);
            if (!toBoolean(expression.getValue(senderContext, Boolean.class))) {
                throw new PermissionDeniedException(expressionSource);
            }
        });
        return joinPoint.proceed();
    }

    @Order(1)
    @Before("within(@(@dev.alangomes.springspigot.security.Audit *) *) " +
            "|| execution(@(@dev.alangomes.springspigot.security.Audit *) * *(..)) " +
            "|| @within(dev.alangomes.springspigot.security.Audit)" +
            "|| execution(@dev.alangomes.springspigot.security.Audit * *(..))")
    public void auditCall(JoinPoint joinPoint) {
        val sender = context.getSender();
        val method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        val signature = ClassUtils.getUserClass(method.getDeclaringClass()).getName() + "." + method.getName();
        val arguments = Arrays.stream(joinPoint.getArgs()).map(String::valueOf).collect(Collectors.joining(", "));

        AopAnnotationUtil.getAppliableAnnotations(method, Audit.class).forEach(audit -> {
            if (sender != null || !audit.senderOnly()) {
                if (sender != null) {
                    log.info(String.format("Player %s invoked %s(%s)", sender.getName(), signature, arguments));
                } else {
                    log.info(String.format("Server invoked %s(%s)", signature, arguments));
                }
            }
        });
    }

}
