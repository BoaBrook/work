package cn.stylefeng.guns.modular.index.response;

import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import lombok.Data;

/**
 * 报警统计响应
 */
@Data
public class AlarmStatisticsResponse {

    /**
     * 当日报警总数
     */
    @ChineseDescription("当日报警总数")
    private Long todayTotalCount;

    /**
     * 当月报警总数
     */
    @ChineseDescription("当月报警总数")
    private Long monthTotalCount;

    /**
     * 当日II级报警总数
     */
    @ChineseDescription("当日II级报警总数")
    private Long todayLevel2Count;

    /**
     * 当日III级报警总数
     */
    @ChineseDescription("当日III级报警总数")
    private Long todayLevel3Count;

    /**
     * 当月II级报警总数
     */
    @ChineseDescription("当月II级报警总数")
    private Long monthLevel2Count;

    /**
     * 当月III级报警总数
     */
    @ChineseDescription("当月III级报警总数")
    private Long monthLevel3Count;

    /**
     * 当年报警总数
     */
    @ChineseDescription("当年报警总数")
    private Long yearTotalCount;

    /**
     * 当年II级报警总数
     */
    @ChineseDescription("当年II级报警总数")
    private Long yearLevel2Count;

    /**
     * 当年III级报警总数
     */
    @ChineseDescription("当年III级报警总数")
    private Long yearLevel3Count;
}
