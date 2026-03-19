package cn.stylefeng.guns.modular.perimeterintrusion.dto;

import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import lombok.Data;

/**
 * 大屏-防区详情查询请求参数
 */
@Data
public class ZoneDetailQueryRequest {

    /**
     * 防区ID
     */
    @ChineseDescription("防区ID")
    private String zoneId;
}
