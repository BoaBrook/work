package cn.stylefeng.guns.modular.perimeterintrusion.dto;

import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import cn.stylefeng.roses.kernel.rule.pojo.request.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 周界入侵主机设备基础信息查询请求参数
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class HostBaseInfoRequest extends BaseRequest {

    /**
     * 设备编码
     */
    @ChineseDescription("设备编码")
    private String deviceCode;

    /**
     * 设备名称（模糊查询）
     */
    @ChineseDescription("设备名称")
    private String deviceName;

    /**
     * 所属站场
     */
    @ChineseDescription("所属站场")
    private String stationId;

    /**
     * 所属作业区
     */
    @ChineseDescription("所属作业区")
    private String areaId;

    /**
     * 所属管线
     */
    @ChineseDescription("所属管线")
    private String lineId;
}
