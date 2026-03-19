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
 * 视频巡检任务结果
 *
 * @author system
 * @date 2026-01-14
 */
@TableName(value = "t_video_inspection_task_result", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class TVideoInspectionTaskResult extends BaseEntity {
    public final static Integer INSPECT_STATUS_PENDING = 0; //待执行
    public final static Integer INSPECT_STATUS_DOING = 1; //执行中
    public final static Integer INSPECT_STATUS_FINISHED = 2; //已完成
    public final static Integer INSPECT_STATUS_CANCELED = 3; //已取消
    public final static Integer INSPECT_STATUS_ERROR = 4; //巡检错误
    /**
     * 视频巡检结果ID（当前表唯一ID)
     */
    @TableId(value = "inspection_result_id")
    @ChineseDescription("视频巡检任务结果ID")
    private String inspectionResultId;
    /**
     * 巡检站
     */
    @TableField(value = "station_id")
    @ChineseDescription("站编号")
    private String stationId;
    /**
     * 视频巡检ID
     */
    @TableField(value = "video_inspection_id")
    @ChineseDescription("视频巡检ID")
    private String videoInspectionId;

    /**
     * 视频巡检开始时间
     */
    @TableField(value = "start_time")
    @ChineseDescription("巡检开始时间")
    private Date startTime;

    /**
     * 视频巡检结束时间
     */
    @TableField(value = "end_time")
    @ChineseDescription("巡检结束时间")
    private Date endTime;

    /**
     * 巡检状态
     */
    @TableField(value = "inspection_status")
    @ChineseDescription("巡检状态")
    private Integer inspectionStatus; //巡检状态： 1:执行中： 2：已完成

    /**
     * 视频巡检名称
     */
    @TableField(exist = false)
    private String videoInspectionName;

    /***
     * 巡检消息
     */
    @TableField(value = "inspection_message")
    @ChineseDescription("巡检消息")
    private String inspectionMessage;

    /**
     * 巡检状态查询条件
     */
    @TableField(exist = false)
    private List<Integer> inspectionStatuses;
}