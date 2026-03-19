package cn.stylefeng.guns.modular.broadcast.remoteClient.dto;

import lombok.Data;

/**
 * 任务信息
 */
@Data
public class TaskInfo {
    /**
     * 任务中终端列表
     */
    private EndpointIpList[] EndpointIpList;

    /**
     * 是否监听（0-否, 1-是）
     */
    private Integer IsMonitor;

    /**
     * 监听任务ID
     */
    private String MonitorTaskID;

    /**
     * 任务开始时间
     */
    private String TaskBeginTime;

    /**
     * 任务ID
     */
    private String TaskID;

    /**
     * 任务发起方
     */
    private String TaskIniator;

    /**
     * 任务发起方ID
     */
    private Integer TaskIniatorID;

    /**
     * 任务名
     */
    private String TaskName;

    /**
     * 当前播放名称
     */
    private String TaskShowInfo;

    /**
     * 任务状态
     */
    private String TaskStatus;

    /**
     * 任务类型值
     */
    private Integer TaskType;

    /**
     * 任务类型名称
     */
    private String TaskTypeName;

    /**
     * 任务用户名
     */
    private String TaskUserName;

    /**
     * 任务音量
     */
    private Integer TaskVolume;

    @Data
    public static class EndpointIpList {
        /**
         * 终端ID
         */
        private Integer EndPointID;

        /**
         * 终端主键
         */
        private String EndPointPrimaryKey;
    }
}