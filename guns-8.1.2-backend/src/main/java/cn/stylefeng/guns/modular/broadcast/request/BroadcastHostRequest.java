package cn.stylefeng.guns.modular.broadcast.request;

import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import cn.stylefeng.roses.kernel.rule.pojo.request.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 应急广播主机设备请求参数
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BroadcastHostRequest extends BaseRequest {

    /**
     * 所属作业区
     */
    @ChineseDescription("所属作业区")
    private String workareaId;

    /**
     * 所属管线
     */
    @ChineseDescription("所属管线")
    private String pipelineId;

    /**
     * 所属站场
     */
    @ChineseDescription("所属站场")
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