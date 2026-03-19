package cn.stylefeng.guns.modular.stationSubsystem.dto;

import lombok.Data;

import java.util.List;

/**
 * 站场子系统配置返回 DTO
 */
@Data
public class StationSubsystemConfigResponse {

    /**
     * 站场ID
     */
    private String stationId;

    /**
     * 站场名称
     */
    private String stationName;

    /**
     * 子系统类型编码列表
     */
    private List<String> subsystemTypes;
}

