package cn.stylefeng.guns.modular.industrialTV.request;

import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import cn.stylefeng.roses.kernel.rule.pojo.request.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 视频巡检任务结果执行记录查询请求参数
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class VideoInspectionTaskResultRawRequest extends BaseRequest {

    /**
     * 视频巡检任务结果ID
     */
    @ChineseDescription("视频巡检任务结果ID")
    private String inspectionResultId;

}
