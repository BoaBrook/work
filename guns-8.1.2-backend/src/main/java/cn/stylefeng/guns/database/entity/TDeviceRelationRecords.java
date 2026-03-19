package cn.stylefeng.guns.database.entity;

import cn.stylefeng.roses.kernel.db.api.pojo.entity.BaseEntity;
import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 设备关联关系记录表
 *
 * @author system
 * @date 2026-01-14
 */
@TableName(value = "t_device_relation_records", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class TDeviceRelationRecords extends BaseEntity {

    /**
     * 关联ID
     */
    @TableId(value = "relation_id")
    @ChineseDescription("关联ID")
    private String relationId;

    /**
     * 关联设备ID
     */
    @TableField(value = "related_device_id")
    @ChineseDescription("关联设备ID")
    private String relatedDeviceId;

    /**
     * 预设位ID
     */
    @TableField(value = "preset_id")
    @ChineseDescription("预设位ID")
    private String presetId;

    /**
     * 门禁设备ID
     */
    @TableField(value = "access_control_device_id")
    @ChineseDescription("门禁设备ID")
    private String accessControlDeviceId;

    /**
     * 应急广播设备ID
     */
    @TableField(value = "emergency_broadcast_id")
    @ChineseDescription("应急广播设备ID")
    private String emergencyBroadcastId;

    /**
     * 子系统类型
     */
    @TableField(value = "subsystem_type")
    @ChineseDescription("子系统类型")
    private String subsystemType;

}