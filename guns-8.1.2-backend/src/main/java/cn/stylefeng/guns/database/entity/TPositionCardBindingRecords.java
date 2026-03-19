package cn.stylefeng.guns.database.entity;

import cn.stylefeng.roses.kernel.db.api.pojo.entity.BaseEntity;
import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 定位卡绑定记录表
 *
 * @author system
 * @date 2026-01-14
 */
@TableName(value = "t_position_card_binding_records", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class TPositionCardBindingRecords extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 定位卡设备ID
     */
    @TableId(value = "position_card_device_id", type = IdType.INPUT)
    @ChineseDescription("定位卡设备ID")
    private String positionCardDeviceId;

    /**
     * 定位卡号
     */
    @TableField(value = "position_card_number")
    @ChineseDescription("定位卡号")
    private String positionCardNumber;

    /**
     * 人员ID
     */
    @TableField(value = "personnel_id")
    @ChineseDescription("人员ID")
    private String personnelId;

    /**
     * 绑定状态
     */
    @TableField(value = "binding_status")
    @ChineseDescription("绑定状态")
    private String bindingStatus;

}