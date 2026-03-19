package cn.stylefeng.guns.modular.industrialTV.remoteClient.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * 视频智能分析系统客户端配置
 */
@Configuration
public class SmartSecurityClientConfig {

    @Bean
    public RestTemplate smartSecurityRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        // 使用默认超时值
        factory.setConnectTimeout(5000);  // 5秒连接超时
        factory.setReadTimeout(30000);    // 30秒读取超时
        return new RestTemplate(factory);
    }
}