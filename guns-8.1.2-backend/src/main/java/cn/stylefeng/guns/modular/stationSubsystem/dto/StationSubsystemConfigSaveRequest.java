package cn.stylefeng.guns.modular.stationSubsystem.dto;

import lombok.Data;

import java.util.List;

/**
 * 站场子系统配置保存请求 DTO
 */
@Data
public class StationSubsystemConfigSaveRequest {

    /**
     * 站场ID
     */
    private String stationId;

    /**
     * 子系统类型编码列表
     */
    private List<String> subsystemTypes;
}

