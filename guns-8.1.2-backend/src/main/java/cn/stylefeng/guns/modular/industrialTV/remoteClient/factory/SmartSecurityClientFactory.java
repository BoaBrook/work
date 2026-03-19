package cn.stylefeng.guns.modular.industrialTV.remoteClient.factory;

import cn.stylefeng.guns.modular.industrialTV.remoteClient.client.SmartSecurityClient;
import cn.stylefeng.guns.modular.industrialTV.remoteClient.config.SmartSecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * 视频智能分析系统客户端工厂类
 * 用于动态创建视频智能分析系统客户端实例
 */
@Component
public class SmartSecurityClientFactory {

    /**
     * 创建具有指定配置的客户端
     */
    public SmartSecurityClient createClient(String host, Integer port, String username, String password) {
        // 创建配置对象
        SmartSecurityConfig config = SmartSecurityConfig.create(host, port, username, password);

        // 创建带有指定超时设置的RestTemplate
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(config.getConnectTimeout());
        factory.setReadTimeout(config.getReadTimeout());
        RestTemplate restTemplate = new RestTemplate(factory);

        // 创建客户端实例
        SmartSecurityClient client = new SmartSecurityClient();
        client.initialize(config, restTemplate, new ObjectMapper());

        return client;
    }

    /**
     * 创建具有完整配置的客户端
     */
    public SmartSecurityClient createClientWithFullConfig(
            String host, Integer port, String username, String password,
            Integer connectTimeout, Integer readTimeout) {
        SmartSecurityConfig config = new SmartSecurityConfig(host, port, username, password);
        config.setConnectTimeout(connectTimeout);
        config.setReadTimeout(readTimeout);

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(config.getConnectTimeout());
        factory.setReadTimeout(config.getReadTimeout());
        RestTemplate restTemplate = new RestTemplate(factory);

        SmartSecurityClient client = new SmartSecurityClient();
        client.initialize(config, restTemplate, new ObjectMapper());

        return client;
    }

    /**
     * 使用已有配置创建客户端
     */
    public SmartSecurityClient createClientWithConfig(SmartSecurityConfig config) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(config.getConnectTimeout());
        factory.setReadTimeout(config.getReadTimeout());
        RestTemplate restTemplate = new RestTemplate(factory);

        SmartSecurityClient client = new SmartSecurityClient();
        client.initialize(config, restTemplate, new ObjectMapper());

        return client;
    }
}