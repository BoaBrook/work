package cn.stylefeng.guns.database.entity;

import cn.stylefeng.roses.kernel.db.api.pojo.entity.BaseEntity;
import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;



/**
 * 阈值配置表
 *
 * @author system
 * @date 2026-02-02
 */
@TableName(value = "threshold_config", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class TThresholdConfig extends BaseEntity {

    /**
     * 设备ID（主键）
     */
    @TableId(value = "device_id")
    @ChineseDescription("设备ID")
    private String deviceId;

    /**
     * 高高报比较符（如：大于、大于等于）
     */
    @TableField(value = "high_high_operator")
    @ChineseDescription("高高报比较符")
    private String highHighOperator;

    /**
     * 高高报阈值
     */
    @TableField(value = "high_high_value")
    @ChineseDescription("高高报阈值")
    private String highHighValue;

    /**
     * 高报比较符（如：介于区间）
     */
    @TableField(value = "high_operator")
    @ChineseDescription("高报比较符")
    private String highOperator;

    /**
     * 高报比较符上限
     */
    @TableField(value = "high_operator_max")
    @ChineseDescription("高报比较符上限")
    private String highOperatorMax;

    /**
     * 高报下限
     */
    @TableField(value = "high_value_min")
    @ChineseDescription("高报下限")
    private String highValueMin;

    /**
     * 高报上限
     */
    @TableField(value = "high_value_max")
    @ChineseDescription("高报上限")
    private String highValueMax;

    /**
     * 低报比较符（如：小于等于、小于）
     */
    @TableField(value = "low_operator")
    @ChineseDescription("低报比较符")
    private String lowOperator;

    /**
     * 低报阈值
     */
    @TableField(value = "low_value")
    @ChineseDescription("低报阈值")
    private String lowValue;
}