package cn.stylefeng.guns.database.entity;

import cn.stylefeng.roses.kernel.db.api.pojo.entity.BaseEntity;
import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 联动报警配置表
 *
 * @author system
 * @date 2026-01-14
 */
@TableName(value = "t_linkage_alarm_config", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class TLinkageAlarmConfig extends BaseEntity {

    /**
     * 联动报警ID
     */
    @TableId(value = "linkage_alarm_id", type = IdType.ASSIGN_ID)
    @ChineseDescription("联动报警ID")
    private String linkageAlarmId;

    /**
     * 名称
     */
    @TableField(value = "linkage_alarm_name")
    @ChineseDescription("名称")
    private String linkageAlarmName;

    /**
     * 所属站场ID
     */
    @TableField(value = "belong_station_id")
    @ChineseDescription("所属站场ID")
    private String belongStationId;

    /**
     * 子系统类型
     */
    @TableField(value = "subsystem_type")
    @ChineseDescription("子系统类型")
    private String subsystemType;

    /**
     * 报警类型
     */
    @TableField(value = "alarm_type")
    @ChineseDescription("报警类型")
    private String alarmType;

    /**
     * 报警等级
     */
    @TableField(value = "alarm_level")
    @ChineseDescription("报警等级")
    private String alarmLevel;

    /**
     * 状态（0-关闭，1-开启）
     */
    @TableField(value = "status")
    @ChineseDescription("状态（0-关闭，1-开启）")
    private String status;

    /**
     * 是否开启录制
     */
    @TableField(value = "is_enable_record")
    @ChineseDescription("是否开启录制")
    private Boolean isEnableRecord;

    /**
     * 录制时长
     */
    @TableField(value = "record_duration")
    @ChineseDescription("录制时长")
    private Integer recordDuration;

    /**
     * 单位（秒、分、时、天）
     */
    @TableField(value = "duration_unit")
    @ChineseDescription("单位（秒、分、时、天）")
    private String durationUnit;

    /**
     * 是否开启抓图
     */
    @TableField(value = "is_enable_snapshot")
    @ChineseDescription("是否开启抓图")
    private Boolean isEnableSnapshot;

    /**
     * 抓图张数
     */
    @TableField(value = "snapshot_count")
    @ChineseDescription("抓图张数")
    private Integer snapshotCount;

    /**
     * 是否打开门禁
     */
    @TableField(value = "is_open_access_control")
    @ChineseDescription("是否打开门禁")
    private Boolean isOpenAccessControl;

    /**
     * 是否播放音频
     */
    @TableField(value = "is_play_audio")
    @ChineseDescription("是否播放音频")
    private Boolean isPlayAudio;

    /**
     * 音频文件ID
     */
    @TableField(value = "audio_file_id")
    @ChineseDescription("音频文件ID")
    private String audioFileId;

    /**
     * 音频文件名称
     */
    @TableField(value = "audio_file_name")
    @ChineseDescription("音频文件名称")
    private String audioFileName;

    @TableField(exist = false)
    @ChineseDescription("创建人名称")
    private String createUserName;

}
