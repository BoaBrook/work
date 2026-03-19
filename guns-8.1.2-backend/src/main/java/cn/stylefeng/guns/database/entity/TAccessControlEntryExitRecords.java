package cn.stylefeng.guns.database.entity;

import cn.stylefeng.roses.kernel.db.api.pojo.entity.BaseEntity;
import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 门禁出入记录表
 *
 * @author system
 * @date 2026-01-14
 */
@TableName(value = "t_access_control_entry_exit_records", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class TAccessControlEntryExitRecords extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 进站时间
     */
    @TableField(value = "entry_time")
    @ChineseDescription("进站时间")
    private Date entryTime;

    /**
     * 进/出
     */
    @TableField(value = "entry_exit_type")
    @ChineseDescription("进/出")
    private String entryExitType;

    /**
     * 访客信息
     */
    @TableField(value = "visitor_info")
    @ChineseDescription("访客信息")
    private String visitorInfo;

    /**
     * 门禁设备ID
     */
    @TableField(value = "access_control_device_id")
    @ChineseDescription("门禁设备ID")
    private String accessControlDeviceId;

    /**
     * 人员ID
     */
    @TableField(value = "personnel_id")
    @ChineseDescription("人员ID")
    private String personnelId;

    /**
     * 进站方式
     */
    @TableField(value = "entry_method")
    @ChineseDescription("进站方式")
    private String entryMethod;

    /**
     * 图片地址
     */
    @TableField(value = "image_address")
    @ChineseDescription("图片地址")
    private String imageAddress;

    /**
     * 在站状态
     */
    @TableField(value = "in_station_status")
    @ChineseDescription("在站状态")
    private String inStationStatus;

    @TableField(exist = false)
    private String belongStationAreaId;

    @TableField(exist = false)
    private String belongPipelineId;

    @TableField(exist = false)
    private String belongStationId;

    @TableField(exist = false)
    private String stationName;

    @TableField(exist = false)
    private String areaName;

    @TableField(exist = false)
    private String pipelineName;

    @TableField(exist = false)
    private String name;

    @TableField(exist = false)
    private String deviceName;

    @TableField(exist = false)
    private String personnelType;

}