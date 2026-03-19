package cn.stylefeng.guns.modular.nodeSystem.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "node-system.kafka")
public class NodeSystemKafkaConfig {

    private String appId;

    private String appSecretKey;

    private String nodeCode;

    private Long maxMessageSize = 524288L;

    private Integer maxDataArrayLength = 50;

    private Long timeErrorRange = 300000L;

}
