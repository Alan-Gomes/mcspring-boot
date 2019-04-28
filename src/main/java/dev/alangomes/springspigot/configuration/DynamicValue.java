package dev.alangomes.springspigot.configuration;

import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Has the same effect of {@link org.springframework.beans.factory.annotation.Value Value} annotation, but allows re-evaluation.
 * Should be used together with {@link dev.alangomes.springspigot.configuration.Instance Instance}.
 */
@Autowired
@Retention(RetentionPolicy.RUNTIME)
public @interface DynamicValue {

    /**
     * The actual value expression: e.g. "#{systemProperties.myProp}".
     */
    String value();

}
