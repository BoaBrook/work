package cn.stylefeng.guns.modular.hikvision.request;

import lombok.Data;

/**
 * 实时预览请求参数
 */
@Data
public class PreviewRequest {

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 通道号 (默认为1)
     */
    private Integer channel;

    /**
     * 码流类型 (0-主码流, 1-子码流, 2-第三码流)
     */
    private Integer streamType;

    /**
     * 协议类型 (0-TCP, 1-UDP, 2-多播)
     */
    private Integer protocolType;

}
