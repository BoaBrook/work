package cn.stylefeng.guns.zlmediakit.dto;

import lombok.Data;

@Data
public class ZlMediaStreamResponseDTO {
    private Integer code;
    private String msg;
    private StreamDataDTO data;

    @Data
    public static class StreamDataDTO{
        private String key;
        private Boolean flag;
    }
}
