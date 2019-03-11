package dev.alangomes.springspigot.configuration;

import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.Environment;

@Configuration
class ResourceConfiguration {

    @Bean
    public Instance configInstance(InjectionPoint injectionPoint, ConversionService conversionService, Environment environment) {
        DynamicValue dynamicValue = injectionPoint.getAnnotation(DynamicValue.class);
        ResolvableType type = ((DependencyDescriptor) injectionPoint).getResolvableType().getGeneric(0);

        return new Instance(environment, dynamicValue.value(), conversionService, type.getRawClass());
    }

}
