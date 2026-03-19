package cn.stylefeng.guns.modular.perimeterintrusion.remote.dto;

import lombok.Data;

@Data
public class PerimeterIntrusionHostState {

    /**
     * 设备ID
     */
    private String deviceId;
    /**
     * 设备名称
     */
    private String deviceName;
    /**
     * 状态 0-离线 1-在线
     */
    private Integer state;

}
