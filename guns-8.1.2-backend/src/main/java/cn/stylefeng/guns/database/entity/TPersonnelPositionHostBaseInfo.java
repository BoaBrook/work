package cn.stylefeng.guns.database.entity;

import cn.stylefeng.roses.kernel.db.api.pojo.entity.BaseEntity;
import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 人员定位主机设备基础信息表
 *
 * @author system
 * @date 2026-01-14
 */
@TableName(value = "t_personnel_position_host_base_info", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class TPersonnelPositionHostBaseInfo extends BaseEntity {

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
     * 账号
     */
    @TableField(value = "account")
    @ChineseDescription("账号")
    private String account;

    /**
     * 密码
     */
    @TableField(value = "password")
    @ChineseDescription("密码")
    private String password;

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

}