package cn.stylefeng.guns.modular.index.deviceStatusMonitor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class WebSocketService {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketService.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 广播设备状态更新
     */
    public void broadcastDeviceStatus(String deviceId, String status) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("type", "DEVICE_STATUS_UPDATE");
            message.put("deviceId", deviceId);
            message.put("status", status);
            message.put("timestamp", System.currentTimeMillis());

            // 发送到特定主题
            messagingTemplate.convertAndSend("/topic/device-status", message);

            // 也可以发送给特定设备订阅者
            messagingTemplate.convertAndSend("/topic/device-status/" + deviceId, message);

            logger.debug("WebSocket消息已发送: deviceId={}, status={}", deviceId, status);

        } catch (Exception e) {
            logger.error("发送WebSocket消息失败", e);
        }
    }

    /**
     * 发送给特定用户
     */
    public void sendToUser(String userId, String deviceId, String status) {
        Map<String, Object> message = new HashMap<>();
        message.put("deviceId", deviceId);
        message.put("status", status);

        messagingTemplate.convertAndSendToUser(
                userId,
                "/queue/device-status",
                message
        );
    }
}
