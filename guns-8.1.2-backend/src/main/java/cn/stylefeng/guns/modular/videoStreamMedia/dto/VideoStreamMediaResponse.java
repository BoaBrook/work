package cn.stylefeng.guns.modular.videoStreamMedia.dto;

import lombok.Data;

/**
 * 视频流媒体服务通用响应
 */
@Data
public class VideoStreamMediaResponse<T> {
    /**
     * 返回数据
     */
    private T data;

    /**
     * 提示信息
     */
    private String message;

    /**
     * 请求是否成功
     */
    private Boolean success;

    /**
     * 数据数量
     */
    private Integer count;

    private String cameraName;

    /**
     * 判断请求是否成功
     */
    public boolean isSuccess() {
        return success != null && success;
    }
}