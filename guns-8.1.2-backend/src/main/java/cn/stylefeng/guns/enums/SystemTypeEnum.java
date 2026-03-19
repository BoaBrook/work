package cn.stylefeng.guns.enums;

import cn.stylefeng.guns.database.service.*;
import com.baomidou.mybatisplus.extension.service.IService;

public enum SystemTypeEnum {

    GYDS("GYDS", "工业电视", TIndustrialTvBaseInfoService.class),
    MJXT("MJXT", "门禁系统", TAccessControlBaseInfoService.class),
    ZJRQ("ZJRQ", "周界入侵", TPerimeterIntrusionHostBaseInfoService.class),
    HQXT("HQXT", "火气系统", TFireGasHostBaseInfoService.class),
    YJGB("YJGB", "应急广播", TEmergencyBroadcastHostBaseInfoService.class),
    RYDW("RYDW", "人员定位", TPersonnelPositionHostBaseInfoService.class),
    JGYT("JGYT", "激光云台", TLaserPanTiltBaseInfoService.class);

    private final String code;
    private final String description;
    private final Class<? extends IService> service;

    SystemTypeEnum(String code, String description, Class<? extends IService> service) {
        this.code = code;
        this.description = description;
        this.service = service;
    }

    public static SystemTypeEnum getByCode(String code) {
        for (SystemTypeEnum systemType : values()) {
            if (systemType.code.equals(code)) {
                return systemType;
            }
        }
        return null;
    }

    public static String getDescriptionByCode(String code) {
        for (SystemTypeEnum systemType : values()) {
            if (systemType.code.equals(code)) {
                return systemType.description;
            }
        }
        return null;
    }

    public static Class<? extends IService> getServiceByCode(String code) {
        for (SystemTypeEnum systemType : values()) {
            if (systemType.code.equals(code)) {
                return systemType.service;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public Class<? extends IService> getService() {
        return service;
    }

}
