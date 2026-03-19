package cn.stylefeng.guns.modular.datimsien;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "sys.config.datimsien")
public class DatimsienConfig {

    /**
     * rt/ht api url
     */
    private String serviceBaseURL;

    /**
     * 用户名
     */
    private String user;

    /**
     * 密码
     */
    private String password;

    /**
     * websocket service url
     */
    private String websocketURL;
}
