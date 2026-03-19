package cn.stylefeng.guns.modular.industrialTV.remoteClient.dto;

import lombok.Data;

/**
 * 摄像头信息
 */
@Data
public class CameraInfo {
    /**
     * 摄像头别名
     */
    private String channelAlias;

    /**
     * 设备类型 aqsCamera，camera、RTSP
     */
    private String devType;

    /**
     * 摄像头ID
     */
    private Integer channelId;

    /**
     * 图片路径（需加上http://10.56.41.6 进行访问）
     */
    private String photoPath;

    /**
     * 区域ID
     */
    private Integer orgId;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 纬度
     */
    private Double dimension;

    /**
     * 当前摄像头开启的任务
     */
    private String openTasks;

    /**
     * 在线状态 normal在线，offline离线
     */
    private String lineStatus;

    /**
     * 流媒体国标设备ID
     */
    private String streamSerial;

    /**
     * 流媒体国标通道ID
     */
    private String streamChannelSerial;
}