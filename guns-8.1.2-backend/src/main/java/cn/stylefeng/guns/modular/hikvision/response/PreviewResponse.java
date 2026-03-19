package cn.stylefeng.guns.modular.hikvision.response;

import lombok.Data;

/**
 * 实时预览响应
 */
@Data
public class PreviewResponse {

    /**
     * 预览句柄
     */
    private Integer previewHandle;

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 流地址 (RTSP/HLS等)
     */
    private String streamUrl;

    /**
     * 预览状态 (0-未开始, 1-预览中, 2-已停止)
     */
    private Integer status;

    /**
     * 错误信息
     */
    private String errorMsg;

}
