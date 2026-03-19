package cn.stylefeng.guns.modular.workarea.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

import java.util.Date;

/**
 * 作业区配置实体类
 *
 * @author system
 * @date 2026-01-20
 */
@Data
@TableName("t_workarea_config")
public class TWorkareaConfig {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 作业区URL
     */
    private String workareaUrl;



    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}