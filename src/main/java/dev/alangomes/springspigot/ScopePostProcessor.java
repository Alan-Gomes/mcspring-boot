package dev.alangomes.springspigot;

import org.bukkit.event.Listener;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.util.Arrays;

class ScopePostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory factory) throws BeansException {
        Arrays.stream(factory.getBeanDefinitionNames()).forEach(beanName -> {
            BeanDefinition beanDef = factory.getBeanDefinition(beanName);
            Class<?> beanType = factory.getType(beanName);
            if (beanType != null && beanType.isAssignableFrom(Listener.class)) {
                beanDef.setScope("singleton");
            }
        });
    }

}