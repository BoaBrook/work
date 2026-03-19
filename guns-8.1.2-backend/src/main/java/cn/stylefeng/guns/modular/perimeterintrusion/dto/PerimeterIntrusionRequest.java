package cn.stylefeng.guns.modular.perimeterintrusion.dto;

import lombok.Data;

@Data
public class PerimeterIntrusionRequest {

    /**
     * 报警 id
     */
    private String alarmId;

    /**
     * 所属父设备物联编码
     */
    private String tid;

    /**
     * 防区名称（可选）
     */
    private String defenceAreaName;

    /**
     * 报警设备物联编码
     */
    private String code;

    /**
     * 级别
     */
    private Integer level;

    /**
     * 报警类型编码（例如：DVS_VehicleDigging）
     */
    private String type;

    /**
     * 仪表名称
     */
    private String deviceName;

    /**
     * 报警类型文字描述（例如：机械作业）
     */
    private String eventType;

    /**
     * 开始时间，格式：yyyy-MM-dd HH:mm:ss
     */
    private String startTime;

    /**
     * 报警次数
     */
    private Integer alarmTimes;

    /**
     * 报警设备所在区域名称
     */
    private String areaName;
}
