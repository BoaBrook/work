package cn.stylefeng.guns.modular.industrialTVManagement.entity;

import cn.stylefeng.guns.database.entity.TIndustrialTvPreset;
import cn.stylefeng.guns.database.entity.TIndustrialTvBaseInfo;
import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 工业电视设备信息（包含站场关联信息）
 *
 * @author system
 * @date 2026-01-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class IndustrialTvWithStationInfo extends TIndustrialTvBaseInfo {

    @ChineseDescription("所属作业区ID")
    private String belongOperationArea;

    @ChineseDescription("所属管线ID")
    private String belongPipeline;

    @ChineseDescription("所属站场名称")
    private String belongStationName;

    @ChineseDescription("所属作业区名称")
    private String belongOperationAreaName;

    @ChineseDescription("所属管线名称")
    private String belongPipelineName;

    @ChineseDescription("预设位列表")
    private List<TIndustrialTvPreset> presetList;

}
