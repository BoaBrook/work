package cn.stylefeng.guns.modular.industrialTV.response;

import lombok.Data;

@Data
public class AlarmStatisticsResponse {

    /**
     * 今日告警总数
     */
    private Integer totalAlarmNum;

    /**
     * 今日已处理
     */
    private Integer handledAlarmNum;

    /**
     * 今日未处理
     */
    private Integer unhandledAlarmNum;

    /**
     * 误报总数
     */
    private Integer falseAlarmNum;

    /**
     * 误报率
     */
    private Double falseAlarmRate;

}
