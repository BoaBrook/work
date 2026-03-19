package cn.stylefeng.guns.modular.hikvision.response;

import lombok.Data;

/**
 * 录像回放响应
 */
@Data
public class PlaybackResponse {

    /**
     * 回放句柄
     */
    private Integer playbackHandle;

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 回放状态 (0-未开始, 1-播放中, 2-暂停, 3-已停止)
     */
    private Integer status;

    /**
     * 当前播放进度 (0-100)
     */
    private Integer position;

    /**
     * 当前播放时间 (秒)
     */
    private Integer currentTime;

    /**
     * 总时长 (秒)
     */
    private Integer totalTime;

    /**
     * 错误信息
     */
    private String errorMsg;

}
