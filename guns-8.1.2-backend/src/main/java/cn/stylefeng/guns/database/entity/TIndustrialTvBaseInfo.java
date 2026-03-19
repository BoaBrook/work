package cn.stylefeng.guns.database.entity;

import cn.stylefeng.roses.kernel.db.api.pojo.entity.BaseEntity;
import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 工业电视设备基础信息表
 *
 * @author system
 * @date 2026-01-14
 */
@TableName(value = "t_industrial_tv_base_info", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class TIndustrialTvBaseInfo extends BaseEntity {

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
     * 硬盘录像机ID
     */
    @TableField(value = "nvr_id")
    @ChineseDescription("硬盘录像机ID")
    private String nvrId;

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
     * 录像保留天数
     */
    @TableField(value = "video_retention_days")
    @ChineseDescription("录像保留天数")
    private Integer videoRetentionDays;

    /**
     * 摄像头类型
     */
    @TableField(value = "camera_type")
    @ChineseDescription("摄像头类型")
    private String cameraType;

    /**
     * 摄像头IP
     */
    @TableField(value = "camera_ip")
    @ChineseDescription("摄像头IP")
    private String cameraIp;

    /**
     * 摄像头端口
     */
    @TableField(value = "camera_port")
    @ChineseDescription("摄像头端口")
    private Integer cameraPort;

    /**
     * 摄像头用户名
     */
    @TableField(value = "camera_username")
    @ChineseDescription("摄像头用户名")
    private String cameraUsername;

    /**
     * 摄像头密码
     */
    @TableField(value = "camera_password")
    @ChineseDescription("摄像头密码")
    private String cameraPassword;

    /**
     * 经度
     */
    @TableField(value = "longitude")
    @ChineseDescription("经度")
    private BigDecimal longitude;

    /**
     * 纬度
     */
    @TableField(value = "latitude")
    @ChineseDescription("纬度")
    private BigDecimal latitude;

    /**
     * 高度
     */
    @TableField(value = "height")
    @ChineseDescription("高度")
    private BigDecimal height;

    /**
     * 流媒体地址
     */
    @TableField(value = "stream_address")
    @ChineseDescription("流媒体地址")
    private String streamAddress;

    /**
     * 国标编号
     */
    @TableField(value = "gb_code")
    @ChineseDescription("国标编号")
    private String gbCode;

    /**
     * 流媒体通道
     */
    @TableField(value = "stream_channel")
    @ChineseDescription("流媒体通道")
    private String streamChannel;

    /**
     * 配置算法
     */
    @TableField(value = "configured_algorithm")
    @ChineseDescription("配置算法")
    private String configuredAlgorithm;

    /**
     * 在线状态 (0-离线, 1-在线, 2-占用)
     */
    @TableField(value = "online_status")
    @ChineseDescription("在线状态")
    private String onlineStatus;

    /**
     * 区域名称
     */
    @TableField(exist = false)
    private String areaName;

    /**
     * 标签信息
     */
    @TableField(exist = false)
    private TTagManagement tag;

}