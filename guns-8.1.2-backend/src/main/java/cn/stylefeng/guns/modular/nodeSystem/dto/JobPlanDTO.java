package cn.stylefeng.guns.modular.nodeSystem.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 作业计划
 */
@Data
public class JobPlanDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 作业ID
     * 32位UUID
     */
    private String jobId;

    /**
     * 节点编码
     * 来源：cn.stylefeng.guns.modular.nodeSystem.service.NodeSystemService.getNodeCode()
     */
    private String nodeCode;

    /**
     * 管线编码
     * 来源：cn.stylefeng.guns.modular.nodeSystem.constants.dict.PipelineCodeEnum
     */
    private String pipelineCode;

    /**
     * 作业区编码
     * 来源：cn.stylefeng.guns.modular.nodeSystem.constants.dict.WorkAreaCodeEnum
     */
    private String workAreaCode;

    /**
     * 场站编码
     * 来源：cn.stylefeng.guns.modular.nodeSystem.constants.dict.StationCodeEnum
     */
    private String stationCode;

    /**
     * 报备人
     */
    private String reporter;

    /**
     * 报备时间（格式：yyyy-MM-dd HH:mm:ss）
     */
    private String reportTime;

    /**
     * 报备内容
     */
    private String reportContent;

    /**
     * 作业开始时间（格式：yyyy-MM-dd HH:mm:ss）
     */
    private String operationStartTime;

    /**
     * 作业结束时间（格式：yyyy-MM-dd HH:mm:ss）
     */
    private String operationEndTime;

}
