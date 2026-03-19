package com.example.casclient1.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Arrays;
import java.util.List;

/**
 * redis配置
 * 用于配置Redis连接工厂和Redis模板
 * @author Hchenbin.
 */
@Configuration
public class RedisConfig {

    private final Environment environment;

    /**
     * 构造函数，注入环境变量对象
     * 用于获取配置信息
     *
     * @param environment 环境变量对象
     */
    public RedisConfig(Environment environment) {
        this.environment = environment;
    }

    /**
     * 根据配置创建Redis连接工厂
     * 支持单机和集群模式
     *
     * @return RedisConnectionFactory 连接工厂实例
     */
    @Bean(name = "myRedisConnectionFactory")
    public RedisConnectionFactory myLettuceConnectionFactory() {
        String mode = environment.getProperty("spring.redis.mode", "single");
        if ("cluster".equals(mode)) {
            return createClusterConnectionFactory();
        } else {
            return createSingleConnectionFactory();
        }
    }

    /**
     * 创建单机模式的Redis连接工厂
     *
     * @return LettuceConnectionFactory 单机模式连接工厂实例
     */
    private RedisConnectionFactory createSingleConnectionFactory() {
        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory();
        Integer port = Integer.valueOf(environment.getProperty("spring.redis.port"));
        String hostName = environment.getProperty("spring.redis.host");
        String password = environment.getProperty("spring.redis.password");

        // 解密密码
//        if (password != null && password.startsWith("DES@")) {
//            String key = ObjectKit.md5("REDIS", true, 16);
//            String pwdSub = password.substring("DES@".length());
//            password = EncryptionKit.decrypt(pwdSub, key, 0);
//        }

        connectionFactory.setPort(port);
        connectionFactory.setHostName(hostName);
        connectionFactory.setPassword(password);
        return connectionFactory;
    }

    /**
     * 创建集群模式的Redis连接工厂
     *
     * @return LettuceConnectionFactory 集群模式连接工厂实例
     */
    private RedisConnectionFactory createClusterConnectionFactory() {
        String clusterNodes = environment.getProperty("spring.redis.cluster.nodes");
        List<String> nodes = Arrays.asList(clusterNodes.split(","));
        RedisClusterConfiguration clusterConfig = new RedisClusterConfiguration();

        nodes.forEach(node -> {
            String[] parts = node.split(":");
            clusterConfig.addClusterNode(new RedisNode(parts[0], Integer.parseInt(parts[1])));
        });

        String password = environment.getProperty("spring.redis.password");
//        if (password != null && password.startsWith("DES@")) {
//            String key = ObjectKit.md5("REDIS", true, 16);
//            String pwdSub = password.substring("DES@".length());
//            password = EncryptionKit.decrypt(pwdSub, key, 0);
//        }
        clusterConfig.setPassword(RedisPassword.of(password));

        return new LettuceConnectionFactory(clusterConfig);
    }

    @Bean
    @SuppressWarnings("all")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(factory);
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // key采用String的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        // hash的key也采用String的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        // value序列化方式采用jackson
        template.setValueSerializer(jackson2JsonRedisSerializer);
        // hash的value序列化方式采用jackson
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }

    /**
     * 创建字符串类型的Redis模板
     * 用于操作字符串类型的Redis数据
     *
     * @param factory Redis连接工厂
     * @return StringRedisTemplate 字符串类型Redis模板实例
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(@Qualifier("myRedisConnectionFactory") RedisConnectionFactory factory) {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate(factory);
        stringRedisTemplate.setKeySerializer(new StringRedisSerializer());
        stringRedisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return stringRedisTemplate;
    }


    //token缓存策略：redis缓存
//    @Autowired
//    private RedisConnectionFactory redisConnectionFactory;
//    @Bean
//    public TokenStore tokenStore(){
//        return new RedisTokenStore(redisConnectionFactory);
//    }
}
