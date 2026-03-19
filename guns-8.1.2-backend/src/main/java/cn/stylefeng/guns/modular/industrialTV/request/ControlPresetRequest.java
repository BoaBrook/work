package cn.stylefeng.guns.modular.industrialTV.request;

import lombok.Data;

@Data
public class ControlPresetRequest {

    private String deviceId;

    // set, goto, remove
    private String command;

    private String presetId;

}
