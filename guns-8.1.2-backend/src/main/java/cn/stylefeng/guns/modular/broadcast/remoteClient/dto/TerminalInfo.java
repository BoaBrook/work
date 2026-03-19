package cn.stylefeng.guns.modular.broadcast.remoteClient.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 终端信息
 */
@Data
public class TerminalInfo {

    /**
     * 报警强切通道标记
     */
    @JsonProperty("AlarmChannel")
    private Integer AlarmChannel;

    /**
     * 时间终端显示亮度
     */
    @JsonProperty("Brightness")
    private Integer Brightness;

    /**
     * 终端呼叫码
     */
    @JsonProperty("CallCode")
    private Integer CallCode;

    /**
     * 终端禁用标记
     */
    @JsonProperty("DisableFlag")
    private Integer DisableFlag;

    /**
     * EQ0-EQ5 段值
     */
    @JsonProperty("EQ0")
    private Integer EQ0;
    
    @JsonProperty("EQ1")
    private Integer EQ1;
    
    @JsonProperty("EQ2")
    private Integer EQ2;
    
    @JsonProperty("EQ3")
    private Integer EQ3;
    
    @JsonProperty("EQ4")
    private Integer EQ4;

    /**
     * 八分区标记
     */
    @JsonProperty("EightZone")
    private Integer EightZone;

    /**
     * 启用终端额外属性标记
     */
    @JsonProperty("EnableFlag")
    private Integer EnableFlag;

    /**
     * 终端ID（唯一标识）
     */
    @JsonProperty("EndpointID")
    private Integer EndpointID;

    /**
     * 终端IP
     */
    @JsonProperty("EndpointIP")
    private String EndpointIP;

    /**
     * 终端MAC
     */
    @JsonProperty("EndpointMac")
    private String EndpointMac;

    /**
     * 终端名称
     */
    @JsonProperty("EndpointName")
    private String EndpointName;

    /**
     * 终端型号
     */
    @JsonProperty("EndpointType")
    private Integer EndpointType;

    /**
     * 终端型号名称
     */
    @JsonProperty("EndpointTypeName")
    private String EndpointTypeName;

    /**
     * 终端版本
     */
    @JsonProperty("EndpointVersion")
    private String EndpointVersion;

    /**
     * 灯光模式ID
     */
    @JsonProperty("LightModeID")
    private Integer LightModeID;

    /**
     * 特殊终端功能
     */
    @JsonProperty("Position")
    private Object Position;

    /**
     * 终端电源控制开关
     */
    @JsonProperty("PowerControl")
    private Integer PowerControl;

    /**
     * 所属中继服务器ID
     */
    @JsonProperty("ProxyServerID")
    private Integer ProxyServerID;

    /**
     * 所属中继服务器IP
     */
    @JsonProperty("ProxyServerIP")
    private String ProxyServerIP;

    /**
     * 所属中继服务器名称
     */
    @JsonProperty("ProxyServerName")
    private String ProxyServerName;

    /**
     * 短路输出标记
     */
    @JsonProperty("ShortOutput")
    private Integer ShortOutput;

    /**
     * 终端工作状态（0-离线, 1-在线, 2-占用）
     */
    @JsonProperty("Status")
    private Integer Status;

    /**
     * 终端在线与否
     */
    @JsonProperty("StatusDsp")
    private String StatusDsp;

    /**
     * 是否支持LED功能
     */
    @JsonProperty("SurrportLED")
    private Integer SurrportLED;

    /**
     * 当前任务标记（空则为当前无任务）
     */
    @JsonProperty("TaskID")
    private String TaskID;

    /**
     * 任务名称
     */
    @JsonProperty("TaskName")
    private String TaskName;

    /**
     * 任务类型
     */
    @JsonProperty("TaskType")
    private Integer TaskType;

    /**
     * 任务类型名称
     */
    @JsonProperty("TaskTypeName")
    private String TaskTypeName;

    /**
     * 时间终端模式
     */
    @JsonProperty("TimeMode")
    private Integer TimeMode;

    /**
     * 音量
     */
    @JsonProperty("Volume")
    private Integer Volume;
}
