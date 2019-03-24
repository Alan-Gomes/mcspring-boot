package dev.alangomes.springspigot.configuration;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.Environment;

@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Instance<T> {

    Environment environment;

    String expression;

    ConversionService conversionService;

    Class<T> type;

    public T get() {
        return conversionService.convert(environment.resolvePlaceholders(expression), type);
    }
}
