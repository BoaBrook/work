package cn.stylefeng.guns.modular.perimeterintrusion.dto;

import cn.stylefeng.guns.database.entity.TIndustrialTvBaseInfo;
import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import lombok.Data;

import java.util.List;

/**
 * 大屏-周界主机防区查询返回DTO
 */
@Data
public class HostZoneScreenResponse {

    /**
     * 设备ID
     */
    @ChineseDescription("设备ID")
    private String deviceId;

    /**
     * 设备名称（格式：所属管线名称-所属站-原设备名称）
     */
    @ChineseDescription("设备名称")
    private String deviceName;

    /**
     * 设备类型
     */
    @ChineseDescription("设备类型")
    private String deviceType;

    /**
     * 防区编号
     */
    @ChineseDescription("防区编号")
    private String zoneCode;

    /**
     * 区域
     */
    @ChineseDescription("区域")
    private String areaName;

    /**
     * 状态
     */
    @ChineseDescription("状态")
    private String status;

    /**
     * 防区路径
     */
    @ChineseDescription("防区路径")
    private String zonePath;

    /**
     * 防区位置信息
     */
    @ChineseDescription("防区位置信息")
    private String zoneLocations;

    private List<TIndustrialTvBaseInfo> tvList;
}
