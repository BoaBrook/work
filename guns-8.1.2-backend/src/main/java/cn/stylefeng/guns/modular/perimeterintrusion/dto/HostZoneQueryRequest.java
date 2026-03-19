package cn.stylefeng.guns.modular.perimeterintrusion.dto;

import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import cn.stylefeng.roses.kernel.rule.pojo.request.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 大屏-周界主机防区查询请求参数
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class HostZoneQueryRequest extends BaseRequest {

    /**
     * 站点ID
     */
    @ChineseDescription("站点ID")
    private String stationId;

    /**
     * 主机ID
     */
    @ChineseDescription("主机ID")
    private String hostId;

    /**
     * 布防状态 0-布防 1-撤防
     */
    private String armedStatus;
}
