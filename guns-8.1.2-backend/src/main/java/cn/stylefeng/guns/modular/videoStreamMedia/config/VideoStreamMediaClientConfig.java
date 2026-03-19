package cn.stylefeng.guns.modular.videoStreamMedia.config;

import cn.stylefeng.guns.modular.videoStreamMedia.client.VideoStreamMediaClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * 视频流媒体服务客户端配置
 */
@Slf4j
@Configuration
public class VideoStreamMediaClientConfig {

    @Autowired
    private VideoStreamMediaConfig config;

    /**
     * RestTemplate Bean（如果容器中没有，则创建）
     */
    @Bean
    @ConditionalOnMissingBean(RestTemplate.class)
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(config.getConnectTimeout());
        factory.setReadTimeout(config.getReadTimeout());
        return new RestTemplate(factory);
    }

//    /**
//     * ObjectMapper Bean（如果容器中没有，则创建）
//     */
//    @Bean
//    @ConditionalOnMissingBean(ObjectMapper.class)
//    public ObjectMapper objectMapper() {
//        return new ObjectMapper();
//    }

    /**
     * 视频流媒体服务客户端Bean
     */
    @Bean
    public VideoStreamMediaClient videoStreamMediaClient(VideoStreamMediaConfig config, RestTemplate restTemplate, ObjectMapper objectMapper) {
        VideoStreamMediaClient client = new VideoStreamMediaClient();
        client.initialize(config, restTemplate, objectMapper);
        log.info("初始化视频流媒体服务客户端成功");
        return client;
    }
}