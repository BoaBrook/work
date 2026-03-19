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
 * 站场子系统配置表
 */
@TableName(value = "t_station_subsystem_config", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class TStationSubsystemConfig extends BaseEntity {

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @ChineseDescription("主键")
    private Long id;

    /**
     * 站场ID
     */
    @TableField(value = "station_id")
    @ChineseDescription("站场ID")
    private String stationId;

    /**
     * 子系统类型
     */
    @TableField(value = "subsystem_type")
    @ChineseDescription("子系统类型")
    private String subsystemType;
}

