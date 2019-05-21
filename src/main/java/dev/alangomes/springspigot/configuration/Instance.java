package dev.alangomes.springspigot.configuration;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.Environment;

import java.util.function.Supplier;

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
        try {
            return conversionService.convert(environment.resolveRequiredPlaceholders(expression), type);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    /*
     * Evaluate and retrieve the value of the property if not {@code null}, otherwise return {@param defaultValue}
     *
     * @param defaultValue the default value to be returned if the property is {@code null}
     * @return The value of the property if not {@code null}, {@param defaultValue} otherwise
     */
    public T orElse(T defaultValue) {
        T value = get();
        return value != null ? value : defaultValue;
    }

    /*
     * Evaluate and retrieve the value of the property if not {@code null}, otherwise return the value supplied from {@param valueSupplier}
     *
     * @param valueSupplier a supplier for the default value to be returned if the property is {@code null}
     * @return The value of the property if not {@code null}, otherwise the value supplied from {@param valueSupplier}
     */
    public T orElseGet(Supplier<? extends T> valueSupplier) {
        T value = get();
        return value != null ? value : valueSupplier.get();
    }
}
