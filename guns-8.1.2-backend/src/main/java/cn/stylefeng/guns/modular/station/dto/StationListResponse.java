package cn.stylefeng.guns.modular.station.dto;

import lombok.Data;

/**
 * 站场列表返回
 */
@Data
public class StationListResponse {

    /**
     * 站场ID（组织org_id）
     */
    private String stationId;

    /**
     * 站场名称（组织org_name）
     */
    private String stationName;

    /**
     * 所属作业区ID（组织父级org_id）
     */
    private String belongOperationArea;

    /**
     * 所属作业区名称（组织父级org_name）
     */
    private String belongOperationAreaName;

    /**
     * 所属管线ID
     */
    private String belongPipeline;

    /**
     * 所属管线名称
     */
    private String belongPipelineName;

    /**
     * 场站编码
     */
    private String stationCode;

    /**
     * 站场位置
     */
    private String stationLocation;

    /**
     * 备注
     */
    private String remark;
}
