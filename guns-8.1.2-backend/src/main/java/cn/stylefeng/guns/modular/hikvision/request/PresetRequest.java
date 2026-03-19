package cn.stylefeng.guns.modular.hikvision.request;

import lombok.Data;

/**
 * 预置点控制请求参数
 */
@Data
public class PresetRequest {

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 预置点编号 (1-255)
     */
    private Integer presetIndex;

    /**
     * 预置点名称
     */
    private String presetName;

    /**
     * 控制命令
     * set - 设置预置点
     * goto - 转到预置点
     * remove - 删除预置点
     */
    private String command;

}
