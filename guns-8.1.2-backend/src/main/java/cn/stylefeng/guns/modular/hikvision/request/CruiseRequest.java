package cn.stylefeng.guns.modular.hikvision.request;

import lombok.Data;

/**
 * 云台巡航控制请求参数
 */
@Data
public class CruiseRequest {

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 巡航路径编号 (1-255)
     */
    private Integer cruiseRoute;

    /**
     * 巡航点编号 (1-32)
     */
    private Integer cruisePoint;

    /**
     * 预置点编号
     */
    private Integer presetIndex;

    /**
     * 巡航速度 (1-7)
     */
    private Integer speed;

    /**
     * 停留时间 (秒)
     */
    private Integer dwellTime;

    /**
     * 控制命令
     * addPoint - 添加巡航点
     * removePoint - 删除巡航点
     * start - 开始巡航
     * stop - 停止巡航
     * get - 获取巡航参数
     */
    private String command;

}
