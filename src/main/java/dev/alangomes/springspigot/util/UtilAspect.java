package dev.alangomes.springspigot.util;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Scope("singleton")
class UtilAspect {

    private Logger logger = LoggerFactory.getLogger(UtilAspect.class);

    @Autowired
    private BukkitScheduler scheduler;

    @Autowired
    private Plugin plugin;

    @Autowired
    private Server server;

    @Order(0)
    @Around("@annotation(dev.alangomes.springspigot.util.Synchronize) || @within(dev.alangomes.springspigot.util.Synchronize)")
    public Object synchronizeCall(ProceedingJoinPoint joinPoint) throws Throwable {
        if (server.isPrimaryThread()) {
            return joinPoint.proceed();
        }
        scheduler.scheduleSyncDelayedTask(plugin, () -> {
            try {
                joinPoint.proceed();
            } catch (Throwable throwable) {
                logger.error("Error in synchronous task", throwable);
            }
        }, 0);
        return null;
    }

}
