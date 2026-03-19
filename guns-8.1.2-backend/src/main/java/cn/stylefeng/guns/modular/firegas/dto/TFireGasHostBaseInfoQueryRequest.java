package cn.stylefeng.guns.modular.firegas.dto;

import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import cn.stylefeng.roses.kernel.rule.pojo.request.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 火气系统主机设备基础信息查询请求参数
 *
 * @author system
 * @date 2026-01-14
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TFireGasHostBaseInfoQueryRequest extends BaseRequest {

    /**
     * 设备编码（模糊查询）
     */
    @ChineseDescription("设备编码")
    private String deviceCode;

    /**
     * 设备名称（模糊查询）
     */
    @ChineseDescription("设备名称")
    private String deviceName;

    /**
     * 所属站场ID
     */
    @ChineseDescription("所属站场ID")
    private String belongStationId;

    /**
     * 所属作业区ID
     */
    @ChineseDescription("所属作业区ID")
    private String belongOperationAreaId;

    /**
     * 所属管线ID
     */
    @ChineseDescription("所属管线ID")
    private String belongPipelineId;

}
