package cn.stylefeng.guns.database.entity;

import cn.stylefeng.roses.kernel.db.api.pojo.entity.BaseEntity;
import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 视频巡检任务结果
 *
 * @author system
 * @date 2026-01-14
 */
@TableName(value = "t_video_inspection_task_result_raw", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class TVideoInspectionTaskResultRaw extends BaseEntity {
    public static final Integer PRESET_INSPECT_RESULT_STATUS_SUCCESS = 0;
    public static final Integer PRESET_INSPECT_RESULT_STATUS_ERROR = 1;
    public static final Integer PRESET_INSPECT_RESULT_STATUS_OFFLINE = 2;
    /**
     * 视频巡检最近执行的结果ID(当前表唯一主键)
     */
    @TableId(value = "inspection_result_raw_id")
    @ChineseDescription("视频巡检任务结果ID")
    private String inspectionResultRawId;
    /**
     * 视频巡检ID
     */
    @TableField(value = "video_inspection_id")
    @ChineseDescription("视频巡检ID")
    private String videoInspectionId;

    /**
     * 视频巡检结果ID
     */
    @TableField(value = "inspection_result_id")
    @ChineseDescription("视频巡检任务结果ID")
    private String inspectionResultId;


    /**
     * 工业电视ID
     */
    @TableField(value = "industrial_tv_id")
    @ChineseDescription("工业电视ID")
    private String industrialTvId;

    /**
     * 预设位ID
     */
    @TableField(value = "preset_id")
    @ChineseDescription("预设位ID")
    private String presetId;

    /**
     * 预设位巡检结果
     */
    @TableField(value = "preset_inspect_result_status")
    @ChineseDescription("预设位巡检结果")
    private Integer presetInspectResultStatus; //0:正常；1：异常； 2：离线

    @TableField(value = "preset_inspect_result_pic")
    @ChineseDescription("巡检影像")
    private Long presetInspectResultPic;

    /**
     * 备注
     */
    @TableField(value = "remark")
    @ChineseDescription("备注")
    private String remark;


    /**
     * 工业电视名称
     */
    @TableField(exist = false)
    private String industrialTvName;

    /**
     * 预设位名称
     */
    @TableField(exist = false)
    private String presetName;

    /**
     * 预设位算法
     */
    @TableField(exist = false)
    private String presetAlgorithm;

    /**
     * 停留时长
     */
    @TableField(exist = false)
    private Integer stayDuration;

}