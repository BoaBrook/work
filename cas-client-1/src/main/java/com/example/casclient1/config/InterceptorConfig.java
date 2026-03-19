package com.example.casclient1.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Order(1)
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new CasAuthInterceptor())
                .addPathPatterns("/**") // 拦截所有路径
                .excludePathPatterns("/login/**", "/public/**"); // 排除不需要拦截的路径
    }
}