package cn.stylefeng.guns.database.entity;

import cn.stylefeng.roses.kernel.db.api.pojo.entity.BaseEntity;
import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 周界入侵主机设备基础信息表
 *
 * @author system
 * @date 2026-01-14
 */
@TableName(value = "t_perimeter_intrusion_host_base_info", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class TPerimeterIntrusionHostBaseInfo extends BaseEntity {

    /**
     * 设备ID
     */
    @TableId(value = "device_id")
    @ChineseDescription("设备ID")
    private String deviceId;

    /**
     * 设备编码
     */
    @TableField(value = "device_code")
    @ChineseDescription("设备编码")
    private String deviceCode;

    /**
     * 设备名称
     */
    @TableField(value = "device_name")
    @ChineseDescription("设备名称")
    private String deviceName;

    /**
     * 所属站场
     */
    @TableField(value = "belong_station_id")
    @ChineseDescription("所属站场")
    private String belongStationId;

    /**
     * 所属站场区域
     */
    @TableField(value = "belong_station_area_id")
    @ChineseDescription("所属站场区域")
    private String belongStationAreaId;

    /**
     * 状态 0-离线 1-在线
     */
    @TableField(value = "status")
    @ChineseDescription("状态 0-离线 1-在线")
    private String status;

    /**
     * 品牌
     */
    @TableField(value = "brand")
    @ChineseDescription("品牌")
    private String brand;

    /**
     * 型号
     */
    @TableField(value = "model")
    @ChineseDescription("型号")
    private String model;

    /**
     * 制造厂商
     */
    @TableField(value = "manufacturer")
    @ChineseDescription("制造厂商")
    private String manufacturer;

    /**
     * IP地址
     */
    @TableField(value = "ip_address")
    @ChineseDescription("IP地址")
    private String ipAddress;

    /**
     * 端口
     */
    @TableField(value = "port")
    @ChineseDescription("端口")
    private Integer port;

    /**
     * 设备类型
     */
    @TableField(value = "device_type")
    @ChineseDescription("设备类型")
    private String deviceType;

    /**
     * 备注
     */
    @TableField(value = "remark")
    @ChineseDescription("备注")
    private String remark;

    /**
     * 区域名称
     */
    @TableField(exist = false)
    private String areaName;

    @TableField(exist = false)
    @ChineseDescription("所属作业区")
    private String workAreaName;

    @TableField(exist = false)
    @ChineseDescription("所属管线")
    private String pipelineName;

    @TableField(exist = false)
    @ChineseDescription("所属站场")
    private String stationName;
}