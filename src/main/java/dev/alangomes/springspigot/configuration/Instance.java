package dev.alangomes.springspigot.configuration;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.Environment;

public class Instance<T> {

    private final Environment environment;

    private final String expression;

    private final ConversionService conversionService;

    private final Class<T> type;

    Instance(Environment environment, String expression, ConversionService conversionService, Class<T> type) {
        this.environment = environment;
        this.expression = expression;
        this.conversionService = conversionService;
        this.type = type;
    }

    public T get() {
        return conversionService.convert(environment.resolvePlaceholders(expression), type);
    }
}
