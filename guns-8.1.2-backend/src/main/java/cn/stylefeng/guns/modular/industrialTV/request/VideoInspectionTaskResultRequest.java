package cn.stylefeng.guns.modular.industrialTV.request;

import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import cn.stylefeng.roses.kernel.rule.pojo.request.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 视频巡检任务结果查询请求参数
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class VideoInspectionTaskResultRequest extends BaseRequest {

    /**
     * 视频巡检ID
     */
    @ChineseDescription("视频巡检ID")
    private String videoInspectionId;

    /**
     * 执行开始时间-开始
     */
    @ChineseDescription("执行开始时间 - 开始")
    private Date executeStartTime;

    /**
     * 执行开始时间 - 结束
     */
    @ChineseDescription("执行开始时间 - 结束")
    private Date executeEndTime;

    /**
     * 执行结束时间 - 开始
     */
    @ChineseDescription("执行结束时间 - 开始")
    private Date finishStartTime;

    /**
     * 执行结束时间 - 结束
     */
    @ChineseDescription("执行结束时间 - 结束")
    private Date finishEndTime;

    /**
     * 执行状态
     */
    @ChineseDescription("执行状态")
    private Integer inspectionStatus;

}
