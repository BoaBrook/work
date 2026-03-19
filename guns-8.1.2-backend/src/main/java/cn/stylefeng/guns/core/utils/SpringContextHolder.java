package cn.stylefeng.guns.core.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring 上下文工具类，用于在非 Spring 管理的类中获取 Bean
 */
@Component // 必须被 Spring 扫描并管理，才能触发 setApplicationContext 方法
public class SpringContextHolder implements ApplicationContextAware {

    // 静态持有 Spring 上下文
    private static ApplicationContext applicationContext;

    /**
     * Spring 容器初始化时自动调用，注入上下文
     * （该方法由 Spring 容器调用，无需手动调用）
     */
    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
    }

    /**
     * 获取 Spring 上下文
     */
    public static ApplicationContext getApplicationContext() {
        checkApplicationContext();
        return applicationContext;
    }

    /**
     * 根据 Bean 类型获取 Bean（推荐，类型安全）
     */
    public static <T> T getBean(Class<T> beanClass) {
        checkApplicationContext();
        return applicationContext.getBean(beanClass);
    }

    /**
     * 根据 Bean 名称获取 Bean
     */
    public static Object getBean(String beanName) {
        checkApplicationContext();
        return applicationContext.getBean(beanName);
    }

    /**
     * 根据 Bean 名称和类型获取 Bean
     */
    public static <T> T getBean(String beanName, Class<T> beanClass) {
        checkApplicationContext();
        return applicationContext.getBean(beanName, beanClass);
    }

    /**
     * 检查上下文是否初始化完成
     */
    private static void checkApplicationContext() {
        if (applicationContext == null) {
            throw new IllegalStateException("Spring 上下文未初始化，请确认 Spring 容器已启动");
        }
    }
}
