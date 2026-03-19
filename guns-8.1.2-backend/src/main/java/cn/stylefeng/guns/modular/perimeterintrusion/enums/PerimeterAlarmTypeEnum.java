package cn.stylefeng.guns.modular.perimeterintrusion.enums;

/**
 * 周界报警类型枚举
 * 包含各类设备报警、入侵检测、环境监测、人员行为监测等报警类型
 */
public enum PerimeterAlarmTypeEnum {

    // 基础未知事件
    UNKNOWN("Unknown", "2", "未知事件", "【%s】【%s】未知报警事件", false),

    // 设备状态类
    DEVICE_OFFLINE("DeviceOffline", "2", "终端离线", "【%s】【%s】终端设备离线", false),
    FAULT_ALARM("FaultAlarm", "2", "设备故障", "【%s】【%s】设备故障报警", false),

    // 安全报警类
    FIRE_ALARM("FireAlarm", "1", "火警", "【%s】【%s】【%s】火警报警", true),
    INTRUSION_ALARM("IntrusionAlarm", "1", "入侵报警", "【%s】【%s】【%s】入侵报警", true),

    // 气体泄漏类
    CH4_LEAKAGE("CH4_Leakage", "1", "激光甲烷泄漏", "【%s】【%s】激光甲烷泄漏报警", false),
    GAS_LEAKAGE("Gas_Leakage", "1", "可燃气体泄漏", "【%s】【%s】可燃气体泄漏报警", false),

    // 人员行为类
    USER_ABSENCES("UserAbsences", "2", "人员离岗", "【%s】【%s】人员离岗报警", false),
    CROSS_REGION_DETECTION_EVENT("CrossRegionDetectionEvent", "2", "穿越警戒区域", "【%s】【%s】【%s】人员穿越警戒区域", true),
    LOITERING("loitering", "2", "徘徊侦测", "【%s】【%s】【%s】人员徘徊侦测报警", true),
    GROUP("group", "2", "人员聚集", "【%s】【%s】【%s】人员聚集报警", true),
    NO_WORK_CLOTHES("NoWorkClothes", "2", "工衣工服监测", "【%s】【%s】未按规定穿着工衣工服", false),
    NO_HELMET("NoHelmet", "2", "安全帽监测", "【%s】【%s】未佩戴安全帽", false),

    // 门禁设备类
    ACS_DEVICE_BREAK("acsDevice_break", "1", "设备防拆报警", "【%s】【%s】门禁设备防拆报警", false),
    ACS_DEVICE_CARD_READER_BREAK("acsDevice_cardReader_break", "1", "读卡器防拆报警", "【%s】【%s】读卡器防拆报警", false),
    ACS_FORCE_ALARM("acs_force_alarm", "1", "胁迫报警", "【%s】【%s】门禁胁迫报警", false),
    ACS_CARD_ALARM("acs_card_alarm", "2", "卡号认证失败超次报警", "【%s】【%s】卡号认证失败次数超限", false),

    // 位置报警类
    LOCATION_IN_ALARM("Location_InAlarm", "1", "进入报警", "【%s】【%s】【%s】非法进入报警区域", true),
    LOCATION_CROSS_ALARM("Location_CrossAlarm", "1", "越界报警", "【%s】【%s】【%s】越界报警", true),
    LOCATION_STAY_ALARM("Location_StayAlarm", "2", "滞留报警", "【%s】【%s】【%s】人员滞留报警", true),
    LOCATION_LACK_ALARM("Location_LackAlarm", "2", "缺员报警", "【%s】【%s】【%s】区域缺员报警", true),
    LOCATION_FULL_ALARM("Location_FullAlarm", "2", "超员报警", "【%s】【%s】【%s】区域超员报警", true),
    LOCATION_STATIC_ALARM("Location_StaticAlarm", "2", "静止报警", "【%s】【%s】【%s】人员静止超时报警", true),
    LOCATION_SOS_ALARM("Location_SOSAlarm", "1", "SOS报警", "【%s】【%s】【%s】SOS紧急报警", false),

    // 振动相关事件
    DVS_UNKNOW("DVS_Unknow", "2", "未知振动事件", "【%s】【%s】未知振动事件", false),
    DVS_VIBRATION_UNKNOWN("DVS_VibrationUnknown", "2", "某种振动事件", "【%s】【%s】异常振动事件", false),

    // 人工/机械作业相关
    DVS_PERSONNEL("DVS_Personnel", "2", "人工某种活动", "【%s】【%s】【%s】人工异常活动监测", true),
    DVS_PERSONNEL_DIGGING("DVS_PersonnelDigging", "2", "人工作业", "【%s】【%s】【%s】人工作业监测", true),
    DVS_PERSONNEL_DIGGING_WARN("DVS_PersonnelDiggingWarn", "3", "人工作业预警", "【%s】【%s】【%s】人工作业预警", true),
    DVS_VEHICLE("DVS_Vehicle", "2", "机械某种活动", "【%s】【%s】【%s】机械异常活动监测", true),
    DVS_VEHICLE_DIGGING("DVS_VehicleDigging", "2", "机械作业", "【%s】【%s】【%s】机械作业监测", true),
    DVS_VEHICLE_DIGGING_WARN("DVS_VehicleDiggingWarn", "3", "机械作业预警", "【%s】【%s】【%s】机械作业预警", true),

    // 周界入侵相关
    DVS_PERIMETER_INTRUDER("DVS_PerimeterIntruder", "1", "入侵报警", "【%s】【%s】【%s】周界入侵报警", true),
    DVS_PERIMETER_INTRUDER_WARN("DVS_PerimeterIntruderWarn", "3", "入侵预警", "【%s】【%s】【%s】周界入侵预警", true);

    // 原有保留类型
    // ZONE_ALARM("DVS_PerimeterIntruder", "2", "防区报警", "【%s】【%s】【%s】入侵报警", true),
    // HOST_OFFLINE("DeviceOffline", "2", "离线", "【%s】【%s】周界入侵主机离线", false),
    // HOST_FAULT("DVS_FaultAlarm", "2", "故障", "【%s】【%s】周界入侵主机故障报警", false);

    /**
     * 报警编码
     */
    private String code;

    /**
     * 报警级别（1-紧急 2-一般 3-预警）
     */
    private String level;

    /**
     * 报警类型名称
     */
    private String type;

    /**
     * 报警内容模板（%s为占位符，依次替换：区域/设备/时间等）
     */
    private String content;

    /**
     * 是否为防区类报警（是否关联具体防区）
     */
    private Boolean isZone;

    /**
     * 根据编码获取枚举实例
     * @param code 报警编码
     * @return 对应的枚举实例，无匹配返回null
     */
    public static PerimeterAlarmTypeEnum getByCode(String code) {
        for (PerimeterAlarmTypeEnum perimeterAlarmType : values()) {
            if (perimeterAlarmType.code.equals(code)) {
                return perimeterAlarmType;
            }
        }
        return null;
    }

    /**
     * 构造方法
     * @param code 报警编码
     * @param level 报警级别
     * @param type 报警类型名称
     * @param content 报警内容模板
     * @param isZone 是否为防区类报警
     */
    PerimeterAlarmTypeEnum(String code, String level, String type, String content, Boolean isZone) {
        this.code = code;
        this.level = level;
        this.type = type;
        this.content = content;
        this.isZone = isZone;
    }

    // Getter方法
    public String getCode() {
        return code;
    }

    public String getLevel() {
        return level;
    }

    public String getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public Boolean getIsZone() {
        return isZone;
    }
}