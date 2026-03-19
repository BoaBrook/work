package com.example.casclient1.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringBeanFactoryUtils implements ApplicationContextAware {

    private static ApplicationContext context = null;

    public static <T> T getBean(Class<T> tClass) {
        return context.getBean(tClass);
    }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (SpringBeanFactoryUtils.context == null) {
            SpringBeanFactoryUtils.context = applicationContext;
        }
    }
}
