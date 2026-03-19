package cn.stylefeng.guns.database.entity;

import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName(value = "t_workarea_base_info", autoResultMap = true)
@Data
public class TWorkareaBaseInfo {

    @TableId(value = "workarea_id")
    @ChineseDescription("作业区ID")
    private String workareaId;

    @TableField(value = "workarea_name")
    @ChineseDescription("作业区名称")
    private String workareaName;

    @TableField(value = "workarea_code")
    @ChineseDescription("作业区代码")
    private String workareaCode;

}
