package cn.stylefeng.guns.modular.casClient.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "cas")
public class CasProperties {

    /**
     * CAS服务端地址
     */
    private String serverUrl = "http://171.7.4.234:8080/cas";

    /**
     * 票据验证地址
     */
    private String validateUrl = "http://171.7.4.234:8080/cas/p3/serviceValidate";

    /**
     * 子系统回调地址
     */
    private String callbackUrl = "http://171.7.9.231:9580/cas/callBack";

    /**
     * 子系统服务标识
     */
    private String service = "http://171.7.9.231:9580/cas/callBack";

    /**
     * 会话超时时间（秒），与TGT保持一致
     */
    private Integer sessionTimeout = 7200;

    /**
     * 测试账号（仅用于联调）
     */
    private String testUsername = "admin";

    /**
     * 是否启用CAS单点登录
     */
    private Boolean enabled = true;
}
