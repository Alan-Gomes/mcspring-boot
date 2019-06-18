package dev.alangomes.springspigot.picocli;

import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
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
        K bean = beanFactory.getBean(cls);
        if (!(bean instanceof Iterable) && AopUtils.isAopProxy(bean)) {
            return (K) ((Advised) bean).getTargetSource().getTarget();
        }
        return bean;
    }

}
