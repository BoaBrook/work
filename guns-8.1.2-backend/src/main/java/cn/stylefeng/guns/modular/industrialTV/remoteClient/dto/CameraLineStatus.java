package cn.stylefeng.guns.modular.industrialTV.remoteClient.dto;

import lombok.Data;

/**
 * 摄像头在线状态
 */
@Data
public class CameraLineStatus {
    /**
     * 流媒体国标通道ID
     */
    private String streamChannelSerial;

    /**
     * 在线状态 normal正常，offline离线
     */
    private String lineStatus;
}