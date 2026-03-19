package cn.stylefeng.guns.database.entity;

import cn.stylefeng.roses.kernel.db.api.pojo.entity.BaseEntity;
import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 站场基础信息表
 *
 * @author system
 * @date 2026-01-14
 */
@TableName(value = "t_station_base_info", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class TStationBaseInfo extends BaseEntity {

    /**
     * 站场ID
     */
    @TableId(value = "station_id")
    @ChineseDescription("站场ID")
    private String stationId;

    /**
     * 站场名称
     */
    @TableField(value = "station_name")
    @ChineseDescription("站场名称")
    private String stationName;

    /**
     * 所属作业区
     */
    @TableField(value = "belong_operation_area")
    @ChineseDescription("所属作业区")
    private String belongOperationArea;

    /**
     * 所属管线
     */
    @TableField(value = "belong_pipeline")
    @ChineseDescription("所属管线")
    private String belongPipeline;

    /**
     * 所属节点
     */
    @TableField(value = "belong_point")
    @ChineseDescription("所属节点")
    private String belongPoint;

    /**
     * 场站编码
     */
    @TableField(value = "station_code")
    @ChineseDescription("场站编码")
    private String stationCode;

    /**
     * 站场位置
     */
    @TableField(value = "station_location")
    @ChineseDescription("站场位置")
    private String stationLocation;

    /**
     * 备注
     */
    @TableField(value = "remark")
    @ChineseDescription("备注")
    private String remark;

}