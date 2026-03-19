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
 * 工业电视预设位表
 *
 * @author system
 * @date 2026-01-14
 */
@TableName(value = "t_industrial_tv_preset", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class TIndustrialTvPreset extends BaseEntity {

    /**
     * 预设位ID
     */
    @TableId(value = "preset_id")
    @ChineseDescription("预设位ID")
    private String presetId;

    /**
     * 预设位编号
     */
    @TableField(value = "preset_code")
    @ChineseDescription("预设位编号")
    private Integer presetCode;

    /**
     * 工业电视ID
     */
    @TableField(value = "industrial_tv_id")
    @ChineseDescription("工业电视ID")
    private String industrialTvId;

    /**
     * 点位名称
     */
    @TableField(value = "preset_name")
    @ChineseDescription("点位名称")
    private String presetName;

    /**
     * 水平角度
     */
    @TableField(value = "horizontal_angle")
    @ChineseDescription("水平角度")
    private BigDecimal horizontalAngle;

    /**
     * 垂直角度
     */
    @TableField(value = "vertical_angle")
    @ChineseDescription("垂直角度")
    private BigDecimal verticalAngle;

    /**
     * 缩放倍数
     */
    @TableField(value = "zoom_multiple")
    @ChineseDescription("缩放倍数")
    private BigDecimal zoomMultiple;

    /**
     * 坐标
     */
    @TableField(value = "coordinate")
    @ChineseDescription("坐标")
    private String coordinate;

}