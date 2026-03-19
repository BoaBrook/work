package cn.stylefeng.guns.modular.hikvision.request;

import lombok.Data;

/**
 * 云台控制请求参数
 */
@Data
public class PtzControlRequest {

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 云台控制命令
     * up - 上
     * down - 下
     * left - 左
     * right - 右
     * upLeft - 左上
     * upRight - 右上
     * downLeft - 左下
     * downRight - 右下
     * zoomIn - 放大
     * zoomOut - 缩小
     * focusNear - 聚焦近
     * focusFar - 聚焦远
     * irisOpen - 光圈开
     * irisClose - 光圈关
     * stop - 停止
     */
    private String command;

    /**
     * 云台速度 (1-7)
     */
    private Integer speed;

    /**
     * 是否停止 (0-开始, 1-停止)
     */
    private Integer stop;

}
