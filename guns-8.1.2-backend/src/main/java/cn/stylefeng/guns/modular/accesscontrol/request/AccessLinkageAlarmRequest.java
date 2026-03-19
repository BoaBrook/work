package cn.stylefeng.guns.modular.accesscontrol.request;

import lombok.Data;

@Data
public class AccessLinkageAlarmRequest {

    /**
     * 工业电视ID
     */
    private String accessControlDeviceId;

    /**
     * 报警类型编码
     */
    private String alarmType;
}
