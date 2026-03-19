package cn.stylefeng.guns.modular.perimeterintrusion.dto;

import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import lombok.Data;

/**
 * 大屏-周界主机详情查询请求参数
 */
@Data
public class HostDetailQueryRequest {

    /**
     * 主机ID
     */
    @ChineseDescription("主机ID")
    private String hostId;
}
