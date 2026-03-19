package cn.stylefeng.guns.modular.index.deviceStatusMonitor;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 启用简单的内存消息代理
        config.enableSimpleBroker("/topic", "/queue");

        // 设置应用程序目标前缀
        config.setApplicationDestinationPrefixes("/app");

        // 用户目标前缀
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 注册WebSocket端点
        registry.addEndpoint("/ws-device")
                .setAllowedOriginPatterns("*")
                .withSockJS();

        // 纯WebSocket端点（不支持SockJS）
        registry.addEndpoint("/ws-device-native")
                .setAllowedOriginPatterns("*");
    }
}
