package cn.stylefeng.guns.modular.nodeSystem.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 报警处置
 */
@Data
public class AlarmRawHandleDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 告警唯一标识
     * 必须与原告警信息的值一致
     */
    private String alarmId;

    /**
     * 节点编码
     * 来源：cn.stylefeng.guns.modular.nodeSystem.service.NodeSystemService.getNodeCode()
     */
    private String nodeCode;

    /**
     * 处置类型
     * 来源：cn.stylefeng.guns.modular.nodeSystem.constants.dict.DisposalTypeEnum
     */
    private String handleType;

    /**
     * 处置内容
     * 处置状态为2时必填
     */
    private String handleContent;

    /**
     * 处置人
     * 处置状态为2时必填
     */
    private String handler;

    /**
     * 处置时间（格式：yyyy-MM-dd HH:mm:ss）
     * 处置状态为2时必填
     */
    private String handleTime;

    /**
     * 处置状态：1-已响应，2-已处置
     */
    private Integer handleStatus;

}
