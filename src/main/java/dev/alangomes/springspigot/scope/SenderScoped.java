package dev.alangomes.springspigot.scope;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Indexed;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Indexed
@Scope(value = "sender", proxyMode = ScopedProxyMode.TARGET_CLASS)
public @interface SenderScoped {
}
