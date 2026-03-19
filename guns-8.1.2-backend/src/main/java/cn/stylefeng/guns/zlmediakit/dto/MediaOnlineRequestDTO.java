package cn.stylefeng.guns.zlmediakit.dto;

import lombok.Data;

@Data
public class MediaOnlineRequestDTO {

    private String secret;
    private String vhost;
    private String app;
    private String stream;
    private String scheme = "rtsp";

}
