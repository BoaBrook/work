package cn.stylefeng.guns.modular.industrialTV.request;

import lombok.Data;

import java.util.Date;

@Data
public class DeviceHistoryVideoRequest {

    private String deviceId;

    private Date startTime;

    private Date endTime;
}
