package cn.stylefeng.guns.modular.industrialTV.remoteClient.config;

import lombok.Data;

/**
 * 视频智能分析系统配置
 */
@Data
public class SmartSecurityConfig {

    /**
     * 视频智能分析服务器地址
     */
    private String host = "10.51.46.6";

    /**
     * 视频智能分析服务器端口
     */
    private Integer port = 80;

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
        return "http://" + host + (port != null && port != 80 ? ":" + port : "");
    }

    /**
     * 默认构造函数
     */
    public SmartSecurityConfig() {}

    /**
     * 构造函数，允许动态设置配置
     */
    public SmartSecurityConfig(String host, Integer port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    /**
     * 构造函数，允许设置全部配置
     */
    public SmartSecurityConfig(String host, Integer port, String username, String password,
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
    public static SmartSecurityConfig create(String host, Integer port, String username, String password) {
        return new SmartSecurityConfig(host, port, username, password);
    }

    /**
     * 静态工厂方法，创建完整配置实例
     */
    public static SmartSecurityConfig createFull(String host, Integer port, String username, String password,
                                                 Integer connectTimeout, Integer readTimeout) {
        return new SmartSecurityConfig(host, port, username, password, connectTimeout, readTimeout);
    }
}