package cn.stylefeng.guns.modular.videoStreamMedia.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 视频流媒体服务配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "video-stream-media")
public class VideoStreamMediaConfig {

    /**
     * 视频流媒体服务器地址
     */
    private String host;

    /**
     * 视频流媒体服务器端口
     */
    private Integer port;

    /**
     * 连接超时时间（毫秒）
     */
    private Integer connectTimeout;

    /**
     * 读取超时时间（毫秒）
     */
    private Integer readTimeout;

    /**
     * 获取完整的API基础URL
     */
    public String getBaseUrl() {
        return "http://" + host + (port != null && port != 80 ? ":" + port : "");
    }

    /**
     * 获取API前缀
     */
    public String getApiPrefix() {
        return "/api";
    }

    /**
     * 获取完整的API URL
     */
    public String getApiUrl() {
        return getBaseUrl() + getApiPrefix();
    }

    /**
     * 默认构造函数
     */
    public VideoStreamMediaConfig() {}

    /**
     * 构造函数，允许动态设置配置
     */
    public VideoStreamMediaConfig(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    /**
     * 构造函数，允许设置全部配置
     */
    public VideoStreamMediaConfig(String host, Integer port, Integer connectTimeout, Integer readTimeout) {
        this.host = host;
        this.port = port;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
    }

    /**
     * 静态工厂方法，创建配置实例
     */
    public static VideoStreamMediaConfig create(String host, Integer port) {
        return new VideoStreamMediaConfig(host, port);
    }

    /**
     * 静态工厂方法，创建完整配置实例
     */
    public static VideoStreamMediaConfig createFull(String host, Integer port, Integer connectTimeout, Integer readTimeout) {
        return new VideoStreamMediaConfig(host, port, connectTimeout, readTimeout);
    }
}