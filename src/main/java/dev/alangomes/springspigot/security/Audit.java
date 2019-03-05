package dev.alangomes.springspigot.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method or all classes in method as auditable.
 *
 * All method calls will be logged with the caller name (if available) and parameter info.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Audit {

    /**
     * Declares whether the audit requires a {@link org.bukkit.command.CommandSender CommandSender} as caller.
     * If true, all calls not originated from a {@link org.bukkit.command.CommandSender CommandSender} will be ignored.
     *
     * Defaults to {@code true}
     */
    boolean senderOnly() default true;

}
