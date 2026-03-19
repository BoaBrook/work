package cn.stylefeng.guns.modular.perimeterintrusion.remote.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class PerimeterIntrusionArmZoneRequest {

    /**
     * 防区ID
     */
    private List<String> ids;

    /**
     * 要修改的状态 0-布防,1-撤防
     */
    private String defenceState;

    /**
     * 撤防截止日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date disarmEndDate;

    /**
     * 原因
     */
    private String reason;
}
