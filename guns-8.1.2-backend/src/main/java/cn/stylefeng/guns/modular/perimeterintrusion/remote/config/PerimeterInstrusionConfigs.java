package cn.stylefeng.guns.modular.perimeterintrusion.remote.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "perimeter-intrusion")
public class PerimeterInstrusionConfigs {

    private List<PerimeterInstrusionConfig> configs;
    @Data
    public static class PerimeterInstrusionConfig{
        private String host = "localhost";

        private Integer port = 8080;

        private String apiKey;
    }
}
