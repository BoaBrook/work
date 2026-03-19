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
 * 报警配置表
 *
 * @author system
 * @date 2026-01-14
 */
@TableName(value = "t_alarm_config", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class TAlarmConfig extends BaseEntity {

    /**
     * 配置ID
     */
    @TableId(value = "config_id", type = IdType.ASSIGN_ID)
    @ChineseDescription("配置ID")
    private String configId;

    /**
     * 所属站场
     */
    @TableField(value = "station_id")
    @ChineseDescription("所属站场")
    private String stationId;

    /**
     * 名称
     */
    @TableField(value = "name")
    @ChineseDescription("名称")
    private String name;

    /**
     * 子系统类型
     */
    @TableField(value = "sub_system_type")
    @ChineseDescription("子系统类型")
    private String subSystemType;

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
     * 通知方式（默认1报警弹窗）
     */
    @TableField(value = "notification_method")
    @ChineseDescription("通知方式（默认1报警弹窗）")
    private String notificationMethod;

    /**
     * 推送方向（可多选：station站场侧、workArea作业区侧、province省公司侧）
     */
    @TableField(value = "push_direction")
    @ChineseDescription("推送方向（可多选：station站场侧、workArea作业区侧、province省公司侧）")
    private String pushDirection;

    /**
     * 报警间隔（单位s）
     */
    @TableField(value = "alarm_interval")
    @ChineseDescription("报警间隔（单位s）")
    private Integer alarmInterval;

    /**
     * 是否弹窗
     */
    @TableField(value = "is_popup")
    @ChineseDescription("是否弹窗")
    private String isPopup;

    /**
     * 是否报警提示音
     */
    @TableField(value = "is_alarm_sound")
    @ChineseDescription("是否报警提示音")
    private String isAlarmSound;

    /**
     * 文件名称
     */
    @TableField(value = "file_name")
    @ChineseDescription("文件名称")
    private String fileName;

    /**
     * 提示音地址
     */
    @TableField(value = "file_id")
    @ChineseDescription("文件ID")
    private String fileId;

    /**
     * 提示音播放时长（单位s）
     */
    @TableField(value = "sound_duration")
    @ChineseDescription("提示音播放时长（单位s）")
    private Integer soundDuration;

    /**
     * 备注
     */
    @TableField(value = "remark")
    @ChineseDescription("备注")
    private String remark;

}
