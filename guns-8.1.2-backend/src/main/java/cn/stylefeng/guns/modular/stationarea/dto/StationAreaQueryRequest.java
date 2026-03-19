package cn.stylefeng.guns.modular.stationarea.dto;

import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import cn.stylefeng.roses.kernel.rule.pojo.request.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 站场区域查询请求参数
 *
 * @author system
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class StationAreaQueryRequest extends BaseRequest {

    /**
     * 区域名称（模糊查询）
     */
    @ChineseDescription("区域名称")
    private String areaName;

    /**
     * 站场ID（下拉所选）
     */
    @ChineseDescription("站场ID")
    private String stationId;

}

