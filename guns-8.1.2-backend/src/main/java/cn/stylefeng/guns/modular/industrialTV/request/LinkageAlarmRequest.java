package cn.stylefeng.guns.modular.industrialTV.request;

import lombok.Data;

/**
 * 联动报警请求
 *
 * @author system
 * @date 2026-03-17
 */
@Data
public class LinkageAlarmRequest {

    /**
     * 工业电视ID
     */
    private String industrialTvId;

    /**
     * 报警类型编码
     */
    private String alarmType;

}
