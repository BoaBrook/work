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
 * 视频巡检任务
 *
 * @author system
 * @date 2026-01-14
 */
@TableName(value = "t_video_inspection_tasks", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class TVideoInspectionTasks extends BaseEntity {
    public static final String INSPECTION_CYCLE_DAILY = "daily";
    public static final String INSPECTION_CYCLE_WORKDAY = "workday";
    public static final String INSPECTION_CYCLE_WEEKEND = "weekend";
    public static final String INSPECTION_CYCLE_CUSTOM = "custom";

    public static final String INTERVAL_UNIT_HOUR = "hour";
    public static final String INTERVAL_UNIT_DAY = "day";
    public static final String INTERVAL_UNIT_MONTH = "month";
    public static final String INTERVAL_UNIT_YEAR = "year";

    public static final Integer INSPECTION_STATUS_VALID = 0; //有效
    public static final Integer INSPECTION_STATUS_INVALID = 1; //无效

    /**
     * 视频巡检ID
     */
    @TableId(value = "video_inspection_id")
    @ChineseDescription("视频巡检ID")
    private String videoInspectionId;

    /**
     * 巡检站
     */
    @TableField(value = "station_id")
    @ChineseDescription("站编号")
    private String stationId;

    /**
     * 视频巡检名称
     */
    @TableField(value = "video_inspection_name")
    @ChineseDescription("视频巡检名称")
    private String videoInspectionName;

    /**
     * 巡检周期
     */
    @TableField(value = "inspection_cycle")
    @ChineseDescription("巡检周期")
    private String inspectionCycle;//daily, workday, weekend, custom

    /**
     * 自定义巡检的开始时间
     */
    @TableField(value = "inspection_custom_start_time")
    @ChineseDescription("自定义巡检周期开始时间")
    private Date inspectionCustomStartTime;

    /**
     * 自定义巡检的结束时间
     */
    @TableField(value = "inspection_custom_end_time")
    @ChineseDescription("自定义巡检周期结束时间")
    private Date inspectionCustomEndTime;

    /**
     * 初次巡检时间
     */
    @TableField(value = "initial_inspection_time")
    @ChineseDescription("初次巡检时间")
    private Date initialInspectionTime;

    /**
     * 巡检间隔
     */
    @TableField(value = "inspection_interval")
    @ChineseDescription("巡检间隔")
    private Integer inspectionInterval;

    /**
     * 间隔单位
     */
    @TableField(value = "interval_unit")
    @ChineseDescription("间隔单位")
    private String intervalUnit; //hour, day, month, year

    /**
     * 备注
     */
    @TableField(value = "remark")
    @ChineseDescription("备注")
    private String remark;

    @TableField(exist = false)
    private List<TVideoInspectionCameraPreset> cameraPresets;

    /**
     * 任务状态
     */
    @TableField(value = "task_status")
    @ChineseDescription("任务状态")
    private Integer taskStatus = INSPECTION_STATUS_VALID; //0： 有效； 1：无效
}