package cn.stylefeng.guns.modular.alarmrecord.dto;

import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import cn.stylefeng.roses.kernel.rule.pojo.request.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 报警记录分页查询请求
 *
 * 查询条件：
 * 报警ID、报警类型、处置状态、报警开始时间、报警结束时间、关键字
 * 关键字匹配范围：报警内容、处理备注
 *
 * @author system
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AlarmRecordQueryRequest extends BaseRequest {

    /**
     * 报警ID
     */
    @ChineseDescription("报警ID")
    private String alarmId;

    /**
     * 报警类型
     */
    @ChineseDescription("报警类型")
    private String alarmType;

    /**
     * 处置状态
     */
    @ChineseDescription("处置状态")
    private String disposalStatus;

    /**
     * 报警开始时间
     */
    @ChineseDescription("报警开始时间")
    private Date alarmStartTime;

    /**
     * 报警结束时间
     */
    @ChineseDescription("报警结束时间")
    private Date alarmEndTime;

    /**
     * 关键字（报警内容、处理备注）
     */
    @ChineseDescription("关键字（报警内容、处理备注）")
    private String keyword;
}

