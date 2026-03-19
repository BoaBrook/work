package cn.stylefeng.guns.modular.broadcast.remoteClient.dto;

import lombok.Data;

/**
 * 任务播放状态
 */
@Data
public class TaskPlayStatus {
    /**
     * 当前歌曲的时长（毫秒）
     */
    private Float CurrentTime;

    /**
     * 当前歌曲名称
     */
    private String MusicName;

    /**
     * 当前正在播放的歌曲序号
     */
    private Integer PlayIndex;

    /**
     * 当前状态（play-播放, pause-暂停, stop-停止）
     */
    private String PlayStatus;

    /**
     * 任务ID
     */
    private String TaskID;

    /**
     * 歌曲总时长（毫秒）
     */
    private Integer TotalTime;
}