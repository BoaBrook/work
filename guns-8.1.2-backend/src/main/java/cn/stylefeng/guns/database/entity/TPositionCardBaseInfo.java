package cn.stylefeng.guns.database.entity;

import cn.stylefeng.roses.kernel.db.api.pojo.entity.BaseEntity;
import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 定位卡基础信息表
 *
 * @author system
 * @date 2026-01-14
 */
@TableName(value = "t_position_card_base_info", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class TPositionCardBaseInfo extends BaseEntity {

    /**
     * 设备ID
     */
    @TableId(value = "device_id")
    @ChineseDescription("设备ID")
    private String deviceId;

    /**
     * 定位卡号
     */
    @TableField(value = "position_card_number")
    @ChineseDescription("定位卡号")
    private String positionCardNumber;

    /**
     * 设备名称
     */
    @TableField(value = "device_name")
    @ChineseDescription("设备名称")
    private String deviceName;

    /**
     * 人员定位主机设备ID
     */
    @TableField(value = "personnel_position_host_id")
    @ChineseDescription("人员定位主机设备ID")
    private String personnelPositionHostId;

}