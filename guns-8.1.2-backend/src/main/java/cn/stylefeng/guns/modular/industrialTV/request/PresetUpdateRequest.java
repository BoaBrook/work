package cn.stylefeng.guns.modular.industrialTV.request;

import lombok.Data;

@Data
public class PresetUpdateRequest {

    private String deviceId;

    private String presetId;

    private String presetName;

    private String coordinate;

}
