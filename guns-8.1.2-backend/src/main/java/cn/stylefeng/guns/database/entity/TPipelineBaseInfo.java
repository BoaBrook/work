package cn.stylefeng.guns.database.entity;

import cn.stylefeng.roses.kernel.db.api.pojo.entity.BaseEntity;
import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName(value = "t_pipeline_base_info", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class TPipelineBaseInfo extends BaseEntity {

    /**
     * 管道ID
     */
    @TableId(value = "pipeline_id")
    @ChineseDescription("管道ID")
    private String pipelineId;

    /**
     * 管道名称
     */
    @TableField(value = "pipeline_name")
    @ChineseDescription("管道名称")
    private String pipelineName;

    /**
     * 管道代码
     */
    @TableField(value = "pipeline_code")
    @ChineseDescription("管道代码")
    private String pipelineCode;

    /**
     * 管道颜色
     */
    @TableField(value = "pipeline_color")
    @ChineseDescription("管道颜色")
    private String pipelineColor;

    /**
     * 管道长度
     */
    @TableField(value = "pipeline_length")
    @ChineseDescription("管道长度")
    private String pipelineLength;

    /**
     * 备注
     */
    @TableField(value = "remark")
    @ChineseDescription("备注")
    private String remark;

}
