package cn.stylefeng.guns.modular.safeday.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

/**
 * 安全运行天数 实体类
 *
 * @author system
 * @date 2026-01-19
 */
@Data
@TableName("safety_operation_days_record")
public class TSafeOperationDays {

    /**
     * 记录ID，唯一标识
     */
    @TableId(value = "RECORD_ID", type = IdType.ASSIGN_UUID)
    private String recordId;

    /**
     * 关联站场基础信息表STATION_ID
     */
    private String stationId;

    /**
     * 安全运行开始日期
     */
    private Date safetyOperationStartDate;

    /**
     * 修改时间
     */
    private Date modifyTime;

    /**
     * 修改人
     */
    private String modifyUser;

    /**
     * 定义
     */
    private String definition;
}