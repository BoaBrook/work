package cn.stylefeng.guns.modular.pipeline.dto;

import lombok.Data;

/**
 * 站场选项类，用于下拉选择
 * 只包含站场ID和名称
 */
@Data
public class StationOption {

    /**
     * 站场ID
     */
    private String stationId;

    /**
     * 站场名称
     */
    private String stationName;

}

