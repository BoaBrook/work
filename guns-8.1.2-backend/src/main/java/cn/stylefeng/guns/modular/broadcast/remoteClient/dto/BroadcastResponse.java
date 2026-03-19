package cn.stylefeng.guns.modular.broadcast.remoteClient.dto;

import lombok.Data;

/**
 * 广播系统通用响应
 */
@Data
public class BroadcastResponse<T> {
    /**
     * 返回数据
     */
    private T data;

    /**
     * 公司名称
     */
    private String company;

    /**
     * 设备名称
     */
    private String device_name;

    /**
     * 返回消息
     */
    private String return_message;

    /**
     * 签名
     */
    private String sign;

    /**
     * 返回码（200为成功）
     */
    private Integer result;

    /**
     * 操作码
     */
    private String actioncode;

    /**
     * Token
     */
    private String token;

    /**
     * 判断请求是否成功
     */
    public boolean isSuccess() {
        return result != null && result == 200;
    }
}