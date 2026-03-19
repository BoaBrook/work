package cn.stylefeng.guns.database.entity;

import cn.stylefeng.roses.kernel.db.api.pojo.entity.BaseEntity;
import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * 视频巡检配置
 *
 * @author system
 * @date 2026-01-14
 */
@TableName(value = "t_video_inspection_camera_preset", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class TVideoInspectionCameraPreset extends BaseEntity {

    /**
     * 视频巡检预设点位ID(当前表唯一编号）
     */
    @TableId(value = "camera_preset_id")
    @ChineseDescription("视频巡检ID")
    private String cameraPresetId;

    /**
     * 视频巡检ID
     */
    @TableField(value = "video_inspection_id")
    @ChineseDescription("视频巡检ID")
    private String videoInspectionId;

    /**
     * 工业电视ID
     */
    @TableField(value = "industrial_tv_id")
    @ChineseDescription("工业电视ID")
    private String industrialTvId;

    /**
     * 预设位ID
     */
    @TableField(value = "preset_id")
    @ChineseDescription("预设位ID")
    private String presetId;

    /**
     * 点位算法
     */
    @TableField(value = "preset_algorithm")
    @ChineseDescription("点位算法")
    private String presetAlgorithm;

    /**
     * 巡检序号
     */
    @TableField(value = "inspection_serial_number")
    @ChineseDescription("巡检序号")
    private Integer inspectionSerialNumber;

    /**
     * 停留时长
     */
    @TableField(value = "stay_duration")
    @ChineseDescription("停留时长")
    private Integer stayDuration;


    @TableField(exist = false)
    private String cameraName; //摄像头名称
    @TableField(exist = false)
    private String cameraType; //摄像头类型
    @TableField(exist = false)
    private String cameraArea; //摄像头区域

}