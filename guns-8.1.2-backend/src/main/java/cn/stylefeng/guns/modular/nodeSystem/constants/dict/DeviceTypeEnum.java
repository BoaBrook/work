package cn.stylefeng.guns.modular.nodeSystem.constants.dict;

import lombok.Getter;

/**
 * 设备类型枚举
 */
@Getter
public enum DeviceTypeEnum {

    PERIMETER_INTRUSION("ZJRQ", "周界入侵"),
    PERIMETER_DEFENSE_ZONE("ZJFQ", "周界防区"),
    ACCESS_CONTROL_SYSTEM("MJXT", "门禁系统"),
    INDUSTRIAL_TELEVISION("GYDS", "工业电视"),
    EMERGENCY_BROADCAST("YJGB", "应急广播"),
    FIRE_GAS_SYSTEM("HQXT", "火气系统"),
    PERSONNEL_POSITIONING("RYDW", "人员定位"),
    LASER_GIMBAL("JGYT", "激光云台");

    private final String code;
    private final String description;

    DeviceTypeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static DeviceTypeEnum getByCode(String code) {
        for (DeviceTypeEnum deviceType : values()) {
            if (deviceType.code.equals(code)) {
                return deviceType;
            }
        }
        return null;
    }

    public static String getDescriptionByCode(String code) {
        for (DeviceTypeEnum deviceType : values()) {
            if (deviceType.code.equals(code)) {
                return deviceType.description;
            }
        }
        return null;
    }

}
