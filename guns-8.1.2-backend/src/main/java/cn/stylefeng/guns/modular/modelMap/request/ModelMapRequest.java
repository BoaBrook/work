package cn.stylefeng.guns.modular.modelMap.request;

import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import cn.stylefeng.roses.kernel.rule.pojo.request.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 模型地图管理查询请求参数
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ModelMapRequest extends BaseRequest {

    /**
     * 所属站场/阀室ID
     */
    @ChineseDescription("所属站场/阀室ID")
    private String belongStationValveChamberId;

    /**
     * 模型名称
     */
    @ChineseDescription("模型名称")
    private String modelName;

}
