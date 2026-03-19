package cn.stylefeng.guns.modular.hikvision.response;

import lombok.Data;

import java.util.List;

/**
 * 巡航信息响应
 */
@Data
public class CruiseResponse {

    /**
     * 巡航路径编号
     */
    private Integer cruiseRoute;

    /**
     * 巡航点列表
     */
    private List<CruisePoint> cruisePoints;

    /**
     * 是否正在巡航
     */
    private Boolean isRunning;

    /**
     * 巡航点信息
     */
    @Data
    public static class CruisePoint {

        /**
         * 巡航点编号
         */
        private Integer cruisePoint;

        /**
         * 预置点编号
         */
        private Integer presetIndex;

        /**
         * 巡航速度
         */
        private Integer speed;

        /**
         * 停留时间 (秒)
         */
        private Integer dwellTime;

    }

}
