package cn.stylefeng.guns.modular.industrialTV.remoteClient.dto;

import lombok.Data;

/**
 * 视频流信息
 */
@Data
public class VideoStreamInfo {
    /**
     * 视频流地址
     */
    private String aqsUrl;

    /**
     * 摄像头名称
     */
    private String channelAlias;

    /**
     * 流媒体国标通道ID
     */
    private String cameraIndexCode;

    /**
     * 流媒体国标设备ID
     */
    private String deviceCode;

    /**
     * 摄像头主键ID
     */
    private String cameraId;

    /**
     * 视频流UUID
     */
    private String streamUuid;
}