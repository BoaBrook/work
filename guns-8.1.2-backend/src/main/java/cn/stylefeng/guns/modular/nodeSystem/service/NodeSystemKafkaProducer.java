package cn.stylefeng.guns.modular.nodeSystem.service;

import cn.stylefeng.guns.modular.nodeSystem.config.NodeSystemKafkaConfig;
import cn.stylefeng.guns.modular.nodeSystem.constants.message.KafkaTopicConstants;
import cn.stylefeng.guns.modular.nodeSystem.dto.CommonMessage;
import cn.stylefeng.guns.modular.nodeSystem.service.NodeSystemKafkaLogService;
import cn.stylefeng.guns.modular.nodeSystem.service.impl.NodeSystemKafkaLogServiceImpl;
import cn.stylefeng.guns.modular.nodeSystem.util.SignatureUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class NodeSystemKafkaProducer {

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @Resource
    private NodeSystemKafkaConfig kafkaConfig;

    @Resource
    private NodeSystemKafkaLogService kafkaLogService;


    public <T> boolean sendMessage(String type, T data) {
        return sendMessage(type, data, null, true);
    }

    public <T> boolean sendMessage(String type, T data, String topic) {
        return sendMessage(type, data, topic, true);
    }

    public <T> boolean sendMessage(String type, T data, String topic, boolean saveLog) {
        long startTime = System.currentTimeMillis();
        CommonMessage<T> message = null;
        String jsonMessage = null;
        String finalTopic = null;

        try {
            message = buildMessage(type, data);
            jsonMessage = JSON.toJSONString(message);

            long messageSize = jsonMessage.getBytes().length;
            if (messageSize > kafkaConfig.getMaxMessageSize()) {
                log.error("Message size exceeds limit: {} bytes, max: {} bytes", messageSize, kafkaConfig.getMaxMessageSize());
                if (saveLog) {
                    kafkaLogService.saveProduceLog(message, null, NodeSystemKafkaLogServiceImpl.STATUS_FAILED, "Message size exceeds limit", jsonMessage, System.currentTimeMillis() - startTime);
                }
                return false;
            }

            if (data instanceof List) {
                List<?> list = (List<?>) data;
                if (list.size() > kafkaConfig.getMaxDataArrayLength()) {
                    log.error("Data array length exceeds limit: {}, max: {}", list.size(), kafkaConfig.getMaxDataArrayLength());
                    if (saveLog) {
                        kafkaLogService.saveProduceLog(message, null, NodeSystemKafkaLogServiceImpl.STATUS_FAILED, "Data array length exceeds limit", jsonMessage, System.currentTimeMillis() - startTime);
                    }
                    return false;
                }
            }

            if (ObjectUtils.isEmpty(topic)) {
                finalTopic = KafkaTopicConstants.getTopicName(kafkaConfig.getNodeCode(), type);
                if (finalTopic == null) {
                    log.error("Failed to generate topic name, nodeCode: {}, messageType: {}", kafkaConfig.getNodeCode(), type);
                    if (saveLog) {
                        kafkaLogService.saveProduceLog(message, null, NodeSystemKafkaLogServiceImpl.STATUS_FAILED, "Failed to generate topic name", jsonMessage, System.currentTimeMillis() - startTime);
                    }
                    return false;
                }
            } else {
                finalTopic = topic;
            }

            ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(finalTopic, jsonMessage);
            CommonMessage<T> finalMessage = message;
            String finalTopicName = finalTopic;
            String finalJsonMessage = jsonMessage;

            future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
                @Override
                public void onSuccess(SendResult<String, String> result) {
                    log.info("Kafka message sent successfully, topic: {}, msgId: {}", finalTopicName, finalMessage.getMsgId());
                    if (saveLog) {
                        kafkaLogService.saveProduceLog(finalMessage, finalTopicName, NodeSystemKafkaLogServiceImpl.STATUS_SUCCESS, null, finalJsonMessage, System.currentTimeMillis() - startTime);
                    }
                }

                @Override
                public void onFailure(Throwable ex) {
                    log.error("Failed to send Kafka message, topic: {}, msgId: {}", finalTopicName, finalMessage.getMsgId(), ex);
                    if (saveLog) {
                        kafkaLogService.saveProduceLog(finalMessage, finalTopicName, NodeSystemKafkaLogServiceImpl.STATUS_FAILED, ex.getMessage(), finalJsonMessage, System.currentTimeMillis() - startTime);
                    }
                }
            });

            return true;
        } catch (Exception e) {
            log.error("Failed to send message", e);
            if (saveLog && message != null) {
                kafkaLogService.saveProduceLog(message, finalTopic, NodeSystemKafkaLogServiceImpl.STATUS_FAILED, e.getMessage(), jsonMessage, System.currentTimeMillis() - startTime);
            }
            return false;
        }
    }

    private <T> CommonMessage<T> buildMessage(String type, T data) {
        CommonMessage<T> message = new CommonMessage<>();
        message.setMsgId(generateMsgId());
        message.setNodeCode(kafkaConfig.getNodeCode());
        message.setAppId(kafkaConfig.getAppId());
        message.setReportTime(System.currentTimeMillis() / 1000);
        message.setType(type);
        message.setData(data);
        String sign = SignatureUtil.generateSign(
            message.getNodeCode(),
            message.getAppId(),
            kafkaConfig.getAppSecretKey(),
            message.getReportTime()
        );
        message.setSign(sign);
        return message;
    }

    private String generateMsgId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

}
