package cn.stylefeng.guns.modular.industrialTV.response;

import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import lombok.Data;

import java.util.List;

/**
 * 视频巡检任务配置响应
 */
@Data
public class VideoInspectionTaskConfigResponse {

    /**
     * 工业电视ID
     */
    @ChineseDescription("工业电视ID")
    private String industrialTvId;

    /**
     * 摄像头名称
     */
    @ChineseDescription("摄像头名称")
    private String cameraName;

    /**
     * 摄像头类型
     */
    @ChineseDescription("摄像头类型")
    private String cameraType;

    /**
     * 区域名称
     */
    @ChineseDescription("区域名称")
    private String areaName;

    /**
     * 预设位配置列表
     */
    @ChineseDescription("预设位配置列表")
    private List<PresetConfig> presetConfigs;

    /**
     * 预设位配置
     */
    @Data
    public static class PresetConfig {
        /**
         * 视频巡检预设点位ID
         */
        @ChineseDescription("视频巡检预设点位ID")
        private String cameraPresetId;

        /**
         * 预设位ID
         */
        @ChineseDescription("预设位ID")
        private String presetId;

        /**
         * 预设位名称
         */
        @ChineseDescription("预设位名称")
        private String presetName;

        /**
         * 点位算法
         */
        @ChineseDescription("点位算法")
        private String presetAlgorithm;

        /**
         * 巡检序号
         */
        @ChineseDescription("巡检序号")
        private Integer inspectionSerialNumber;

        /**
         * 停留时长
         */
        @ChineseDescription("停留时长")
        private Integer stayDuration;
    }

}
