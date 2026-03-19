package cn.stylefeng.guns.modular.perimeterintrusion.dto;

import lombok.Data;

/**
 * 周界入侵联动报警请求
 *
 * @author system
 * @date 2026-03-18
 */
@Data
public class LinkageAlarmRequest {

    /**
     * 周界入侵主机ID
     */
    private String hostId;

    /**
     * 报警类型编码
     */
    private String alarmType;

}
