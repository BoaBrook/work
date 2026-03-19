package cn.stylefeng.guns.modular.nodeSystem.service;

import cn.stylefeng.guns.modular.nodeSystem.dto.CommonMessage;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.stylefeng.guns.modular.nodeSystem.entity.NodeSystemKafkaLog;

public interface NodeSystemKafkaLogService extends IService<NodeSystemKafkaLog> {

    <T> void saveProduceLog(CommonMessage<T> message, String topic, Integer status, String errorMessage, String messageContent, Long processTime);

    <T> void saveConsumeLog(CommonMessage<T> message, String topic, Integer partition, Long offset, Integer status, String errorMessage, String messageContent, Long processTime);
    
}
