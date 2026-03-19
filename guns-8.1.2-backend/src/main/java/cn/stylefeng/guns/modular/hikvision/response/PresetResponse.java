package cn.stylefeng.guns.modular.hikvision.response;

import lombok.Data;

/**
 * 预置点信息响应
 */
@Data
public class PresetResponse {

    /**
     * 预置点编号
     */
    private Integer presetIndex;

    /**
     * 预置点名称
     */
    private String presetName;

    /**
     * 是否启用
     */
    private Boolean enabled;

}
