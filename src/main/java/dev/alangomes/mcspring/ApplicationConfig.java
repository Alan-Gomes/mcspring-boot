package dev.alangomes.mcspring;

import dev.alangomes.mcspring.hook.ScopePostProcessor;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = "dev.alangomes.mcspring")
public class ApplicationConfig {

    @Bean
    public static BeanFactoryPostProcessor scopeBeanFactoryPostProcessor() {
        return new ScopePostProcessor();
    }

}
