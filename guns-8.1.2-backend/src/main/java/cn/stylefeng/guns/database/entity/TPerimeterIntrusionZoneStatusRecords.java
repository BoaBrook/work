package cn.stylefeng.guns.database.entity;

import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 周界入侵防区状态记录表
 *
 * @author system
 * @date 2026-01-14
 */
@TableName(value = "t_perimeter_intrusion_zone_status_records", autoResultMap = true)
@Data
public class TPerimeterIntrusionZoneStatusRecords implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 防区ID
     */
    @TableField(value = "zone_id")
    @ChineseDescription("防区ID")
    private String zoneId;

    /**
     * 布防状态
     */
    @TableField(value = "armed_status")
    @ChineseDescription("布防状态")
    private String armedStatus;

    /**
     * 修改人
     */
    @TableField(value = "modify_user")
    @ChineseDescription("修改人")
    private String modifyUser;

    /**
     * 修改时间
     */
    @TableField(value = "modify_time")
    @ChineseDescription("修改时间")
    private Date modifyTime;

}