package cn.stylefeng.guns.database.entity;

import cn.stylefeng.roses.kernel.db.api.pojo.entity.BaseEntity;
import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 巡检任务执行记录
 *
 * @author system
 * @date 2026-01-14
 */
@TableName(value = "t_inspection_task_execution_records", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class TInspectionTaskExecutionRecords extends BaseEntity {

    /**
     * 执行批次ID
     */
    @TableId(value = "execution_batch_id")
    @ChineseDescription("执行批次ID")
    private String executionBatchId;

    /**
     * 视频巡检ID
     */
    @TableField(value = "video_inspection_id")
    @ChineseDescription("视频巡检ID")
    private String videoInspectionId;

    /**
     * 执行批次号
     */
    @TableField(value = "execution_batch_number")
    @ChineseDescription("执行批次号")
    private String executionBatchNumber;

    /**
     * 执行开始时间
     */
    @TableField(value = "execution_start_time")
    @ChineseDescription("执行开始时间")
    private Date executionStartTime;

    /**
     * 总巡检项数
     */
    @TableField(value = "total_inspection_items")
    @ChineseDescription("总巡检项数")
    private Integer totalInspectionItems;

    /**
     * 总耗时
     */
    @TableField(value = "total_consumption_time")
    @ChineseDescription("总耗时")
    private Integer totalConsumptionTime;

    /**
     * 执行状态
     */
    @TableField(value = "execution_status")
    @ChineseDescription("执行状态")
    private String executionStatus;

    /**
     * 执行结果
     */
    @TableField(value = "execution_result")
    @ChineseDescription("执行结果")
    private String executionResult;

}