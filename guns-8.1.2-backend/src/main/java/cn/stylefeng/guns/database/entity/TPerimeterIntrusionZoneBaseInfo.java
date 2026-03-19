package cn.stylefeng.guns.database.entity;

import cn.stylefeng.roses.kernel.db.api.pojo.entity.BaseEntity;
import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 周界入侵防区基础信息表
 *
 * @author system
 * @date 2026-01-14
 */
@TableName(value = "t_perimeter_intrusion_zone_base_info", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class TPerimeterIntrusionZoneBaseInfo extends BaseEntity {

    /**
     * 防区ID
     */
    @TableId(value = "zone_id")
    @ChineseDescription("防区ID")
    private String zoneId;

    /**
     * 防区编码
     */
    @TableField(value = "zone_code")
    @ChineseDescription("防区编码")
    private String zoneCode;

    /**
     * 防区名称
     */
    @TableField(value = "zone_name")
    @ChineseDescription("防区名称")
    private String zoneName;

    /**
     * 所属站场区域
     */
    @TableField(value = "belong_station_area_id")
    @ChineseDescription("所属站场区域")
    private String belongStationAreaId;

    /**
     * 周界入侵主机设备ID
     */
    @TableField(value = "perimeter_intrusion_host_id")
    @ChineseDescription("周界入侵主机设备ID")
    private String perimeterIntrusionHostId;

    /**
     * 防区路径
     */
    @TableField(value = "zone_path")
    @ChineseDescription("防区路径")
    private String zonePath;

    /**
     * 防区位置信息描述
     */
    @TableField(value = "location_desp")
    @ChineseDescription("防区位置信息描述")
    private String locationDesp;

    /**
     * 防区开始位置
     */
    @TableField(value = "start_location")
    @ChineseDescription("防区开始位置")
    private String startLocation;

    /**
     * 防区结束位置
     */
    @TableField(value = "end_location")
    @ChineseDescription("防区结束位置")
    private String endLocation;

    /**
     * 通道号
     */
    @TableField(value = "channel_id")
    @ChineseDescription("通道号")
    private String channelId;

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
     * 防区位置信息
     */
    @TableField(value = "zone_locations")
    @ChineseDescription("防区位置信息")
    public String zoneLocations;

    @TableField(exist = false)
    @ChineseDescription("所属作业区")
    private String workAreaName;

    @TableField(exist = false)
    @ChineseDescription("所属管线")
    private String pipelineName;

    @TableField(exist = false)
    @ChineseDescription("所属站场")
    private String stationName;

    @TableField(exist = false)
    @ChineseDescription("所属站场ID")
    private String stationId;

    @TableField(exist = false)
    @ChineseDescription("布防状态")
    private String armedStatus;

    @TableField(exist = false)
    @ChineseDescription("主机设备名称")
    private String hostDeviceName;

    @TableField(exist = false)
    @ChineseDescription("摄像头名称")
    private String tvName;

    @TableField(exist = false)
    @ChineseDescription("预置位名称")
    private String presetName;
}