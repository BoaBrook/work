package cn.stylefeng.guns.database.entity;

import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 巡检任务执行记录详情
 *
 * @author system
 * @date 2026-01-14
 */
@TableName(value = "t_inspection_task_execution_detail_records", autoResultMap = true)
@Data
public class TInspectionTaskExecutionDetailRecords implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 执行批次ID
     */
    @TableId(value = "execution_batch_id")
    @ChineseDescription("执行批次ID")
    private String executionBatchId;

    /**
     * 停留时长
     */
    @TableField(value = "stay_duration")
    @ChineseDescription("停留时长")
    private Integer stayDuration;

    /**
     * 采集影像地址
     */
    @TableField(value = "collected_image_address")
    @ChineseDescription("采集影像地址")
    private String collectedImageAddress;

    /**
     * 结果
     */
    @TableField(value = "result")
    @ChineseDescription("结果")
    private String result;

    /**
     * 记录时间
     */
    @TableField(value = "record_time")
    @ChineseDescription("记录时间")
    private Date recordTime;

}