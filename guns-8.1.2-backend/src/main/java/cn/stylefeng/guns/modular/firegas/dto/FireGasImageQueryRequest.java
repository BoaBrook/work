package cn.stylefeng.guns.modular.firegas.dto;

import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import cn.stylefeng.roses.kernel.rule.pojo.request.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 火气系统图片查询请求参数
 *
 * @author system
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FireGasImageQueryRequest extends BaseRequest {

    /**
     * 所属站场ID
     */
    @ChineseDescription("所属站场ID")
    private String belongStationId;

    /**
     * 位置
     */
    @ChineseDescription("位置")
    private String position;

    /**
     * 模型代码
     */
    @ChineseDescription("模型代码")
    private String modelCode;

    /**
     * 模型名称
     */
    @ChineseDescription("模型名称")
    private String modelName;

}
