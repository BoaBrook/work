package cn.stylefeng.guns.modular.broadcast.request;

import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import cn.stylefeng.roses.kernel.rule.pojo.request.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 语音播报素材查询请求参数
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class VoiceMaterialRequest extends BaseRequest {

    /**
     * 语音名称
     */
    @ChineseDescription("语音名称")
    private String voiceName;

    /**
     * 音频类型
     */
    @ChineseDescription("音频类型")
    private String audioType;

    /**
     * 启用状态
     */
    @ChineseDescription("启用状态")
    private String enableStatus;

}
