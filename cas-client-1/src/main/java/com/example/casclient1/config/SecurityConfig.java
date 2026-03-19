package com.example.casclient1.config;

import org.jasig.cas.client.validation.Cas30ServiceTicketValidator;
import org.jasig.cas.client.validation.TicketValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

/**
 * Spring Security配置类
 * 用于配置CAS客户端的安全认证
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * CAS服务器地址前缀
     */
    @Value("${cas.server-url-prefix}")
    private String casServerUrlPrefix;

    /**
     * CAS服务器登录URL
     */
    @Value("${cas.server-login-url}")
    private String casServerLoginUrl;

    /**
     * CAS服务器登出URL
     */
    @Value("${cas.server-logout-url}")
    private String casServerLoginOutUrl;

    /**
     * CAS客户端主机地址
     */
    @Value("${cas.client-host-url}")
    private String casClientHostUrl;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/public/**", "/login/**").permitAll()  // 公开资源
                .anyRequest().authenticated()
//                .and()
//                .logout()
//                .logoutUrl(casServerLoginOutUrl)
//                .logoutSuccessUrl("/login?logout")
//                .deleteCookies("JSESSIONID")
//                .invalidateHttpSession(true)
                .and()
                .csrf().disable()
                .addFilter(casAuthenticationFilter())
                .exceptionHandling()
                .authenticationEntryPoint(casAuthenticationEntryPoint())
                .and()
                .sessionManagement()
                .sessionFixation().none();
    }

    @Bean
    public CasAuthenticationFilter casAuthenticationFilter() throws Exception {
        CasAuthenticationFilter filter = new CasAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManager());
        return filter;
    }

    /**
     * 配置CAS认证入口点
     */
    @Bean
    public CasAuthenticationEntryPoint casAuthenticationEntryPoint() {
        CasAuthenticationEntryPoint entryPoint = new CasAuthenticationEntryPoint();
        entryPoint.setLoginUrl(casServerLoginUrl);
        entryPoint.setServiceProperties(serviceProperties());
        return entryPoint;
    }


    /**
     * 配置CAS服务属性
     */
    @Bean
    public ServiceProperties serviceProperties() {
        ServiceProperties serviceProperties = new ServiceProperties();
        // 这里的"/login/cas" 是CAS服务器用来回调你的应用的URL，用于处理CAS服务器返回的服务票据。虽然你的应用中没有直接定义这个端点，但Spring Security CAS过滤器会自动处理这个路径。
        serviceProperties.setService(casClientHostUrl + "/login/cas");
        serviceProperties.setSendRenew(false);
        return serviceProperties;
    }

    /**
     * 配置CAS认证提供者
     */
    @Bean
    public AuthenticationProvider casAuthenticationProvider() {
        CasAuthenticationProvider provider = new CasAuthenticationProvider();
        provider.setServiceProperties(serviceProperties());
        provider.setTicketValidator(ticketValidator());
        provider.setKey("CAS_PROVIDER_SHARED_KEY");// 认证提供者密钥，共享登录的服务中密钥必须一致
        provider.setAuthenticationUserDetailsService(
                ticket -> new User(
                        ticket.getName(),
                        "NO_PASSWORD",
                        true,
                        true,
                        true,
                        true,
                        AuthorityUtils.createAuthorityList("ROLE_USER")
                )
        );
        return provider;
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(casAuthenticationProvider());
    }


    /**
     * 配置CAS票据验证器
     */
    @Bean
    public TicketValidator ticketValidator() {
        return new CustomCas30ServiceTicketValidator(casServerUrlPrefix);
    }


} 