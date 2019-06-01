package dev.alangomes.springspigot.configuration;

import lombok.val;
import org.bukkit.configuration.ConfigurationSection;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.env.Environment;

import java.util.Map;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Configuration
class ResourceConfiguration {

    @Scope(SCOPE_PROTOTYPE)
    @Bean
    public Instance configInstance(InjectionPoint injectionPoint, ConversionService conversionService, Environment environment) {
        val dynamicValue = injectionPoint.getAnnotation(DynamicValue.class);
        val type = ((DependencyDescriptor) injectionPoint).getResolvableType().getGeneric(0);

        return new Instance(environment, dynamicValue.value(), conversionService, type.getRawClass());
    }

    @Bean
    public ConversionService conversionService() {
        DefaultConversionService conversionService = new DefaultConversionService();
        conversionService.addConverter(ConfigurationSection.class, Map.class, (s) -> s.getValues(true));
        return conversionService;
    }

}
