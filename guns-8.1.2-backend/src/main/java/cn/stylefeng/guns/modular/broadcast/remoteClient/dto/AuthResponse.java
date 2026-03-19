package cn.stylefeng.guns.modular.broadcast.remoteClient.dto;

import lombok.Data;

/**
 * 认证响应
 */
@Data
public class AuthResponse {
    /**
     * 返回数据
     */
    private String data;

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
}