package cn.stylefeng.guns.modular.broadcast.remoteClient.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 任务结果
 */
@Data
public class TaskResult {
    /**
     * 远程任务ID标识（由服务器建立）
     */
    @JsonProperty("RemoteID")
    private Integer RemoteID;

    /**
     * 远程任务类型（不是远程任务为空）
     */
    @JsonProperty("RemoteType")
    private String RemoteType;

    /**
     * 任务ID
     */
    @JsonProperty("TaskID")
    private String TaskID;

    /**
     * 任务当前状态
     */
    @JsonProperty("TaskStatus")
    private String TaskStatus;
}