package com.example.casclient1.config;

import com.example.casclient1.utils.SpringBeanFactoryUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Slf4j
public class CasAuthInterceptor implements HandlerInterceptor {

    private static final String casClientHostUrl = "http://localhost:8081/client1/login/cas";
    private static final String casServerLoginUrl = "http://171.7.4.234:8080/cas/login";
    private static final String casValidHostUrl = "http://171.7.4.234:8080/cas/p3/serviceValidate";

    private StringRedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 检查是否有有效的CAS票据
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // 检查是否认证（排除匿名用户）
        boolean isAuthenticated = auth != null && auth.isAuthenticated() &&
                !"anonymousUser".equals(auth.getName());

        if (!isAuthenticated) {
            // 如果没有认证，重定向到CAS登录页面
            String redirectUrl = casServerLoginUrl + "?service=" + casClientHostUrl;

            response.sendRedirect(redirectUrl);
            return false;
        } else {
            // TODO 获取CAS认证账号、部门、角色等信息;
            String username = auth.getName();
            if (!StringUtils.isEmpty(username)) {
                redisTemplate = SpringBeanFactoryUtils.getBean(StringRedisTemplate.class);
                Object userInfo = redisTemplate.opsForValue().get(username);
                log.info("CAS 用户/角色/部门信息：{}", userInfo);
            }
            return true;
        }
    }

}