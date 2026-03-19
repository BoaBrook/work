package cn.stylefeng.guns.modular.firegas.dto;

import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import cn.stylefeng.roses.kernel.rule.pojo.request.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 火气系统传感器查询请求参数
 *
 * @author system
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FireGasSensorQueryRequest extends BaseRequest {

    /**
     * 所属站场ID
     */
    @ChineseDescription("所属站场ID")
    private String belongStationId;

    /**
     * 火气系统图片ID
     */
    @ChineseDescription("火气系统图片ID")
    private String fireGasImageId;

    /**
     * 火气系统主机设备ID
     */
    @ChineseDescription("火气系统主机设备ID")
    private String fireGasHostId;

}
