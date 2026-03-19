package cn.stylefeng.guns.database.entity;

import cn.stylefeng.roses.kernel.db.api.pojo.entity.BaseEntity;
import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 激光云台设备基础信息表
 *
 * @author system
 * @date 2026-01-14
 */
@TableName(value = "t_laser_pan_tilt_base_info", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class TLaserPanTiltBaseInfo extends BaseEntity {

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
     * 备注
     */
    @TableField(value = "remark")
    @ChineseDescription("备注")
    private String remark;

    /**
     * 巡检状态
     */
    @TableField(value = "inspection_status")
    @ChineseDescription("巡检状态")
    private String inspectionStatus;

    /**
     * 区域名称
     */
    @TableField(exist = false)
    private String areaName;

    /**
     * 所属作业区名称
     */
    @TableField(exist = false)
    private String belongOperationArea;

    /**
     * 所属管线名称
     */
    @TableField(exist = false)
    private String belongPipeline;

    /**
     * 所属站场名称
     */
    @TableField(exist = false)
    private String belongStationName;

    /**
     * 阈值配置
     */
    @TableField(exist = false)
    private TThresholdConfig thresholdConfig;

}