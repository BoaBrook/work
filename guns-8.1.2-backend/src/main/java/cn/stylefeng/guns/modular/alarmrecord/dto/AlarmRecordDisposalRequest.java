package cn.stylefeng.guns.modular.alarmrecord.dto;

import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import cn.stylefeng.roses.kernel.rule.pojo.request.BaseRequest;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 报警处置请求
 *
 * 用于单条报警处置操作
 *
 * @author system
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AlarmRecordDisposalRequest extends BaseRequest {

    /**
     * 报警ID
     */
    @ChineseDescription("报警ID")
    @NotBlank(message = "报警ID不能为空", groups = {dispose.class})
    private String alarmId;

    /**
     * 处理结果
     */
    @ChineseDescription("处理结果")
    @NotBlank(message = "处理结果不能为空", groups = {dispose.class})
    private String processResult;

    /**
     * 处理备注
     */
    @ChineseDescription("处理备注")
    private String processRemark;

    /**
     * 处理人
     */
    @ChineseDescription("处理人")
    @NotBlank(message = "处理人不能为空", groups = {dispose.class})
    private String processUser;

    /**
     * 处理时间
     */
    @ChineseDescription("处理时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @NotNull(message = "处理时间不能为空", groups = {dispose.class})
    private Date processTime;

    public @interface dispose {
    }
}

