package cn.stylefeng.guns.modular.deviceRelation.entity;

import lombok.Data;

/**
 * 关联设备弹窗中门禁设备列表 DTO
 */
@Data
public class AccessControlRelationDTO {

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

