package cn.stylefeng.guns.modular.industrialTVManagement.entity;

import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import lombok.Data;

/**
 * 站场下拉选项（含站场ID、站场名称、所属作业区、所属管线）
 * @author system
 */
@Data
public class StationOptionDTO {

    /**
     * 站场ID
     */
    @ChineseDescription("站场ID")
    private String stationId;

    /**
     * 站场名称
     */
    @ChineseDescription("站场名称")
    private String stationName;

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
     * 所属作业区名称
    */
    @ChineseDescription("所属作业区名称")
    private String belongOperationAreaName;

    /** 
     * 所属管线名称 
    */
    @ChineseDescription("所属管线名称")
    private String belongPipelineName;
}
