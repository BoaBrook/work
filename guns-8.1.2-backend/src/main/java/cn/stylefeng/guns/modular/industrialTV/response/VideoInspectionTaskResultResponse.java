package cn.stylefeng.guns.modular.industrialTV.response;

import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import lombok.Data;

import java.util.Date;

/**
 * 视频巡检任务结果响应
 */
@Data
public class VideoInspectionTaskResultResponse {

    /**
     * 视频巡检任务结果ID
     */
    @ChineseDescription("视频巡检任务结果ID")
    private String inspectionResultId;

    /**
     * 执行批次号
     */
    @ChineseDescription("执行批次号")
    private String inspectionBatchNo;

    /**
     * 视频巡检ID
     */
    @ChineseDescription("视频巡检ID")
    private String videoInspectionId;

    /**
     * 视频巡检名称
     */
    @ChineseDescription("视频巡检名称")
    private String videoInspectionName;

    /**
     * 开始执行时间
     */
    @ChineseDescription("开始执行时间")
    private Date startTime;

    /**
     * 执行结束时间
     */
    @ChineseDescription("执行结束时间")
    private Date endTime;

    /**
     * 总巡检项数
     */
    @ChineseDescription("总巡检项数")
    private Integer totalInspectCount;

    /**
     * 总耗时（分钟）
     */
    @ChineseDescription("总耗时")
    private String totalDuration;

    /**
     * 执行状态
     */
    @ChineseDescription("执行状态")
    private Integer inspectionStatus;

    /**
     * 执行状态名称
     */
    @ChineseDescription("执行状态名称")
    private String inspectionStatusName;

    /**
     * 执行结果（正常/异常）
     */
    @ChineseDescription("执行结果")
    private String inspectResult;

}
