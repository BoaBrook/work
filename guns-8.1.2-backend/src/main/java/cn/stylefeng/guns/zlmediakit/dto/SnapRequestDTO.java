package cn.stylefeng.guns.zlmediakit.dto;

import lombok.Data;

@Data
public class SnapRequestDTO {
    private String url;
    private Integer timeout_sec = 10;
    private Integer expire_sec = 30;
    private String secret;
}
