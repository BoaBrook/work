package cn.stylefeng.guns.modular.alarmrecord.dto;

import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import cn.stylefeng.roses.kernel.rule.pojo.request.BaseRequest;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * 报警批量处置请求
 *
 * 用于一次性处置多条报警记录
 *
 * @author system
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AlarmRecordBatchDisposalRequest extends BaseRequest {

    /**
     * 批量处置验证组
     */
    public @interface batchDispose {
    }

    /**
     * 报警ID列表
     */
    @ChineseDescription("报警ID列表")
    @NotEmpty(message = "报警ID列表不能为空", groups = {batchDispose.class})
    private List<String> alarmIds;

    /**
     * 处理结果
     */
    @ChineseDescription("处理结果")
    @NotBlank(message = "处理结果不能为空", groups = {batchDispose.class})
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
    @NotBlank(message = "处理人不能为空", groups = {batchDispose.class})
    private String processUser;

    /**
     * 处理时间
     */
    @ChineseDescription("处理时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @NotNull(message = "处理时间不能为空", groups = {batchDispose.class})
    private Date processTime;
}

