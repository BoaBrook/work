package cn.stylefeng.guns.modular.perimeterintrusion.dto;

import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import lombok.Data;

/**
 * 大屏-周界主机详情查询返回DTO
 */
@Data
public class HostDetailScreenResponse {

    /**
     * 主机名称
     */
    @ChineseDescription("主机名称")
    private String deviceName;

    /**
     * 设备编码
     */
    @ChineseDescription("设备编码")
    private String deviceCode;

    /**
     * 设备类型
     */
    @ChineseDescription("设备类型")
    private String deviceType;

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
     * 所属站场区域
     */
    @ChineseDescription("所属站场区域")
    private String areaName;
}
