package dev.alangomes.springspigot.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Determine a business logic to filter the method calls (or all methods inside class) based in the {@link org.bukkit.command.CommandSender CommandSender}
 * of the current context. This automatically requires the presence of a {@link org.bukkit.command.CommandSender CommandSender}.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Authorize {


    /**
     * The expression to be ran before the method is called.
     * If the expression evaluates to {@code false}, the call will automatically throw a {@link dev.alangomes.springspigot.exception.PermissionDeniedException PermissionDeniedException}.
     * If there's no sender in the current context, the method will throw a {@link dev.alangomes.springspigot.exception.PlayerNotFoundException PlayerNotFoundException}.
     *
     * E.g.: @Authorize("hasPermission('test.permission') or isOp()")
     *
     * @see <a href="https://docs.spring.io/spring/docs/3.0.x/reference/expressions.html#expressions-language-ref">Language Reference</a>
     */
    String value();

    /**
     * The message to be thrown in {@link dev.alangomes.springspigot.exception.PermissionDeniedException PermissionDeniedException}
     * if the expression evaluates to {@code false}
     */
    String message() default "";

    /**
     * Additional parameters that will be set on the expression as {@code #params}.
     * @see dev.alangomes.springspigot.security.HasPermission
     */
    String[] params() default {};

}
