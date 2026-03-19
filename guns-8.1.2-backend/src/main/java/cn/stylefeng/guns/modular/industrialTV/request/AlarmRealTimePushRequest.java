package cn.stylefeng.guns.modular.industrialTV.request;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class AlarmRealTimePushRequest {
    // 报警来源ID，默认：SVP
    private String sourceId;
    // 告警区域编号
    private String areaId;
    // 告警区域名称，如：高后果区
    private String areaName;
    // 设备编号，如：1231
    private String devId;
    // 设备名称，如：消防泵-1
    private String devName;
    // 设备类型，取值：camera
    private String devType;
    // 事件内容
    private String eventContent;
    // 事件类型，取值见附录
    private String eventType;
    // 事件名称，取值见附录
    private String eventName;
    // 事件级别，取值：1/2/3
    private String eventLevel;
    // 事件告警图片地址
    private List<String> eventImageUrl;
    // 事件告警视频地址
    private String eventVideoUrl;
    // 事件告警时间
    private Date dateTime;
    // 报警记录id
    private String msgId;

}
