package cn.stylefeng.guns.modular.industrialTVManagement.request;

import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import lombok.Data;

/**
 * 工业电视设备列表查询入参
 */
@Data
public class IndustrialTVListRequest {

    @ChineseDescription("页码，从1开始")
    private Integer pageNo;

    @ChineseDescription("每页条数")
    private Integer pageSize;

    @ChineseDescription("站场ID")
    private String stationId;

    @ChineseDescription("所属站场ID（兼容旧参数名belongStationId）")
    private String belongStationId;

    @ChineseDescription("所属作业区ID")
    private String belongOperationArea;

    @ChineseDescription("所属管线ID")
    private String belongPipeline;

    @ChineseDescription("设备名称（模糊）")
    private String deviceName;

    @ChineseDescription("设备编码（模糊）")
    private String deviceCode;

    @ChineseDescription("品牌（模糊）")
    private String brand;
}

