package cn.stylefeng.guns.modular.stationSubsystem.service.impl;

import cn.stylefeng.guns.database.entity.TStationBaseInfo;
import cn.stylefeng.guns.database.entity.TStationSubsystemConfig;
import cn.stylefeng.guns.database.service.TStationBaseInfoService;
import cn.stylefeng.guns.database.service.TStationSubsystemConfigService;
import cn.stylefeng.guns.enums.SystemTypeEnum;
import cn.stylefeng.guns.modular.stationSubsystem.dto.StationSubsystemConfigListRequest;
import cn.stylefeng.guns.modular.stationSubsystem.dto.StationSubsystemConfigResponse;
import cn.stylefeng.guns.modular.stationSubsystem.dto.StationSubsystemConfigSaveRequest;
import cn.stylefeng.guns.modular.stationSubsystem.service.StationSubsystemConfigService;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StationSubsystemConfigServiceImpl implements StationSubsystemConfigService {

    @Resource
    private TStationBaseInfoService stationBaseInfoService;

    @Resource
    private TStationSubsystemConfigService stationSubsystemConfigService;

    @Override
    public PageResult<StationSubsystemConfigResponse> pageList(StationSubsystemConfigListRequest request) {
        if (request == null) {
            request = new StationSubsystemConfigListRequest();
        }
        int pageNo = request.getPageNo() != null ? request.getPageNo() : 1;
        int pageSize = request.getPageSize() != null ? request.getPageSize() : 10;

        LambdaQueryWrapper<TStationBaseInfo> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(request.getStationName())) {
            wrapper.like(TStationBaseInfo::getStationName, request.getStationName().trim());
        }
        Page<TStationBaseInfo> stationPage = stationBaseInfoService.page(new Page<>(pageNo, pageSize), wrapper);
        List<TStationBaseInfo> stations = stationPage.getRecords();
        if (stations == null || stations.isEmpty()) {
            PageResult<StationSubsystemConfigResponse> result = new PageResult<>();
            result.setPageNo(pageNo);
            result.setPageSize(pageSize);
            result.setTotalPage((int) stationPage.getPages());
            result.setTotalRows((int) stationPage.getTotal());
            result.setRows(Collections.emptyList());
            return result;
        }

        List<String> stationIds = stations.stream()
                .map(TStationBaseInfo::getStationId)
                .collect(Collectors.toList());
        List<TStationSubsystemConfig> configs = stationSubsystemConfigService.lambdaQuery()
                .in(TStationSubsystemConfig::getStationId, stationIds)
                .list();
        Map<String, List<String>> configMap = configs.stream()
                .collect(Collectors.groupingBy(TStationSubsystemConfig::getStationId,
                        Collectors.mapping(TStationSubsystemConfig::getSubsystemType, Collectors.toList())));

        List<StationSubsystemConfigResponse> rows = stations.stream().map(station -> {
            StationSubsystemConfigResponse dto = new StationSubsystemConfigResponse();
            dto.setStationId(station.getStationId());
            dto.setStationName(station.getStationName());
            dto.setSubsystemTypes(configMap.getOrDefault(station.getStationId(), Collections.emptyList()));
            return dto;
        }).collect(Collectors.toList());

        PageResult<StationSubsystemConfigResponse> result = new PageResult<>();
        result.setPageNo(pageNo);
        result.setPageSize(pageSize);
        result.setTotalPage((int) stationPage.getPages());
        result.setTotalRows((int) stationPage.getTotal());
        result.setRows(rows);
        return result;
    }

    @Override
    public List<String> getConfig(String stationId) {
        List<TStationSubsystemConfig> list = stationSubsystemConfigService.lambdaQuery()
                .eq(TStationSubsystemConfig::getStationId, stationId)
                .list();
        return list.stream()
                .map(TStationSubsystemConfig::getSubsystemType)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(StationSubsystemConfigSaveRequest request) {
        if (request == null || request.getStationId() == null || request.getStationId().trim().isEmpty()) {
            return false;
        }
        String stationId = request.getStationId().trim();

        // 删除旧配置
        stationSubsystemConfigService.lambdaUpdate()
                .eq(TStationSubsystemConfig::getStationId, stationId)
                .remove();

        List<String> types = request.getSubsystemTypes();
        if (types == null || types.isEmpty()) {
            return true;
        }

        // 过滤非法的子系统类型，只保留 SystemTypeEnum 中存在的 code
        Set<String> validCodes = Arrays.stream(SystemTypeEnum.values())
                .map(SystemTypeEnum::getCode)
                .collect(Collectors.toSet());

        List<TStationSubsystemConfig> toSave = types.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(code -> !code.isEmpty() && validCodes.contains(code))
                .distinct()
                .map(code -> {
                    TStationSubsystemConfig cfg = new TStationSubsystemConfig();
                    cfg.setStationId(stationId);
                    cfg.setSubsystemType(code);
                    return cfg;
                })
                .collect(Collectors.toList());

        if (!toSave.isEmpty()) {
            stationSubsystemConfigService.saveBatch(toSave);
        }
        return true;
    }
}

