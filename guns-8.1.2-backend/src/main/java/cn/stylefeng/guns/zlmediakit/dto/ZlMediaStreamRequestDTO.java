package cn.stylefeng.guns.zlmediakit.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ZlMediaStreamRequestDTO {
    private String secret;
    private String vhost;
    private String app;
    private String stream;
    private String url;
    private Integer retry_count = -1;
    private Integer rtp_type = 0;
    private Integer timeout_sec = 5;
    private Boolean enable_hls = false;
    private Boolean enable_hls_fmp4 = false;
    private Boolean enable_mp4 = false;
    private Boolean enable_rtsp = true;
    private Boolean enable_rtmp = false;
    private Boolean enable_ts = false;
    private Boolean enable_fmp4 = false;
    private Boolean hls_demand = false;
    private Boolean rtsp_demand = false;
    private Boolean rtmp_demand = false;
    private Boolean ts_demand = false;
    private Boolean fmp4_demand = false;
    private Boolean enable_audio = false;
    private Boolean add_mute_audio = false;
    private String mp4_save_path = "";
    private Integer mp4_max_seconds = 600;
    private Boolean mp4_as_player = false;
    private Boolean auto_close = true;

    public Map<String, Object> toMap() {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(this, HashMap.class);
    }

}
