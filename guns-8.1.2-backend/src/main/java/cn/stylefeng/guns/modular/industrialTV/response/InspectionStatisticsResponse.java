package cn.stylefeng.guns.modular.industrialTV.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InspectionStatisticsResponse {

    /**
     * 总巡检计划
     */
    private Integer totalNum = 0;

    /**
     * 已完成的巡检计划
     */
    private Integer completedNum = 0;

    /**
     * 待执行的巡检计划
     */
    private Integer pendingNum = 0;

}
