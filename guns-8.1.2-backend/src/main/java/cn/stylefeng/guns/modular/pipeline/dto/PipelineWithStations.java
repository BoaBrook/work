package cn.stylefeng.guns.modular.pipeline.dto;

import lombok.Data;

import java.util.Date;

@Data
public class PipelineWithStations {

    /**
     * 管道ID
     */
    private String pipelineId;

    /**
     * 管道名称
     */
    private String pipelineName;

    /**
     * 管道代码
     */
    private String pipelineCode;

    /**
     * 管道颜色
     */
    private String pipelineColor;

    /**
     * 管道长度
     */
    private String pipelineLength;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建用户
     */
    private Long createUser;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 关联站场名称，多个站场使用逗号分隔
     */
    private String stationName;

}
