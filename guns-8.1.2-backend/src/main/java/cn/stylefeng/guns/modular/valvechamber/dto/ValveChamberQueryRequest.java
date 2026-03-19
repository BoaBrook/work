package cn.stylefeng.guns.modular.valvechamber.dto;

import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import cn.stylefeng.roses.kernel.rule.pojo.request.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 阀室查询请求参数
 *
 * @author system
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ValveChamberQueryRequest extends BaseRequest {

    /**
     * 阀室名称
     */
    @ChineseDescription("阀室名称")
    private String valveChamberName;

    /**
     * 所属站场ID
     */
    @ChineseDescription("所属站场ID")
    private String stationId;

    /**
     * 所属站场区域ID
     */
    @ChineseDescription("所属站场区域ID")
    private String areaId;
}
