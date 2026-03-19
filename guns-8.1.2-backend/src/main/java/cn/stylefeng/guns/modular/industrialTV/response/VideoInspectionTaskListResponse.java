package cn.stylefeng.guns.modular.industrialTV.response;

import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import lombok.Data;

import java.util.Date;

/**
 * 视频巡检任务列表响应
 */
@Data
public class VideoInspectionTaskListResponse {

    /**
     * 视频巡检ID
     */
    @ChineseDescription("视频巡检ID")
    private String videoInspectionId;

    /**
     * 站场ID
     */
    @ChineseDescription("站场ID")
    private String stationId;

    /**
     * 视频巡检名称
     */
    @ChineseDescription("视频巡检名称")
    private String videoInspectionName;

    /**
     * 巡检周期
     */
    @ChineseDescription("巡检周期")
    private String inspectionCycle;

    /**
     * 巡检周期名称
     */
    @ChineseDescription("巡检周期名称")
    private String inspectionCycleName;

    /**
     * 自定义巡检周期开始时间
     */
    @ChineseDescription("自定义巡检周期开始时间")
    private Date inspectionCustomStartTime;

    /**
     * 自定义巡检周期结束时间
     */
    @ChineseDescription("自定义巡检周期结束时间")
    private Date inspectionCustomEndTime;

    /**
     * 初次巡检时间
     */
    @ChineseDescription("初次巡检时间")
    private Date initialInspectionTime;

    /**
     * 巡检间隔
     */
    @ChineseDescription("巡检间隔")
    private Integer inspectionInterval;

    /**
     * 间隔单位
     */
    @ChineseDescription("间隔单位")
    private String intervalUnit;

    /**
     * 间隔单位名称
     */
    @ChineseDescription("间隔单位名称")
    private String intervalUnitName;

    /**
     * 备注
     */
    @ChineseDescription("备注")
    private String remark;

    /**
     * 任务状态（启用/停用）
     */
    @ChineseDescription("任务状态")
    private Integer taskStatus;

    /**
     * 任务状态名称
     */
    @ChineseDescription("任务状态名称")
    private String taskStatusName;

    /**
     * 最近执行完成时间
     */
    @ChineseDescription("最近执行完成时间")
    private Date lastCompletionTime;

    /**
     * 执行状态（最近一条执行记录的状态）
     */
    @ChineseDescription("执行状态")
    private Integer inspectionStatus;

    /**
     * 执行状态名称
     */
    @ChineseDescription("执行状态名称")
    private String inspectionStatusName;

    /**
     * 创建人
     */
    @ChineseDescription("创建人")
    private String createUserName;

}