package cn.stylefeng.guns.modular.broadcast.remoteClient.factory;

import cn.stylefeng.guns.modular.broadcast.remoteClient.client.BroadcastClient;
import cn.stylefeng.guns.modular.broadcast.remoteClient.config.BroadcastConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * 广播客户端工厂类
 * 用于动态创建广播客户端实例
 */
@Component
public class BroadcastClientFactory {

    /**
     * 创建具有指定配置的广播客户端
     */
    public BroadcastClient createClient(String host, Integer port, String username, String password) {
        // 创建配置对象
        BroadcastConfig config = BroadcastConfig.create(host, port, username, password);

        // 创建带有指定超时设置的RestTemplate
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(config.getConnectTimeout());
        factory.setReadTimeout(config.getReadTimeout());
        RestTemplate restTemplate = new RestTemplate(factory);

        // 创建客户端实例
        BroadcastClient client = new BroadcastClient();
        client.initialize(config, restTemplate, new ObjectMapper());

        return client;
    }

    /**
     * 创建具有完整配置的广播客户端
     */
    public BroadcastClient createClientWithFullConfig(
            String host, Integer port, String username, String password,
            Integer connectTimeout, Integer readTimeout) {
        BroadcastConfig config = new BroadcastConfig(host, port, username, password);
        config.setConnectTimeout(connectTimeout);
        config.setReadTimeout(readTimeout);

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(config.getConnectTimeout());
        factory.setReadTimeout(config.getReadTimeout());
        RestTemplate restTemplate = new RestTemplate(factory);

        BroadcastClient client = new BroadcastClient();
        client.initialize(config, restTemplate, new ObjectMapper());

        return client;
    }

    /**
     * 使用已有配置创建客户端
     */
    public BroadcastClient createClientWithConfig(BroadcastConfig config) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(config.getConnectTimeout());
        factory.setReadTimeout(config.getReadTimeout());
        RestTemplate restTemplate = new RestTemplate(factory);

        BroadcastClient client = new BroadcastClient();
        client.initialize(config, restTemplate, new ObjectMapper());

        return client;
    }
}