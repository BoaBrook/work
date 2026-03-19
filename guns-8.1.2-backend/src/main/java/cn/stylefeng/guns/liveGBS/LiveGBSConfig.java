package cn.stylefeng.guns.liveGBS;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Slf4j
@Component
@ConfigurationProperties(prefix = "video.livegbs")
public class LiveGBSConfig {

    private String server;
    private String username;
    private String password;

}
