package cn.stylefeng.guns.modular.nodeSystem.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 设备状态
 */
@Data
public class DeviceStatusDTO implements Serializable {

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
     * 设备编码
     */
    private String deviceCode;

    /**
     * 设备名称
     * 见章节四命名规范
     * 命名格式：管线名称-场站名称-[具体设备名称]
     * 示例：
     * - 工业电视：管线名称-场站名称-摄像头名称（如：港枣线-枣庄输油站-13#罐区西侧）
     * - 应急广播：管线名称-场站名称-广播名称
     * - 人员定位：管线名称-场站名称-人员定位系统名称
     * - 激光云台：管线名称-场站名称-激光云台名称
     * - 门禁系统：管线名称-场站名称-门禁名称
     * - 火气系统：管线名称-场站名称-火气系统名称
     * - 周界入侵：管线名称-场站名称-周界主机系统名称
     * - 周界防区：管线名称-场站名称-周界防区名称
     */
    private String deviceName;

    /**
     * 在线/离线状态：1-在线，2-离线
     */
    private Integer type;

    /**
     * 在线时间/离线时间（格式：yyyy-MM-dd HH:mm:ss）
     */
    private String triggerTime;

}
