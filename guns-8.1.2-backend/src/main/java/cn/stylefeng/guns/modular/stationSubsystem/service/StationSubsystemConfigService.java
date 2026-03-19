package cn.stylefeng.guns.modular.stationSubsystem.service;

import cn.stylefeng.guns.modular.stationSubsystem.dto.StationSubsystemConfigListRequest;
import cn.stylefeng.guns.modular.stationSubsystem.dto.StationSubsystemConfigResponse;
import cn.stylefeng.guns.modular.stationSubsystem.dto.StationSubsystemConfigSaveRequest;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;

import java.util.List;

/**
 * 站场子系统配置业务 Service
 */
public interface StationSubsystemConfigService {

    /**
     * 分页获取站场及其子系统配置列表
     */
    PageResult<StationSubsystemConfigResponse> pageList(StationSubsystemConfigListRequest request);

    /**
     * 获取站场当前已配置的子系统类型
     */
    List<String> getConfig(String stationId);

    /**
     * 覆盖式保存某站场的子系统配置
     */
    boolean save(StationSubsystemConfigSaveRequest request);
}

