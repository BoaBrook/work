package cn.stylefeng.guns.modular.perimeterintrusion.dto;

import lombok.Data;

import java.util.List;

/**
 * 防区布防/撤防请求参数
 */
@Data
public class ZoneArmedRequest {

    /**
     * 设备ID集合
     */
    List<Device> devices;

    /**
     * 布防状态
     */
    private String armedStatus;

    /**
     * 原因
     */
    private String reason = "融合平台远程调用";

    @Data
    public static class Device {
        private String deviceId;
        private String deviceType;
    }
}
