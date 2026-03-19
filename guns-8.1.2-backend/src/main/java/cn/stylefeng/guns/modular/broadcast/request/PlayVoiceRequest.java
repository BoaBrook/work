package cn.stylefeng.guns.modular.broadcast.request;

import lombok.Data;

import java.util.List;

@Data
public class PlayVoiceRequest {

    private String stationId;

    private List<String> deviceIds;

    private String voiceId;

}
