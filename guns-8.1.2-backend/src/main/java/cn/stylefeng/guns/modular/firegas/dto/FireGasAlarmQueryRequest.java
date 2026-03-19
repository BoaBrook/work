package cn.stylefeng.guns.modular.firegas.dto;

import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import cn.stylefeng.roses.kernel.rule.pojo.request.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 火气系统报警查询请求参数
 *
 * @author system
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FireGasAlarmQueryRequest extends BaseRequest {

    /**
     * 站场ID
     */
    @ChineseDescription("站场ID")
    private String stationId;

    /**
     * 系统类型（可选，默认使用火气系统类型）
     */
    @ChineseDescription("系统类型")
    private String systemType;

    /**
     * 处置状态
     */
    @ChineseDescription("处置状态")
    private String disposalStatus;

}
