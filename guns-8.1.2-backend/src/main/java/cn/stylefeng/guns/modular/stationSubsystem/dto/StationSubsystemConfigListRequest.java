package cn.stylefeng.guns.modular.stationSubsystem.dto;

import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import cn.stylefeng.roses.kernel.rule.pojo.request.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 站场子系统配置列表查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class StationSubsystemConfigListRequest extends BaseRequest {

    /**
     * 站场名称（模糊查询）
     */
    @ChineseDescription("站场名称")
    private String stationName;
}
