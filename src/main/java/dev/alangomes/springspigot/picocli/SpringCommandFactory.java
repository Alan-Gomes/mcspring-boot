package dev.alangomes.springspigot.picocli;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

@Component
class SpringCommandFactory implements CommandLine.IFactory {

    @Autowired
    private BeanFactory beanFactory;

    @Override
    public <K> K create(Class<K> cls) throws Exception {
        return beanFactory.getBean(cls);
    }

}
