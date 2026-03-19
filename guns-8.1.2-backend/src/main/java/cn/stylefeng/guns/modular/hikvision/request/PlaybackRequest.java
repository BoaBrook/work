package cn.stylefeng.guns.modular.hikvision.request;

import lombok.Data;

import java.util.Date;

/**
 * 录像回放请求参数
 */
@Data
public class PlaybackRequest {

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 通道号 (默认为1)
     */
    private Integer channel;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 回放控制命令
     * play - 开始播放
     * pause - 暂停播放
     * resume - 恢复播放
     * stop - 停止播放
     * slow - 慢放
     * fast - 快放
     * normal - 正常速度
     * frame - 单帧播放
     */
    private String command;

    /**
     * 播放进度 (0-100)
     */
    private Integer position;

}
