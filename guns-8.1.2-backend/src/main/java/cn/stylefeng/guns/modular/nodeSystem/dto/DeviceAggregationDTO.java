package cn.stylefeng.guns.modular.nodeSystem.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 设备汇总
 */
@Data
public class DeviceAggregationDTO implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * 设备汇总（设备总数）
     */
    private Integer deviceSummary;

    /**
     * 设备类型
     * 来源：cn.stylefeng.guns.modular.nodeSystem.constants.dict.DeviceTypeEnum
     */
    private String deviceType;

    /**
     * 在线设备数量
     */
    private Integer onDeviceSummary;

    /**
     * 离线设备数量
     */
    private Integer offlineDeviceSummary;

}
