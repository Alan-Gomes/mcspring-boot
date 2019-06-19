package dev.alangomes.springspigot.picocli.conversion;

import lombok.SneakyThrows;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import picocli.CommandLine;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

@Service
public class PicocliConversorInjector {

    @Autowired
    private ConversionService conversionService;

    @SneakyThrows
    public void injectConversor(CommandLine commandLine) {
        val interpreterField = CommandLine.class.getDeclaredField("interpreter");
        interpreterField.setAccessible(true);
        val interpreter = interpreterField.get(commandLine);
        val interpreterClass = interpreterField.getType();

        val registryField = interpreterClass.getDeclaredField("converterRegistry");
        registryField.setAccessible(true);
        removeFinal(registryField);

        val originalRegistry = registryField.get(interpreter);
        if (!(originalRegistry instanceof ConverterRegistryDecorator)) {
            registryField.set(interpreter, new ConverterRegistryDecorator((Map<Class<?>,
                    CommandLine.ITypeConverter<?>>) originalRegistry, conversionService));
        }
        commandLine.getSubcommands().values().forEach(this::injectConversor);
    }

    @SneakyThrows
    private void removeFinal(Field field) {
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
    }

}
