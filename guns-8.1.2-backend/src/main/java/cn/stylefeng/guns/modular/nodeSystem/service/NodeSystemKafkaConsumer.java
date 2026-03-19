package cn.stylefeng.guns.modular.nodeSystem.service;

import cn.stylefeng.guns.modular.nodeSystem.config.NodeSystemKafkaConfig;
import cn.stylefeng.guns.modular.nodeSystem.constants.message.KafkaTopicConstants;
import cn.stylefeng.guns.modular.nodeSystem.constants.message.MessageTypeConstants;
import cn.stylefeng.guns.modular.nodeSystem.dto.CommandDownlinkDTO;
import cn.stylefeng.guns.modular.nodeSystem.dto.CommandDownlinkDTO.JobParameters;
import cn.stylefeng.guns.modular.nodeSystem.dto.CommonMessage;
import cn.stylefeng.guns.modular.nodeSystem.service.NodeSystemKafkaLogService;
import cn.stylefeng.guns.modular.nodeSystem.service.impl.NodeSystemKafkaLogServiceImpl;
import cn.stylefeng.guns.modular.nodeSystem.util.SignatureUtil;
import cn.stylefeng.guns.modular.report.service.impl.ReportRecordsServiceImpl;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Slf4j
@Service
public class NodeSystemKafkaConsumer {

    @Resource
    private NodeSystemKafkaConfig kafkaConfig;

    @Resource
    private ReportRecordsServiceImpl reportRecordsService;

    @Resource
    private NodeSystemKafkaLogService kafkaLogService;

    @Getter
    private String commandIssuanceTopic;

    /**
     * 操作类型：设备汇总统计
     */
    private static final String OPERATION_TYPE_DEVICE_AGGREGATION = "HZTJ";

    /**
     * 操作类型：作业计划反馈
     */
    private static final String OPERATION_TYPE_JOB_PLAN_FEEDBACK = "JPFK";

    @PostConstruct
    public void init() {
        commandIssuanceTopic = KafkaTopicConstants.getTopicName(
            kafkaConfig.getNodeCode(),
            MessageTypeConstants.COMMAND_ISSUANCE
        );
        if (commandIssuanceTopic == null) {
            log.error("Failed to get command issuance topic, nodeCode: {}", kafkaConfig.getNodeCode());
        } else {
            log.info("Command issuance topic initialized: {}", commandIssuanceTopic);
        }
    }

    @KafkaListener(topics = "#{@nodeSystemKafkaConsumer.getCommandIssuanceTopic()}")
    public void receiveMessage(
        @Payload String message,
        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
        @Header(KafkaHeaders.OFFSET) long offset) {
        long startTime = System.currentTimeMillis();
        CommonMessage<CommandDownlinkDTO> commonMessage = null;
        String errorMessage;
        
        try {
            log.info("Received Kafka message, topic: {}, partition: {}, offset: {}", topic, partition, offset);
            commonMessage = JSON.parseObject(message, new TypeReference<CommonMessage<CommandDownlinkDTO>>() {});

            if (!validateSign(commonMessage)) {
                errorMessage = "Invalid signature";
                log.error("Invalid signature, msgId: {}", commonMessage.getMsgId());
                kafkaLogService.saveConsumeLog(commonMessage, topic, partition, offset, NodeSystemKafkaLogServiceImpl.STATUS_FAILED, errorMessage, message, System.currentTimeMillis() - startTime);
                return;
            }
            if (!validateTime(commonMessage.getReportTime())) {
                errorMessage = "Time error exceeds limit";
                log.error("Time error exceeds limit, msgId: {}, reportTime: {}", commonMessage.getMsgId(), commonMessage.getReportTime());
                kafkaLogService.saveConsumeLog(commonMessage, topic, partition, offset, NodeSystemKafkaLogServiceImpl.STATUS_FAILED, errorMessage, message, System.currentTimeMillis() - startTime);
                return;
            }
            if (!MessageTypeConstants.COMMAND_ISSUANCE.equals(commonMessage.getType())) {
                errorMessage = "Received non-command-issuance message";
                log.warn("Received non-command-issuance message, type: {}, msgId: {}", commonMessage.getType(), commonMessage.getMsgId());
                kafkaLogService.saveConsumeLog(commonMessage, topic, partition, offset, NodeSystemKafkaLogServiceImpl.STATUS_FAILED, errorMessage, message, System.currentTimeMillis() - startTime);
                return;
            }

            handleCommandIssuance(commonMessage);
            log.info("Command issuance message processed successfully, msgId: {}", commonMessage.getMsgId());
            kafkaLogService.saveConsumeLog(commonMessage, topic, partition, offset, NodeSystemKafkaLogServiceImpl.STATUS_SUCCESS, null, message, System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            log.error("Failed to process Kafka message, topic: {}, partition: {}, offset: {}", topic, partition, offset, e);
            kafkaLogService.saveConsumeLog(commonMessage, topic, partition, offset, NodeSystemKafkaLogServiceImpl.STATUS_FAILED, e.getMessage(), message, System.currentTimeMillis() - startTime);
        }
    }

    private void handleCommandIssuance(CommonMessage<CommandDownlinkDTO> message) {
        CommandDownlinkDTO commandData = message.getData();
        if (commandData == null) {
            log.warn("Command data is null, msgId: {}", message.getMsgId());
            return;
        }
        String operationType = commandData.getOperationType();
        log.info("Processing command issuance, msgId: {}, operationType: {}", message.getMsgId(), operationType);
        if (OPERATION_TYPE_DEVICE_AGGREGATION.equals(operationType)) {
            handleDeviceAggregationCommand(commandData);
        } else if (OPERATION_TYPE_JOB_PLAN_FEEDBACK.equals(operationType)) {
            handleJobPlanFeedbackCommand(commandData);
        } else {
            log.warn("Unknown operation type: {}, msgId: {}", operationType, message.getMsgId());
        }
    }

    private void handleDeviceAggregationCommand(CommandDownlinkDTO commandData) {
        log.info("Handling device aggregation command, commandTime: {}", commandData.getCommandTime());
        // TODO: 实现设备汇总统计指令处理逻辑   ----待调用设备汇总统计方法
    }

    private void handleJobPlanFeedbackCommand(CommandDownlinkDTO commandData) {
        log.info("Handling job plan feedback command, commandTime: {}", commandData.getCommandTime());
        
        String parametersJson = commandData.getParameters();
        if (ObjectUtils.isEmpty(parametersJson)) {
            log.warn("Job parameters is null or empty, skip handling job plan feedback");
            return;
        }
        try {
            JobParameters parameters = JSON.parseObject(parametersJson, JobParameters.class);
            if (parameters == null) {
                log.warn("Failed to parse job parameters, parametersJson: {}", parametersJson);
                return;
            }
            
            String jobId = parameters.getJobId();
            Integer status = parameters.getStatus();
            String rejectReason = parameters.getRejectReason();
            String checkerName = parameters.getCheckerName();
            String commandTime = commandData.getCommandTime();
            reportRecordsService.handleJobPlanFeedback(jobId, status, rejectReason, checkerName, commandTime);
        } catch (Exception e) {
            log.error("Failed to parse job parameters JSON, parametersJson: {}", parametersJson, e);
        }
    }

    private boolean validateSign(CommonMessage<?> message) {
        String expectedSign = SignatureUtil.generateSign(
            message.getNodeCode(),
            message.getAppId(),
            kafkaConfig.getAppSecretKey(),
            message.getReportTime()
        );
        return expectedSign.equals(message.getSign());
    }

    private boolean validateTime(Long reportTime) {
        long currentTime = System.currentTimeMillis() / 1000;
        long timeDiff = Math.abs(currentTime - reportTime);
        return timeDiff <= (kafkaConfig.getTimeErrorRange() / 1000);
    }

}
