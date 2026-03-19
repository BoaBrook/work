package cn.stylefeng.guns.database.entity;

import cn.stylefeng.roses.kernel.db.api.pojo.entity.BaseEntity;
import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 火气系统传感器设备基础信息表
 *
 * @author system
 * @date 2026-01-14
 */
@TableName(value = "t_fire_gas_sensor_base_info", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class TFireGasSensorBaseInfo extends BaseEntity {

    /**
     * 设备ID
     */
    @TableId(value = "device_id", type = IdType.ASSIGN_ID)
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
     * 设备序号
     */
    @TableField(value = "device_serial_number")
    @ChineseDescription("设备序号")
    private String deviceSerialNumber;

    /**
     * 设备型号
     */
    @TableField(value = "device_model")
    @ChineseDescription("设备型号")
    private String deviceModel;

    /**
     * 设备种类
     */
    @TableField(value = "device_type")
    @ChineseDescription("设备种类")
    private String deviceType;

    /**
     * 所属站场区域ID
     */
    @TableField(value = "belong_station_area_id")
    @ChineseDescription("所属站场区域ID")
    private String belongStationAreaId;

    /**
     * 所在位置
     */
    @TableField(value = "location")
    @ChineseDescription("所在位置")
    private String location;

    /**
     * 火气系统主机设备ID
     */
    @TableField(value = "fire_gas_host_id")
    @ChineseDescription("火气系统主机设备ID")
    private String fireGasHostId;

    /**
     * 火气系统图片ID
     */
    @TableField(value = "fire_gas_image_id")
    @ChineseDescription("火气系统图片ID")
    private String fireGasImageId;

    /**
     * 偏移地址
     */
    @TableField(value = "offset_address")
    @ChineseDescription("偏移地址")
    private String offsetAddress;

    /**
     * X轴
     */
    @TableField(value = "x_axis")
    @ChineseDescription("X轴")
    private BigDecimal xAxis;

    /**
     * Y轴
     */
    @TableField(value = "y_axis")
    @ChineseDescription("Y轴")
    private BigDecimal yAxis;

    /**
     * 站名称（不存库）
     */
    @TableField(exist = false)
    @ChineseDescription("站名称")
    private String stationName;

    /**
     * 区域名称（不存库）
     */
    @TableField(exist = false)
    @ChineseDescription("区域名称")
    private String areaName;

    /**
     * 主机名称（不存库）
     */
    @TableField(exist = false)
    @ChineseDescription("主机名称")
    private String hostName;

    /**
     * 采集单元ID（不存库）
     */
    @TableField(exist = false)
    @ChineseDescription("采集单元ID")
    private String acqUnitId;

    /**
     * 流媒体地址（不存库）
     */
    @TableField(exist = false)
    @ChineseDescription("流媒体地址")
    private String streamAddress;

    /**
     * 摄像头状态（不存库，来自TIndustrialTvBaseInfo.onlineStatus，0-离线 1-在线 2-占用）
     */
    @TableField(exist = false)
    @ChineseDescription("摄像头状态")
    private String cameraOnlineStatus;

}