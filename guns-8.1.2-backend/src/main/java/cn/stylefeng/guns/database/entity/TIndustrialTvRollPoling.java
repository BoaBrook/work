package cn.stylefeng.guns.database.entity;

import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 工业电视轮巡配置表
 *
 * @author system
 * @date 2026-01-20
 */
@TableName(value = "t_industrial_tv_roll_poling", autoResultMap = true)
@Data
public class TIndustrialTvRollPoling {

    /**
     * 轮巡编号
     */
    @TableId(value = "roll_poling_id")
    @ChineseDescription("轮巡编号")
    private String rollPolingId;

    /**
     * 轮巡主题
     */
    @TableField(value = "roll_poling_theme")
    @ChineseDescription("轮巡主题")
    private String rollPolingTheme;

    /**
     * 所属单位
     */
    @TableField(value = "belong_unit")
    @ChineseDescription("所属单位")
    private String belongUnit;

    /**
     * 所属单位名称
     */
    @TableField(exist = false)
    private String belongUnitName;

    /**
     * 停留时长（分）
     */
    @TableField(value = "stay_duration")
    @ChineseDescription("停留时长（分）")
    private Integer stayDuration;

    /**
     * 备注信息
     */
    @TableField(value = "remark")
    @ChineseDescription("备注信息")
    private String remark;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    @ChineseDescription("创建时间")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    @ChineseDescription("更新时间")
    private Date updateTime;

    /**
     * 关联工业电视id,多个用","分割
     */
    @TableField(value = "related_tv")
    @ChineseDescription("关联工业电视id,多个用\",\"分割")
    private String relatedTv;

    /**
     * 启用状态（Y：启用，N：停止）
     */
    @TableField(value = "enable")
    @ChineseDescription("启用状态（Y：启用，N：停止）")
    private String enable;

    /**
     * 站场id
     */
    @TableField(value = "station_id")
    @ChineseDescription("站场id")
    private String stationId;

}