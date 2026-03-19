package cn.stylefeng.guns.modular.tagmanagement.request;

import cn.stylefeng.roses.kernel.rule.pojo.request.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TagManagementListRequest extends BaseRequest {

    /**
     * 标签名称（模糊）
     */
    private String tagName;

    /**
     * 所属作业区（组织ID）
     */
    private String belongOperationArea;

    /**
     * 所属站场ID
     */
    private String belongStationId;

    /**
     * 所属管线ID
     */
    private String belongPipeline;

}
