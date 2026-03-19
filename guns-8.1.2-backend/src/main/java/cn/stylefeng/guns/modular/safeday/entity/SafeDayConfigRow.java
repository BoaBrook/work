package cn.stylefeng.guns.modular.safeday.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 页面内容配置（安全运行天数）列表行 DTO
 *
 * @author system
 */
@Data
public class SafeDayConfigRow {

    private String stationId;

    private String orgName;

    private String stationName;

    private Integer valveChamberCount;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date safetyOperationStartDate;

    private String definition;
}
