package cn.stylefeng.guns.modular.perimeterintrusion.dto;

import cn.stylefeng.guns.database.entity.TIndustrialTvBaseInfo;
import cn.stylefeng.guns.database.entity.TIndustrialTvPreset;
import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import lombok.Data;

import java.util.List;

/**
 * 大屏-防区详情查询返回DTO
 */
@Data
public class ZoneDetailScreenResponse {

    /**
     * 防区名称
     */
    @ChineseDescription("防区名称")
    private String zoneName;

    /**
     * 主机名称
     */
    @ChineseDescription("主机名称")
    private String hostName;

    /**
     * 防区状态
     */
    @ChineseDescription("防区状态")
    private String status;

    /**
     * 防区位置信息描述
     */
    @ChineseDescription("防区位置信息描述")
    private String locationDesp;

    /**
     * 通道号
     */
    @ChineseDescription("通道号")
    private String channelId;

    /**
     * 防区开始位置
     */
    @ChineseDescription("防区开始位置")
    private String startLocation;

    /**
     * 防区结束位置
     */
    @ChineseDescription("防区结束位置")
    private String endLocation;

    /**
     * 工业电视预设位列表
     */
    @ChineseDescription("工业电视预设位列表")
    private List<TIndustrialTvPreset> presetList;

    /**
     * 工业电视列表
     */
    @ChineseDescription("工业电视列表")
    private List<TIndustrialTvBaseInfo> tvList;
}
