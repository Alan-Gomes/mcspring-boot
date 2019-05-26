package dev.alangomes.springspigot.picocli.conversion;

import org.springframework.core.convert.ConversionService;
import picocli.CommandLine;

class ConverterDecorator<T> implements CommandLine.ITypeConverter<T> {

    private final ConversionService conversionService;

    private final Class<T> type;

    public ConverterDecorator(ConversionService conversionService, Class<T> type) {
        this.conversionService = conversionService;
        this.type = type;
    }

    @Override
    public T convert(String value) {
        return conversionService.convert(value, type);
    }
}
