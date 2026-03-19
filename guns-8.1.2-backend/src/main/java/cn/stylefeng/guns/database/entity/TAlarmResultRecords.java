package cn.stylefeng.guns.database.entity;

import cn.stylefeng.roses.kernel.db.api.pojo.entity.BaseEntity;
import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 报警结果记录表
 *
 * @author system
 * @date 2026-01-14
 */
@TableName(value = "t_alarm_result_records", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class TAlarmResultRecords extends BaseEntity {

    /**
     * 报警ID
     */
    @TableId(value = "alarm_id", type = IdType.ASSIGN_ID)
    @ChineseDescription("报警ID")
    private String alarmId;

    /**
     * 报警设备ID
     */
    @TableField(value = "alarm_device_id")
    @ChineseDescription("报警设备ID")
    private String alarmDeviceId;

    /**
     * 报警位置
     */
    @TableField(value = "alarm_location")
    @ChineseDescription("报警位置")
    private String alarmLocation;

    /**
     * 子系统类型
     */
    @TableField(value = "subsystem_type")
    @ChineseDescription("子系统类型")
    private String subsystemType;

    /**
     * 告警类型
     */
    @TableField(value = "alarm_type")
    @ChineseDescription("告警类型")
    private String alarmType;

    /**
     * 告警等级
     */
    @TableField(value = "alarm_level")
    @ChineseDescription("告警等级")
    private String alarmLevel;

    /**
     * 报警内容
     */
    @TableField(value = "alarm_content")
    @ChineseDescription("报警内容")
    private String alarmContent;

    /**
     * 报警时间
     */
    @TableField(value = "alarm_time")
    @ChineseDescription("报警时间")
    private Date alarmTime;

    /**
     * 响应时间
     */
    @TableField(value = "response_time")
    @ChineseDescription("响应时间")
    private Date responseTime;

    /**
     * 处置状态
     */
    @TableField(value = "disposal_status")
    @ChineseDescription("处置状态")
    private String disposalStatus;

    /**
     * 处理结果
     */
    @TableField(value = "process_result")
    @ChineseDescription("处理结果")
    private String processResult;

    /**
     * 处理备注
     */
    @TableField(value = "process_remark")
    @ChineseDescription("处理备注")
    private String processRemark;

    /**
     * 处理人
     */
    @TableField(value = "process_user")
    @ChineseDescription("处理人")
    private String processUser;

    /**
     * 处理时间
     */
    @TableField(value = "process_time")
    @ChineseDescription("处理时间")
    private Date processTime;

    /**
     * 恢复时间
     */
    @TableField(value = "recover_time")
    @ChineseDescription("恢复时间")
    private Date recoverTime;

    /**
     * 报警图片
     */
    @TableField(value = "alarm_image")
    @ChineseDescription("报警图片")
    private String alarmImage;

    /**
     * 所属作业区
     */
    @TableField(exist = false)
    private String operationAreaName;

    /**
     * 所属站场
     */
    @TableField(exist = false)
    private String stationName;

    /**
     * 所属管线
     */
    @TableField(exist = false)
    private String pipelineName;

    /**
     * 报警设备名称
     */
    @TableField(exist = false)
    private String alarmDeviceName;

    /**
     * 子系统类型名称
     */
    @TableField(exist = false)
    private String subsystemTypeName;

    /**
     * 判断是否是二级报警
     *
     * @return true-是二级报警，false-不是
     */
    public boolean isLevel2() {
        if (alarmLevel == null) {
            return false;
        }
        return "2".equals(alarmLevel);
    }

    /**
     * 判断是否是三级报警
     *
     * @return true-是三级报警，false-不是
     */
    public boolean isLevel3() {
        if (alarmLevel == null) {
            return false;
        }
        return "3".equals(alarmLevel);
    }

}