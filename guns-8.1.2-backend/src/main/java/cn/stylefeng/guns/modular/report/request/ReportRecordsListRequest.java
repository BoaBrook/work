package cn.stylefeng.guns.modular.report.request;

import cn.stylefeng.roses.kernel.rule.pojo.request.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class ReportRecordsListRequest extends BaseRequest {

    /**
     * 状态 0-待上报、1-审核中、2-已通过、3-已驳回
     */
    private Integer status;

    private String reportContent;

    private Date startTime;
    private Date endTime;

}
