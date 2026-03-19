package cn.stylefeng.guns.modular.perimeterintrusion.dto;

import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 新增周界入侵防区请求参数
 */
@Data
public class ZoneAddRequest {

    /**
     * 防区编码
     */
    @ChineseDescription("防区编码")
    @NotBlank(message = "防区编码不能为空")
    private String zoneCode;

    /**
     * 防区名称
     */
    @ChineseDescription("防区名称")
    @NotBlank(message = "防区名称不能为空")
    private String zoneName;

    /**
     * 所属站场区域
     */
    @ChineseDescription("所属站场区域")
    @NotBlank(message = "所属站场区域不能为空")
    private String belongStationAreaId;

    /**
     * 周界入侵主机设备ID
     */
    @ChineseDescription("周界入侵主机设备ID")
    @NotBlank(message = "周界入侵主机设备ID不能为空")
    private String perimeterIntrusionHostId;

    /**
     * 防区路径
     */
    @ChineseDescription("防区路径")
    private String zonePath;

    /**
     * 防区位置信息描述
     */
    @ChineseDescription("防区位置信息描述")
    private String locationDesp;

    /**
     * 防区开始位置
     */
    @ChineseDescription("防区开始位置")
    private String startLocation;

    /**
     * 防区结束位置
     */
    @ChineseDescription("防区结束位置")
    private String endLocation;

    /**
     * 通道号
     */
    @ChineseDescription("通道号")
    private String channelId;

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

    /**
     * 防区位置信息
     */
    @ChineseDescription("防区位置信息")
    private String zoneLocations;
}
