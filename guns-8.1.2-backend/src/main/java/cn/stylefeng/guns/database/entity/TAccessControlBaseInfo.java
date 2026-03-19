package cn.stylefeng.guns.database.entity;

import cn.stylefeng.roses.kernel.db.api.pojo.entity.BaseEntity;
import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 门禁设备基础信息表
 *
 * @author system
 * @date 2026-01-14
 */
@TableName(value = "t_access_control_base_info", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class TAccessControlBaseInfo extends BaseEntity {

    /**
     * 设备ID
     */
    @TableId(value = "device_id")
    @ChineseDescription("设备ID")
    private String deviceId;

    /**
     * 设备名称
     */
    @TableField(value = "device_name")
    @ChineseDescription("设备名称")
    private String deviceName;

    /**
     * 设备编码
     */
    @TableField(value = "device_code")
    @ChineseDescription("设备编码")
    private String deviceCode;

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
     * 在线状态:1-在线；0-离线；
     */
    @TableField(value = "state")
    @ChineseDescription("在线状态")
    private Integer state;

    /**
     * 备注
     */
    @TableField(value = "remark")
    @ChineseDescription("备注")
    private String remark;

    /**
     * 视频流id
     */
    @TableField(value = "stream_address")
    @ChineseDescription("视频流id")
    private String streamAddress;

    /**
     * 所属管线id
     */
    @TableField(value = "belong_pipeline_id")
    @ChineseDescription("所属管线id")
    private String belongPipelineId;

    /**
     * 是否为采集机 1-是 0-否
     */
    @TableField(value = "is_collection_machine")
    @ChineseDescription("是否为采集机")
    private Integer isCollectionMachine;

    /**
     * 账号
     */
    @TableField(value = "access_account")
    @ChineseDescription("账号")
    private String accessAccount;

    /**
     * 密码
     */
    @TableField(value = "access_password")
    @ChineseDescription("密码")
    private String accessPassword;

    /**
     * 是否为大门设备 1-大门出站设备 0-大门进站设备
     */
    @TableField(value = "is_big_door")
    @ChineseDescription("是否为采集机")
    private String isBigDoor;

    @TableField(exist = false)
    private String stationName;

    @TableField(exist = false)
    private String areaName;

    @TableField(exist = false)
    private String pipelineName;

}