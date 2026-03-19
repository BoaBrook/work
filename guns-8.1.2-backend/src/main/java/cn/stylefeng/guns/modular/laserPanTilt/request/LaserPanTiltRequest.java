package cn.stylefeng.guns.modular.laserPanTilt.request;

import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import cn.stylefeng.roses.kernel.rule.pojo.request.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 激光云台设备查询请求参数
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class LaserPanTiltRequest extends BaseRequest {

    /**
     * 所属作业区ID
     */
    @ChineseDescription("所属作业区ID")
    private String belongOperationArea;

    /**
     * 所属管线ID
     */
    @ChineseDescription("所属管线ID")
    private String belongPipeline;

    /**
     * 所属站场ID
     */
    @ChineseDescription("所属站场ID")
    private String belongStationId;

    /**
     * 设备名称
     */
    @ChineseDescription("设备名称")
    private String deviceName;

    /**
     * 设备编码
     */
    @ChineseDescription("设备编码")
    private String deviceCode;

}
