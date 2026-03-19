package cn.stylefeng.guns.modular.deviceRelation.entity;

import lombok.Data;

/**
 * 应急广播设备列表 DTO
 */
@Data
public class EmergencyBroadcastRelationDTO {

    private String deviceId;

    private String deviceName;

    private String deviceCode;

    private String brand;

    private String model;

    private String ipAddress;

    /**
     * 是否已被当前子系统 / 当前设备关联
     */
    private Boolean checked;
}

