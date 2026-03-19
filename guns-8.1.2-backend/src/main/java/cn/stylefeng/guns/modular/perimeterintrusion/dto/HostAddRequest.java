package cn.stylefeng.guns.modular.perimeterintrusion.dto;

import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 新增周界入侵主机请求参数
 */
@Data
public class HostAddRequest {

    /**
     * 设备编码
     */
    @ChineseDescription("设备编码")
    @NotBlank(message = "设备编码不能为空")
    private String deviceCode;

    /**
     * 设备名称
     */
    @ChineseDescription("设备名称")
    @NotBlank(message = "设备名称不能为空")
    private String deviceName;

    /**
     * 所属站场
     */
    @ChineseDescription("所属站场")
    @NotBlank(message = "所属站场不能为空")
    private String belongStationId;

    /**
     * 所属站场区域
     */
    @ChineseDescription("所属站场区域")
    @NotBlank(message = "所属站场区域不能为空")
    private String belongStationAreaId;

    /**
     * 状态 0-离线 1-在线
     */
    @ChineseDescription("状态")
    private String status;

    /**
     * 品牌
     */
    @ChineseDescription("品牌")
    private String brand;

    /**
     * 型号
     */
    @ChineseDescription("型号")
    private String model;

    /**
     * IP地址
     */
    @ChineseDescription("IP地址")
    private String ipAddress;

    /**
     * 端口
     */
    @ChineseDescription("端口")
    private Integer port;

    /**
     * 设备类型
     */
    @ChineseDescription("设备类型")
    private String deviceType;

    /**
     * 备注
     */
    @ChineseDescription("备注")
    private String remark;
}
