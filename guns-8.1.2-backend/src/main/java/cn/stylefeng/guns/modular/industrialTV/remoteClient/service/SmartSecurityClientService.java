package cn.stylefeng.guns.modular.industrialTV.remoteClient.service;

import cn.stylefeng.guns.modular.industrialTV.remoteClient.client.SmartSecurityClient;
import cn.stylefeng.guns.modular.industrialTV.remoteClient.config.SmartSecurityConfig;
import cn.stylefeng.guns.modular.industrialTV.remoteClient.factory.SmartSecurityClientFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 视频智能分析系统客户端服务类
 * 管理多个视频智能分析系统客户端实例
 */
@Service
public class SmartSecurityClientService {

    @Autowired
    private SmartSecurityClientFactory clientFactory;

    // 缓存不同配置的客户端实例
    private final ConcurrentHashMap<String, SmartSecurityClient> clientCache = new ConcurrentHashMap<>();

    /**
     * 获取指定配置的客户端实例
     */
    public SmartSecurityClient getClient(String host, Integer port, String username, String password) {
        String key = buildKey(host, port, username);
        return clientCache.computeIfAbsent(key, k ->
            clientFactory.createClient(host, port, username, password));
    }

    /**
     * 获取指定配置的客户端实例（带完整配置）
     */
    public SmartSecurityClient getClientWithFullConfig(
            String host, Integer port, String username, String password,
            Integer connectTimeout, Integer readTimeout) {
        String key = buildKey(host, port, username);
        return clientCache.computeIfAbsent(key, k ->
            clientFactory.createClientWithFullConfig(host, port, username, password, connectTimeout, readTimeout));
    }

    /**
     * 获取指定配置的客户端实例（使用配置对象）
     */
    public SmartSecurityClient getClientWithConfig(SmartSecurityConfig config) {
        String key = buildKey(config.getHost(), config.getPort(), config.getUsername());
        return clientCache.computeIfAbsent(key, k ->
            clientFactory.createClientWithConfig(config));
    }

    /**
     * 移除指定的客户端实例
     */
    public void removeClient(String host, Integer port, String username) {
        String key = buildKey(host, port, username);
        clientCache.remove(key);
    }

    /**
     * 清空所有客户端实例
     */
    public void clearAllClients() {
        clientCache.clear();
    }

    /**
     * 检查是否存在指定配置的客户端实例
     */
    public boolean containsClient(String host, Integer port, String username) {
        String key = buildKey(host, port, username);
        return clientCache.containsKey(key);
    }

    /**
     * 构建缓存键
     */
    private String buildKey(String host, Integer port, String username) {
        return host + ":" + port + ":" + username;
    }

    /**
     * 获取缓存中客户端的数量
     */
    public int getClientCount() {
        return clientCache.size();
    }
}