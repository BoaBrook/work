package cn.stylefeng.guns.modular.industrialTV.request;

import lombok.Data;

@Data
public class PollingPlanControlRequest {

    private String stationId;

    // start-开始，stop-结束
    private String command;

}
