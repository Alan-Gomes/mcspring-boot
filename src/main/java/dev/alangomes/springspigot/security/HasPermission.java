package dev.alangomes.springspigot.security;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to only allow calls from sender with the defined permission(s)
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Authorize("#params.?[!#root.hasPermission(#this)].length == 0")
public @interface HasPermission {

    /**
     * The permission array to be checked over the current sender in the {@link dev.alangomes.springspigot.context.Context}.
     * All permissions must be satisfied to the call be allowed.
     */
    @AliasFor(annotation = Authorize.class, attribute = "params")
    String[] value();

    /**
     * The message to be thrown in {@link dev.alangomes.springspigot.exception.PermissionDeniedException PermissionDeniedException}
     * if the sender is not a player.
     */
    @AliasFor(annotation = Authorize.class, attribute = "message")
    String message() default "";

}
