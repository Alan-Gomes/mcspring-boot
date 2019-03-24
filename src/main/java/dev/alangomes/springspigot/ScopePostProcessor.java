package dev.alangomes.springspigot;

import lombok.val;
import org.bukkit.event.Listener;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.util.Arrays;

class ScopePostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory factory) throws BeansException {
        Arrays.stream(factory.getBeanDefinitionNames()).forEach(beanName -> {
            val beanDef = factory.getBeanDefinition(beanName);
            val beanType = factory.getType(beanName);
            if (beanType != null && beanType.isAssignableFrom(Listener.class)) {
                beanDef.setScope("singleton");
            }
        });
    }

}