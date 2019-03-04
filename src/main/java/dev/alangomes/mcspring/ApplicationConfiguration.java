package dev.alangomes.mcspring;

import dev.alangomes.mcspring.hook.ScopePostProcessor;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

@SpringBootApplication(scanBasePackages = "dev.alangomes.mcspring")
@Scope("singleton")
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
public class ApplicationConfiguration {

    @Bean
    public static BeanFactoryPostProcessor scopeBeanFactoryPostProcessor() {
        return new ScopePostProcessor();
    }


}
