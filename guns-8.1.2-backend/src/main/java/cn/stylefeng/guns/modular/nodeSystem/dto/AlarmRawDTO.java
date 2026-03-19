package cn.stylefeng.guns.modular.nodeSystem.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 报警点位
 */
@Data
public class AlarmRawDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 告警唯一标识
     * 唯一，节点编码+告警标识
     */
    private String alarmId;

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
     */
    private String deviceName;

    /**
     * 设备点位（位置）
     * 如：大门，预留字段
     */
    private String deviceLocation;

    /**
     * 设备类型
     * 来源：cn.stylefeng.guns.modular.nodeSystem.constants.dict.DeviceTypeEnum
     */
    private String deviceType;

    /**
     * 告警级别：1-I级，2-II级，3-III级，4-IV级
     */
    private Integer alarmLevel;

    /**
     * 告警类型
     * 如：烟雾探测器报警
     */
    private String alarmType;

    /**
     * 告警内容
     */
    private String alarmContent;

    /**
     * 告警时间（格式：yyyy-MM-dd HH:mm:ss）
     */
    private String alarmTime;

    /**
     * 告警关联的摄像头信息
     * JSON数组，工业电视必传
     */
    private List<AlarmCameraPamDTO> alarmCameraPam;

    /**
     * 数组，工业电视必传
     * http-flv协议或mp4
     */
    private List<String> alarmVideoUrl;

    /**
     * 告警照片URL
     * 数组，工业电视必传
     */
    private List<String> alarmImageUrl;

    /**
     * AI识别状态
     * 1-已识别，0-未识别，工业电视传
     */
    private Integer aiRecognitionStatus;

    /**
     * AI识别内容
     * 工业电视必传
     */
    private String aiRecognitionResult;

    /**
     * 告警关联的摄像头信息
     */
    @Data
    public static class AlarmCameraPamDTO implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 摄像头编码
         * 告警关联的摄像头编码
         */
        private String deviceCameraCode;

        /**
         * 摄像头直播地
         * 址,http-flv 协议
         * 工业电视必传
         */
        private String flvUrl;

    }

}
