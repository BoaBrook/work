package cn.stylefeng.guns.modular.nodeSystem.constants.message;

/**
 * Kafka主题常量
 */
public class KafkaTopicConstants {

    /**
     * 节点状态
     */
    public static final String TOPIC_PREFIX_NODE_STATUS = "platform_status_";

    /**
     * 设备清单
     */
    public static final String TOPIC_PREFIX_DEVICE_LIST = "device_inventory_";

    /**
     * 设备汇总统计
     */
    public static final String TOPIC_PREFIX_DEVICE_SUMMARY_STATISTICS = "device_aggregation_";

    /**
     * 设备状态
     */
    public static final String TOPIC_PREFIX_DEVICE_STATUS = "device_status_event_";

    /**
     * 报警信息数据
     */
    public static final String TOPIC_PREFIX_ALARM_INFORMATION_DATA = "alarm_raw_";

    /**
     * 报警处置
     */
    public static final String TOPIC_PREFIX_ALARM_HANDLING = "alarm_raw_handle_";

    /**
     * 指令下发
     */
    public static final String TOPIC_PREFIX_COMMAND_ISSUANCE = "command_downlink_";

    /**
     * 作业计划
     */
    public static final String TOPIC_PREFIX_WORK_PLAN = "job_plan_";

    /**
     * 激光云台设备实时数据
     */
    public static final String TOPIC_PREFIX_LASER_GIMBAL_DEVICE_REALTIME_DATA = "device_real_time_info_";


    public static String getTopicName(String nodeCode, String messageTypeCode) {
        String prefix = getTopicPrefix(messageTypeCode);
        if (prefix == null || nodeCode == null) {
            return null;
        }
        return prefix + nodeCode.toLowerCase();
    }

    public static String getTopicPrefix(String messageTypeCode) {
        if (messageTypeCode == null) {
            return null;
        }
        switch (messageTypeCode) {
            case MessageTypeConstants.NODE_STATUS:
                return TOPIC_PREFIX_NODE_STATUS;
            case MessageTypeConstants.DEVICE_LIST:
                return TOPIC_PREFIX_DEVICE_LIST;
            case MessageTypeConstants.DEVICE_SUMMARY_STATISTICS:
                return TOPIC_PREFIX_DEVICE_SUMMARY_STATISTICS;
            case MessageTypeConstants.DEVICE_STATUS:
                return TOPIC_PREFIX_DEVICE_STATUS;
            case MessageTypeConstants.ALARM_INFORMATION_DATA:
                return TOPIC_PREFIX_ALARM_INFORMATION_DATA;
            case MessageTypeConstants.ALARM_HANDLING:
                return TOPIC_PREFIX_ALARM_HANDLING;
            case MessageTypeConstants.COMMAND_ISSUANCE:
                return TOPIC_PREFIX_COMMAND_ISSUANCE;
            case MessageTypeConstants.WORK_PLAN:
                return TOPIC_PREFIX_WORK_PLAN;
            case MessageTypeConstants.LASER_GIMBAL_DEVICE_REALTIME_DATA:
                return TOPIC_PREFIX_LASER_GIMBAL_DEVICE_REALTIME_DATA;
            default:
                return null;
        }
    }

}
