package cn.stylefeng.guns.modular.pipeline.request;

import lombok.Data;

@Data
public class PipelineListRequest {

    /**
     * 页码
     */
    private Integer pageNo;

    /**
     * 每页大小
     */
    private Integer pageSize;

    /**
     * 管线名称
     */
    private String pipelineName;

    /**
     * 站场ID
     */
    private String stationId;

}
