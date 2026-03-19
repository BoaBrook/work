package cn.stylefeng.guns.database.entity;

import cn.stylefeng.roses.kernel.db.api.pojo.entity.BaseEntity;
import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 站场区域基础信息表
 *
 * @author system
 * @date 2026-01-14
 */
@TableName(value = "t_station_area_base_info", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class TStationAreaBaseInfo extends BaseEntity {

    /**
     * 区域ID
     */
    @TableId(value = "area_id")
    @ChineseDescription("区域ID")
    private String areaId;

    /**
     * 区域名称
     */
    @TableField(value = "area_name")
    @ChineseDescription("区域名称")
    private String areaName;

    /**
     * 所属站场
     */
    @TableField(value = "belong_station_id")
    @ChineseDescription("所属站场")
    private String belongStationId;

    /**
     * 区域类型
     */
    @TableField(value = "area_type")
    @ChineseDescription("区域类型")
    private String areaType;

    /**
     * 位置
     */
    @TableField(value = "area_location")
    @ChineseDescription("位置")
    private String areaLocation;

    /**
     * 备注
     */
    @TableField(value = "remark")
    @ChineseDescription("备注")
    private String remark;

    /**
     * 区域编码
     */
    @TableField(value = "area_code")
    @ChineseDescription("区域编码")
    private String areaCode;

    /**
     * 所属站场名称
     */
    @TableField(exist = false)
    @ChineseDescription("所属站场名称")
    private String belongStationName;

}