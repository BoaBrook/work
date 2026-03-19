package cn.stylefeng.guns.modular.broadcast.remoteClient.config;

import lombok.Data;

/**
 * IP广播系统配置
 */
@Data
public class BroadcastConfig {

    /**
     * 广播服务器地址
     */
    private String host = "localhost";

    /**
     * 广播服务器端口
     */
    private Integer port = 8001;

    /**
     * 用户名
     */
    private String username = "admin";

    /**
     * 密码
     */
    private String password = "123456";

    /**
     * 连接超时时间（毫秒）
     */
    private Integer connectTimeout = 5000;

    /**
     * 读取超时时间（毫秒）
     */
    private Integer readTimeout = 30000;

    /**
     * 获取完整的API基础URL
     */
    public String getBaseUrl() {
        return "http://" + host + ":" + port + "/api/v29+";
    }

    /**
     * 默认构造函数
     */
    public BroadcastConfig() {}

    /**
     * 构造函数，允许动态设置配置
     */
    public BroadcastConfig(String host, Integer port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    /**
     * 构造函数，允许设置全部配置
     */
    public BroadcastConfig(String host, Integer port, String username, String password,
                          Integer connectTimeout, Integer readTimeout) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
    }

    /**
     * 静态工厂方法，创建配置实例
     */
    public static BroadcastConfig create(String host, Integer port, String username, String password) {
        return new BroadcastConfig(host, port, username, password);
    }

    /**
     * 静态工厂方法，创建完整配置实例
     */
    public static BroadcastConfig createFull(String host, Integer port, String username, String password,
                                           Integer connectTimeout, Integer readTimeout) {
        return new BroadcastConfig(host, port, username, password, connectTimeout, readTimeout);
    }
}