package cn.stylefeng.guns.modular.nodeSystem.service;

import cn.stylefeng.guns.modular.nodeSystem.config.NodeSystemKafkaConfig;
import cn.stylefeng.guns.modular.nodeSystem.constants.message.MessageTypeConstants;
import cn.stylefeng.guns.modular.nodeSystem.dto.AlarmRawDTO;
import cn.stylefeng.guns.modular.nodeSystem.dto.AlarmRawHandleDTO;
import cn.stylefeng.guns.modular.nodeSystem.dto.DeviceAggregationDTO;
import cn.stylefeng.guns.modular.nodeSystem.dto.DeviceInventoryDTO;
import cn.stylefeng.guns.modular.nodeSystem.dto.DeviceStatusDTO;
import cn.stylefeng.guns.modular.nodeSystem.dto.JobPlanDTO;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class NodeSystemService {

    @Resource
    private NodeSystemKafkaProducer kafkaProducer;

    @Resource
    private NodeSystemKafkaConfig kafkaConfig;

    @Scheduled(cron = "0 * * * * ?")
    public void sendNodeStatus() {
        try {
            Map<String, Object> data = new HashMap<>();
            boolean success = kafkaProducer.sendMessage(MessageTypeConstants.NODE_STATUS, data, null, false);
            if (success) {
                log.info("Node status sent successfully");
            } else {
                log.error("Failed to send node status");
            }
        } catch (Exception e) {
            log.error("Error sending node status", e);
        }
    }

    /**
     * 获取节点编码
     **/
    public String getNodeCode() {
        return kafkaConfig.getNodeCode();
    }

    /**
     * 用途：节点设备清单数据上报，设备新增、编辑、删除时上报。
     * 频率：初始全量发送、变更时增量发送
     */
    public boolean sendDeviceInventory(List<DeviceInventoryDTO> deviceList) {
        try {
            if (ObjectUtils.isEmpty(deviceList)) {
                log.warn("DeviceInventory is empty, skip sending");
                return false;
            }
            return kafkaProducer.sendMessage(MessageTypeConstants.DEVICE_LIST, deviceList);
        } catch (Exception e) {
            log.error("Error sending DeviceInventory: ", e);
            return false;
        }
    }

    /**
     * 用途：上报场站设备汇总数据，按照设备类型进行汇总
     * 频率：设备数量或状态变更时上报，或省平台下发相应指令时节点平台及时上报
     */
    public boolean sendDeviceAggregation(List<DeviceAggregationDTO> deviceAggregationList) {
        try {
            if (ObjectUtils.isEmpty(deviceAggregationList)) {
                log.warn("Device aggregation list is empty, skip sending");
                return false;
            }
            return kafkaProducer.sendMessage(MessageTypeConstants.DEVICE_SUMMARY_STATISTICS, deviceAggregationList);
        } catch (Exception e) {
            log.error("Error sending device aggregation", e);
            return false;
        }
    }

    /**
     * 用途：设备上线/离线时上报设备状态。
     * 频率：设备上线/离线时上报
     */
    public boolean sendDeviceStatus(DeviceStatusDTO deviceStatus) {
        try {
            if (deviceStatus == null) {
                log.warn("Device status is null, skip sending");
                return false;
            }
            return kafkaProducer.sendMessage(MessageTypeConstants.DEVICE_STATUS, deviceStatus);
        } catch (Exception e) {
            log.error("Error sending device status", e);
            return false;
        }
    }

    /**
     * 用途：节点上报告警数据
     * 频率：告警发生时及时发送
     */
    public boolean sendAlarmRaw(AlarmRawDTO alarmRaw) {
        try {
            if (alarmRaw == null) {
                log.warn("Alarm raw is null, skip sending");
                return false;
            }
            return kafkaProducer.sendMessage(MessageTypeConstants.ALARM_INFORMATION_DATA, alarmRaw);
        } catch (Exception e) {
            log.error("Error sending alarm raw", e);
            return false;
        }
    }

    /**
     * 用途：告警响应或告警处置完成时上报告警处置信息
     * 频率：告警处置响应或完成时上报
     */
    public boolean sendAlarmRawHandle(AlarmRawHandleDTO alarmRawHandle) {
        try {
            if (alarmRawHandle == null) {
                log.warn("Alarm raw handle is null, skip sending");
                return false;
            }
            return kafkaProducer.sendMessage(MessageTypeConstants.ALARM_HANDLING, alarmRawHandle);
        } catch (Exception e) {
            log.error("Error sending alarm raw handle", e);
            return false;
        }
    }

    /**
     * 用途：节点上报作业计划
     * 频率：按需上报
     */
    public boolean sendJobPlan(JobPlanDTO jobPlan) {
        try {
            if (jobPlan == null) {
                log.warn("Job plan is null, skip sending");
                return false;
            }
            return kafkaProducer.sendMessage(MessageTypeConstants.WORK_PLAN, jobPlan);
        } catch (Exception e) {
            log.error("Error sending job plan", e);
            return false;
        }
    }

}
