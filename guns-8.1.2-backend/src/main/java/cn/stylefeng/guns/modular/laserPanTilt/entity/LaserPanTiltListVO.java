package cn.stylefeng.guns.modular.laserPanTilt.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class LaserPanTiltListVO implements Serializable {

    private String deviceId;
    private String deviceCode;
    private String deviceName;
    private String belongStationId;
    private String belongStationAreaId;
    private String brand;
    private String model;
    private String ipAddress;
    private Integer port;
    private String remark;
    private String inspectionStatus;

    private String belongStationName;
    private String belongOperationArea;
    private String belongPipeline;
    private ThresholdConfigVO thresholdConfig;
}
