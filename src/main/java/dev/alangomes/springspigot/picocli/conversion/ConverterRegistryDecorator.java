package dev.alangomes.springspigot.picocli.conversion;

import org.springframework.core.convert.ConversionService;
import picocli.CommandLine;

import java.util.HashMap;
import java.util.Map;

class ConverterRegistryDecorator extends HashMap<Class<?>, CommandLine.ITypeConverter<?>> {

    private final ConversionService conversionService;

    ConverterRegistryDecorator(Map<Class<?>, CommandLine.ITypeConverter<?>> originalRegistry, ConversionService conversionService) {
        super(originalRegistry);
        this.conversionService = conversionService;
    }

    @Override
    public boolean containsKey(Object type) {
        if (conversionService.canConvert(String.class, (Class<?>) type)) {
            return true;
        }
        return super.containsKey(type);
    }

    @Override
    public CommandLine.ITypeConverter<?> get(Object type) {
        if (conversionService.canConvert(String.class, (Class<?>) type)) {
            return new ConverterDecorator<>(conversionService, (Class<?>) type);
        }
        return super.get(type);
    }
}
