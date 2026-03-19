package cn.stylefeng.guns.modular.industrialTV.request;

import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import cn.stylefeng.roses.kernel.rule.pojo.request.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 视频巡检任务查询请求参数
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class VideoInspectionTaskRequest extends BaseRequest {

    /**
     * 所属作业区ID
     */
    @ChineseDescription("所属作业区ID")
    private String workAreaId;

    /**
     * 所属管线ID
     */
    @ChineseDescription("所属管线ID")
    private String pipelineId;

    /**
     * 所属站场ID
     */
    @ChineseDescription("所属站场ID")
    private String stationId;

    /**
     * 巡检名称
     */
    @ChineseDescription("巡检名称")
    private String taskName;

    /**
     * 执行状态
     */
    @ChineseDescription("执行状态")
    private Integer inspectionStatus;

}
