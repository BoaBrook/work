package cn.stylefeng.guns.zlmediakit.dto;

import lombok.Data;

@Data
public class ZlMediaCacheDTO {
    private static final String WEB_RTC_URL = "http://%s/index/api/webrtc?app=%s&stream=%s&type=play";
    private String stream;
    private String webRtcUrl;
    public ZlMediaCacheDTO(String host, String app, String stream) {
        this.stream = stream;
        this.webRtcUrl = String.format(WEB_RTC_URL, host, app, stream);
    }
}
