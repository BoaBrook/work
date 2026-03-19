package cn.stylefeng.guns.modular.perimeterintrusion.dto;

import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import cn.stylefeng.roses.kernel.rule.pojo.request.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 周界入侵防区基础信息查询请求参数
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ZoneBaseInfoRequest extends BaseRequest {

    /**
     * 防区编码
     */
    @ChineseDescription("防区编码")
    private String zoneCode;

    /**
     * 防区名称（模糊查询）
     */
    @ChineseDescription("防区名称")
    private String zoneName;

    /**
     * 所属站场区域
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

    /**
     * 周界入侵主机设备ID
     */
    @ChineseDescription("周界入侵主机设备ID")
    private String hostId;
}
