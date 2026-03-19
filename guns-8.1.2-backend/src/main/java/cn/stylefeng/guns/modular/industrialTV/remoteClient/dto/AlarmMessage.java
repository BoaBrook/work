package cn.stylefeng.guns.modular.industrialTV.remoteClient.dto;

import lombok.Data;

import java.util.List;

/**
 * 告警消息
 */
@Data
public class AlarmMessage {
    /**
     * 报警记录ID
     */
    private Integer msgId;

    /**
     * 消息code编码
     */
    private String msgType;

    /**
     * 报警类目
     */
    private String msgTypeCn;

    /**
     * 区域ID
     */
    private Integer orgId;

    /**
     * 区域名称
     */
    private String orgName;

    /**
     * 处理状态 deal 已处理 undeal 未处理
     */
    private String dealResult;

    /**
     * 处理结果
     */
    private String remark;

    /**
     * 报警内容
     */
    private String alarmType;

    /**
     * 创建时间
     */
    private Long createDate;

    /**
     * 报警时间
     */
    private Long imageTime;

    /**
     * 报警监控摄像头名称
     */
    private String channelAlias;

    /**
     * 摄像头ID
     */
    private Integer cameraId;

    /**
     * 报警图片列表
     */
    private List<String> imageUrls;

    /**
     * 报警短视频地址
     */
    private String videoUrl;

    /**
     * 文件访问地址
     */
    private String httpUrl;

    /**
     * 正常/误报
     */
    private String msgTag;

    /**
     * 事件等级：1、2、3
     */
    private String msgLevel;

    /**
     * 流媒体国标通道ID
     */
    private String streamChannelSerial;

    /**
     * 设备类型
     */
    private String devType;
}