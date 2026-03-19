package cn.stylefeng.guns.modular.industrialTV.response;

import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import lombok.Data;

import java.util.Date;

/**
 * 视频巡检任务结果执行记录响应
 */
@Data
public class VideoInspectionTaskResultRawResponse {

    /**
     * 视频巡检任务结果执行记录ID
     */
    @ChineseDescription("视频巡检任务结果执行记录ID")
    private String inspectionResultRawId;

    /**
     * 工业电视ID
     */
    @ChineseDescription("工业电视ID")
    private String industrialTvId;

    /**
     * 摄像头名称
     */
    @ChineseDescription("摄像头名称")
    private String cameraName;

    /**
     * 预设位ID
     */
    @ChineseDescription("预设位ID")
    private String presetId;

    /**
     * 预设位名称
     */
    @ChineseDescription("预设位名称")
    private String presetName;

    /**
     * 预设位巡检结果状态
     */
    @ChineseDescription("预设位巡检结果状态")
    private Integer presetInspectResultStatus;

    /**
     * 预设位巡检结果状态名称
     */
    @ChineseDescription("预设位巡检结果状态名称")
    private String presetInspectResultStatusName;

    /**
     * 算法配置
     */
    @ChineseDescription("算法配置")
    private String presetAlgorithm;

    /**
     * 停留时长
     */
    @ChineseDescription("停留时长")
    private Integer stayDuration;

    /**
     * 巡检影像
     */
    @ChineseDescription("巡检影像")
    private Long presetInspectResultPic;

    /**
     * 备注
     */
    @ChineseDescription("备注")
    private String remark;

    /**
     * 创建时间
     */
    @ChineseDescription("创建时间")
    private Date createTime;

}
