package cn.stylefeng.guns.modular.industrialTV.remoteClient.dto;

import lombok.Data;

/**
 * 认证响应
 */
@Data
public class AuthResponse {
    /**
     * Token
     */
    private dataInfo data;

    /**
     * 提示信息
     */
    private String message;

    /**
     * 返回码（0为成功）
     */
    private Integer code;

    @Data
    public static class dataInfo {
        private String token;
    }

}