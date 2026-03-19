package cn.stylefeng.guns.database.entity;

import cn.stylefeng.roses.kernel.db.api.pojo.entity.BaseEntity;
import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 硬盘录像机设备基础信息表
 *
 * @author system
 * @date 2026-01-14
 */
@TableName(value = "t_nvr_base_info", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class TNvrBaseInfo extends BaseEntity {

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
     * 硬盘录像机IP
     */
    @TableField(value = "nvr_ip")
    @ChineseDescription("硬盘录像机IP")
    private String nvrIp;

    /**
     * 硬盘录像机端口
     */
    @TableField(value = "nvr_port")
    @ChineseDescription("硬盘录像机端口")
    private Integer nvrPort;

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
     * rtsp地址
     */
    @TableField(value = "rtsp_url")
    @ChineseDescription("rtsp地址")
    private String rtspUrl;

}