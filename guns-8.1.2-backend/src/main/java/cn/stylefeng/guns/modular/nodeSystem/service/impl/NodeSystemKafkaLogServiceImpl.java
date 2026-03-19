package cn.stylefeng.guns.modular.nodeSystem.service.impl;

import cn.stylefeng.guns.modular.nodeSystem.config.NodeSystemKafkaConfig;
import cn.stylefeng.guns.modular.nodeSystem.dto.CommonMessage;
import cn.stylefeng.guns.modular.nodeSystem.entity.NodeSystemKafkaLog;
import cn.stylefeng.guns.modular.nodeSystem.mapper.NodeSystemKafkaLogMapper;
import cn.stylefeng.guns.modular.nodeSystem.service.NodeSystemKafkaLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class NodeSystemKafkaLogServiceImpl extends ServiceImpl<NodeSystemKafkaLogMapper, NodeSystemKafkaLog> implements NodeSystemKafkaLogService {

    @Resource
    private NodeSystemKafkaLogMapper nodeSystemKafkaLogMapper;

    @Resource
    private NodeSystemKafkaConfig kafkaConfig;

    /**
     * 操作类型：生产
     */
    public static final String OPERATION_TYPE_PRODUCE = "PRODUCE";

    /**
     * 操作类型：消费
     */
    public static final String OPERATION_TYPE_CONSUME = "CONSUME";

    /**
     * 状态：成功
     */
    public static final Integer STATUS_SUCCESS = 1;

    /**
     * 状态：失败
     */
    public static final Integer STATUS_FAILED = 2;

    @Override
    public <T> void saveProduceLog(CommonMessage<T> message, String topic, Integer status, String errorMessage, String messageContent, Long processTime) {
        saveKafkaLog(message, OPERATION_TYPE_PRODUCE, topic, null, null, status, errorMessage, messageContent, processTime);
    }

    @Override
    public <T> void saveConsumeLog(CommonMessage<T> message, String topic, Integer partition, Long offset, Integer status, String errorMessage, String messageContent, Long processTime) {
        saveKafkaLog(message, OPERATION_TYPE_CONSUME, topic, partition, offset, status, errorMessage, messageContent, processTime);
    }


    private <T> void saveKafkaLog(CommonMessage<T> message, String operationType, String topic, Integer partition, Long offset, Integer status, String errorMessage, String messageContent, Long processTime) {
        try {
            NodeSystemKafkaLog kafkaLog = new NodeSystemKafkaLog();
            kafkaLog.setMsgId(message != null ? message.getMsgId() : null);
            kafkaLog.setOperationType(operationType);
            kafkaLog.setTopic(topic);
            kafkaLog.setPartitionId(partition);
            kafkaLog.setOffsetIdx(offset);
            kafkaLog.setMessageType(message != null ? message.getType() : null);
            kafkaLog.setNodeCode(message != null ? message.getNodeCode() : kafkaConfig.getNodeCode());
            kafkaLog.setStatus(status);
            kafkaLog.setErrorMessage(errorMessage);
            kafkaLog.setMessageContent(messageContent);
            kafkaLog.setProcessTime(processTime);
            this.save(kafkaLog);
        } catch (Exception e) {
            log.error("Failed to save Kafka log", e);
        }
    }

}

