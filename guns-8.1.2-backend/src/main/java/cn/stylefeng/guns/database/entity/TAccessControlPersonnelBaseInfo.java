package cn.stylefeng.guns.database.entity;

import cn.stylefeng.roses.kernel.db.api.pojo.entity.BaseEntity;
import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * 门禁人员管理基础信息表
 *
 * @author system
 * @date 2026-01-14
 */
@TableName(value = "t_access_control_personnel_base_info", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class TAccessControlPersonnelBaseInfo extends BaseEntity {

    /**
     * ID
     */
    @TableId(value = "id")
    @ChineseDescription("ID")
    private String id;

    /**
     * 人员ID
     */
    @TableField(value = "personnel_id")
    @ChineseDescription("人员ID")
    private String personnelId;

    /**
     * 姓名
     */
    @TableField(value = "name")
    @ChineseDescription("姓名")
    private String name;

    /**
     * 人员编码
     */
    @TableField(value = "personnel_code")
    @ChineseDescription("人员编码")
    private String personnelCode;

    /**
     * 人员类型
     */
    @TableField(value = "personnel_type")
    @ChineseDescription("人员类型")
    private String personnelType;

    /**
     * 所属站场
     */
    @TableField(value = "belong_station_id")
    @ChineseDescription("所属站场")
    private String belongStationId;

    /**
     * 来访单位
     */
    @TableField(value = "visiting_company")
    @ChineseDescription("来访单位")
    private String visitingCompany;

    /**
     * 门禁权限
     */
    @TableField(value = "access_permission")
    @ChineseDescription("门禁权限")
    private String accessPermission;

    /**
     * 手机
     */
    @TableField(value = "mobile_phone")
    @ChineseDescription("手机")
    private String mobilePhone;

    /**
     * 性别
     */
    @TableField(value = "gender")
    @ChineseDescription("性别")
    private String gender;

    /**
     * 有效期起点
     */
    @TableField(value = "validity_start_time")
    @ChineseDescription("有效期起点")
    private Date validityStartTime;

    /**
     * 有效期截止
     */
    @TableField(value = "validity_end_time")
    @ChineseDescription("有效期截止")
    private Date validityEndTime;

    /**
     * 人员分组
     */
    @TableField(value = "personnel_group")
    @ChineseDescription("人员分组")
    private String personnelGroup;

    /**
     * 身份证号
     */
    @TableField(value = "id_card_number")
    @ChineseDescription("身份证号")
    private String idCardNumber;

    /**
     * 设备名称
     */
    @TableField(value = "device_name")
    @ChineseDescription("设备名称")
    private String deviceName;

    /**
     * 所属单位
     */
    @TableField(value = "belong_company")
    @ChineseDescription("所属单位")
    private String belongCompany;

    /**
     * 人脸数据
     */
    @TableField(value = "face_data")
    @ChineseDescription("人脸数据")
    private String faceData;

    /**
     * 工号
     */
    @TableField(value = "job_number")
    @ChineseDescription("工号")
    private String jobNumber;

    /**
     * 所属设备
     */
    @TableField(value = "access_control_device_id")
    @ChineseDescription("所属设备")
    private String accessControlDeviceId;

    /**
     * 在站状态
     */
    @TableField(exist = false)
    private String inStationStatus;

    @TableField(exist = false)
    private String belongStationAreaId;

    @TableField(exist = false)
    private String belongPipelineId;

    @TableField(exist = false)
    private String stationName;

    @TableField(exist = false)
    private String areaName;

    @TableField(exist = false)
    private String pipelineName;

    @TableField(exist = false)
    private List<TAccessControlBaseInfo> devicesInfo;

}