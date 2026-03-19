package cn.stylefeng.guns.modular.industrialTV.request;

import cn.stylefeng.roses.kernel.rule.pojo.request.BaseRequest;
import lombok.Data;

@Data
public class TaskResultDetailsRequest extends BaseRequest {

    /**
     * 任务ID
     */
    private String taskId;

}
