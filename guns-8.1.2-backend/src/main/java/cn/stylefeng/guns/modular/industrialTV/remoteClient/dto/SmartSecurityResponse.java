package cn.stylefeng.guns.modular.industrialTV.remoteClient.dto;

import lombok.Data;

/**
 * 视频智能分析系统通用响应
 */
@Data
public class SmartSecurityResponse<T> {
    /**
     * 返回数据
     */
    private T data;

    /**
     * 提示信息
     */
    private String message;

    /**
     * 返回码（0为成功）
     */
    private String code;

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 判断请求是否成功
     */
    public boolean isSuccess() {
        return success != null && success || "0".equals(code);
    }
}