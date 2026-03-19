package cn.stylefeng.guns.database.entity;

import cn.stylefeng.roses.kernel.db.api.pojo.entity.BaseEntity;
import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 配置算法基础信息表
 *
 * @author system
 * @date 2026-01-14
 */
@TableName(value = "t_configured_algorithm_base_info", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class TConfiguredAlgorithmBaseInfo extends BaseEntity {

    /**
     * 算法ID
     */
    @TableId(value = "algorithm_id")
    @ChineseDescription("算法ID")
    private String algorithmId;

    /**
     * 算法名称
     */
    @TableField(value = "algorithm_name")
    @ChineseDescription("算法名称")
    private String algorithmName;

}