package cn.stylefeng.guns.modular.station.dto;

import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import cn.stylefeng.roses.kernel.rule.pojo.request.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 站场列表查询参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StationListRequest extends BaseRequest {

    @ChineseDescription("站场名称")
    private String stationName;

    @ChineseDescription("所属作业区ID")
    private String belongOperationArea;

    @ChineseDescription("所属管线ID")
    private String belongPipeline;
}
