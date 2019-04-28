package dev.alangomes.springspigot.configuration;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.Environment;

/**
 * Class that represents a dynamic property
 */
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Instance<T> {

    Environment environment;

    String expression;

    ConversionService conversionService;

    Class<T> type;

    /**
     * Evaluate and retrieve the value of the property
     *
     * @return The value of the property
     */
    public T get() {
        return conversionService.convert(environment.resolvePlaceholders(expression), type);
    }
}
