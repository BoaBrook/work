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
 * 标签管理
 *
 * @author system
 * @date 2026-01-14
 */
@TableName(value = "t_tag_management", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class TTagManagement extends BaseEntity {

    /**
     * 标签ID
     */
    @TableId(value = "tag_id")
    @ChineseDescription("标签ID")
    private String tagId;

    /**
     * 标签名称
     */
    @TableField(value = "tag_name")
    @ChineseDescription("标签名称")
    private String tagName;

    /**
     * 设备ID
     */
    @TableField(value = "device_id")
    @ChineseDescription("设备ID")
    private String deviceId;

    /**
     * 模型ID
     */
    @TableField(value = "model_id")
    @ChineseDescription("模型ID")
    private String modelId;

    /**
     * 子系统类型
     */
    @TableField(value = "subsystem_type")
    @ChineseDescription("子系统类型")
    private String subsystemType;

    /**
     * X
     */
    @TableField(value = "x_coordinate")
    @ChineseDescription("X")
    private BigDecimal xCoordinate;

    /**
     * Y
     */
    @TableField(value = "y_coordinate")
    @ChineseDescription("Y")
    private BigDecimal yCoordinate;

    /**
     * Z
     */
    @TableField(value = "z_coordinate")
    @ChineseDescription("Z")
    private BigDecimal zCoordinate;

    /**
     * 经度
     */
    @TableField(value = "longitude")
    @ChineseDescription("经度")
    private String longitude;

    /**
     * 纬度
     */
    @TableField(value = "latitude")
    @ChineseDescription("纬度")
    private String latitude;

    /**
     * 高度
     */
    @TableField(value = "height")
    @ChineseDescription("高度")
    private String height;

    /**
     * online:在线, offline:离线, alarm:报警
     */
    @TableField(exist = false)
    private String status;

}