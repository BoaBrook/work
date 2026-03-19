package cn.stylefeng.guns.database.entity;

import cn.stylefeng.roses.kernel.db.api.pojo.entity.BaseEntity;
import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 阀室基础信息表
 *
 * @author system
 * @date 2026-01-14
 */
@TableName(value = "t_valve_chamber_base_info", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class TValveChamberBaseInfo extends BaseEntity {

    /**
     * 阀室ID
     */
    @TableId(value = "valve_chamber_id")
    @ChineseDescription("阀室ID")
    private String valveChamberId;

    /**
     * 阀室名称
     */
    @TableField(value = "valve_chamber_name")
    @ChineseDescription("阀室名称")
    private String valveChamberName;

    /**
     * 所属站场区域
     */
    @TableField(value = "belong_station_area_id")
    @ChineseDescription("所属站场区域")
    private String belongStationAreaId;

    /**
     * 阀室编码
     */
    @TableField(value = "valve_chamber_code")
    @ChineseDescription("阀室编码")
    private String valveChamberCode;

    /**
     * 位置
     */
    @TableField(value = "location")
    @ChineseDescription("位置")
    private String location;

    /**
     * 备注
     */
    @TableField(value = "remark")
    @ChineseDescription("备注")
    private String remark;

}