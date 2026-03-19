package cn.stylefeng.guns.modular.industrialTV.request;

import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 视频巡检任务更新请求参数
 */
@Data
public class VideoInspectionTaskUpdateRequest {

    /**
     * 视频巡检ID
     */
    @ChineseDescription("视频巡检ID")
    private String videoInspectionId;

    /**
     * 站场ID
     */
    @ChineseDescription("站场ID")
    private String stationId;

    /**
     * 视频巡检名称
     */
    @ChineseDescription("视频巡检名称")
    private String videoInspectionName;

    /**
     * 巡检周期
     */
    @ChineseDescription("巡检周期")
    private String inspectionCycle;

    /**
     * 自定义巡检周期开始时间
     */
    @ChineseDescription("自定义巡检周期开始时间")
    private Date inspectionCustomStartTime;

    /**
     * 自定义巡检周期结束时间
     */
    @ChineseDescription("自定义巡检周期结束时间")
    private Date inspectionCustomEndTime;

    /**
     * 初次巡检时间
     */
    @ChineseDescription("初次巡检时间")
    private Date initialInspectionTime;

    /**
     * 巡检间隔
     */
    @ChineseDescription("巡检间隔")
    private Integer inspectionInterval;

    /**
     * 间隔单位
     */
    @ChineseDescription("间隔单位")
    private String intervalUnit;

    /**
     * 备注
     */
    @ChineseDescription("备注")
    private String remark;

    /**
     * 任务状态
     */
    @ChineseDescription("任务状态")
    private Integer taskStatus;

    /**
     * 巡检配置列表（按工业电视分组）
     */
    @ChineseDescription("巡检配置列表")
    private List<CameraConfig> cameraConfigs;

    /**
     * 摄像头配置（按工业电视分组）
     */
    @Data
    public static class CameraConfig {
        /**
         * 工业电视ID
         */
        @ChineseDescription("工业电视ID")
        private String industrialTvId;

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

}