package cn.stylefeng.guns.modular.industrialTVManagement.service.impl;

import cn.stylefeng.guns.core.utils.PageToPageResultUtils;
import cn.stylefeng.guns.database.entity.*;
import cn.stylefeng.guns.database.service.*;
import cn.stylefeng.guns.modular.industrialTVManagement.entity.AlgorithmOptionDTO;
import cn.stylefeng.guns.modular.industrialTVManagement.entity.IndustrialTvWithStationInfo;
import cn.stylefeng.guns.modular.industrialTVManagement.entity.StationAreaOptionDTO;
import cn.stylefeng.guns.modular.industrialTVManagement.entity.StationOptionDTO;
import cn.stylefeng.guns.modular.industrialTVManagement.request.IndustrialTVListRequest;
import cn.stylefeng.guns.modular.industrialTVManagement.service.IndustrialTVManagementService;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;
import cn.stylefeng.roses.kernel.sys.modular.org.entity.HrOrganization;
import cn.stylefeng.roses.kernel.sys.modular.org.service.HrOrganizationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * 工业电视设备管理Service实现
 *
 * @author system
 * @date 2026-01-27
 */
@Service
public class IndustrialTVManagementServiceImpl implements IndustrialTVManagementService {

    /**
     * 站场级别的锁映射，用于校验设备编码唯一性时的并发控制
     * 按站场ID加锁，不同站场的校验可并行执行，同一站场的校验串行执行
     */
    private static final ConcurrentHashMap<String, ReentrantLock> STATION_LOCKS = new ConcurrentHashMap<>();

    private static final String MASK_PASSWORD = "******";

    @Resource
    private TIndustrialTvBaseInfoService industrialTvBaseInfoService;

    @Resource
    private TStationBaseInfoService stationBaseInfoService;

    @Resource
    private TPipelineBaseInfoService pipelineBaseInfoService;

    @Resource
    private HrOrganizationService sysHrOrganizationService;

    @Resource
    private TConfiguredAlgorithmBaseInfoService configuredAlgorithmBaseInfoService;

    @Resource
    private TStationAreaBaseInfoService stationAreaBaseInfoService;

    @Resource
    private TIndustrialTvPresetService industrialTvPresetService;

    @Override
    public PageResult<IndustrialTvWithStationInfo> list(IndustrialTVListRequest request) {
        int pageNo = request.getPageNo() != null ? request.getPageNo() : 1;
        int pageSize = request.getPageSize() != null ? request.getPageSize() : 10;
        LambdaQueryWrapper<TIndustrialTvBaseInfo> queryWrapper = new LambdaQueryWrapper<>();
        buildQueryCondition(queryWrapper, request);
        Page<TIndustrialTvBaseInfo> pageResult = industrialTvBaseInfoService.page(new Page<>(pageNo, pageSize), queryWrapper);
        Page<IndustrialTvWithStationInfo> resultPage = new Page<>(pageNo, pageSize);
        resultPage.setTotal(pageResult.getTotal());
        resultPage.setRecords(pageResult.getRecords().stream().map(this::convertToWithStationInfo).collect(Collectors.toList()));
        return PageToPageResultUtils.pageToPageResult(resultPage);
    }

 
    private IndustrialTvWithStationInfo convertToWithStationInfo(TIndustrialTvBaseInfo industrialTv) {
        if (industrialTv == null) {
            return null;
        }
        IndustrialTvWithStationInfo result = new IndustrialTvWithStationInfo();
        org.springframework.beans.BeanUtils.copyProperties(industrialTv, result);

        if (StringUtils.isNotBlank(result.getCameraPassword())) {
            result.setCameraPassword(MASK_PASSWORD);
        }

        if (industrialTv.getBelongStationId() != null) {
            TStationBaseInfo station = stationBaseInfoService.getById(industrialTv.getBelongStationId());
            if (station != null) {
                result.setBelongOperationArea(station.getBelongOperationArea());
                result.setBelongPipeline(station.getBelongPipeline());
                result.setBelongStationName(station.getStationName());
                result.setBelongOperationAreaName(resolveWorkareaName(station.getBelongOperationArea()));
                result.setBelongPipelineName(resolvePipelineName(station.getBelongPipeline()));
            }
        }
        if (industrialTv.getDeviceId() != null && !industrialTv.getDeviceId().trim().isEmpty()) {
            List<TIndustrialTvPreset> presetList = industrialTvPresetService.lambdaQuery()
                .eq(TIndustrialTvPreset::getIndustrialTvId, industrialTv.getDeviceId())
                .list();
            result.setPresetList(presetList);
        }
        return result;
    }

    private void buildQueryCondition(LambdaQueryWrapper<TIndustrialTvBaseInfo> queryWrapper, IndustrialTVListRequest request) {
        String stationIdVal = StringUtils.trimToNull(request.getStationId());
        if (stationIdVal == null) {
            stationIdVal = StringUtils.trimToNull(request.getBelongStationId());
        }
        String operationAreaVal = StringUtils.trimToNull(request.getBelongOperationArea());
        String pipelineVal = StringUtils.trimToNull(request.getBelongPipeline());

        if (stationIdVal != null || operationAreaVal != null || pipelineVal != null) {
            LambdaQueryWrapper<TStationBaseInfo> stationWrapper = new LambdaQueryWrapper<>();
            if (stationIdVal != null) {
                stationWrapper.eq(TStationBaseInfo::getStationId, stationIdVal);
            }
            if (operationAreaVal != null) {
                stationWrapper.eq(TStationBaseInfo::getBelongOperationArea, operationAreaVal);
            }
            if (pipelineVal != null) {
                stationWrapper.eq(TStationBaseInfo::getBelongPipeline, pipelineVal);
            }
            List<TStationBaseInfo> stations = stationBaseInfoService.list(stationWrapper);
            List<String> stationIds = stations.stream()
                .map(TStationBaseInfo::getStationId)
                .filter(id -> id != null && !id.trim().isEmpty())
                .collect(Collectors.toList());
            if (stationIds.isEmpty()) {
                queryWrapper.eq(TIndustrialTvBaseInfo::getDeviceId, "");
                return;
            }
            queryWrapper.in(TIndustrialTvBaseInfo::getBelongStationId, stationIds);
        }

        String deviceNameVal = StringUtils.trimToNull(request.getDeviceName());
        String deviceCodeVal = StringUtils.trimToNull(request.getDeviceCode());
        String brandVal = StringUtils.trimToNull(request.getBrand());
        if (deviceNameVal != null) {
            queryWrapper.like(TIndustrialTvBaseInfo::getDeviceName, deviceNameVal);
        }
        if (deviceCodeVal != null) {
            queryWrapper.like(TIndustrialTvBaseInfo::getDeviceCode, deviceCodeVal);
        }
        if (brandVal != null) {
            queryWrapper.like(TIndustrialTvBaseInfo::getBrand, brandVal);
        }
    }

    @Override
    public IndustrialTvWithStationInfo getId(String deviceId) {
        TIndustrialTvBaseInfo industrialTv = industrialTvBaseInfoService.getById(deviceId);
        return convertToWithStationInfo(industrialTv);
    }

    @Override
    public boolean add(TIndustrialTvBaseInfo industrialTvBaseInfo) {
        return industrialTvBaseInfoService.save(industrialTvBaseInfo);
    }

    @Override
    public boolean update(TIndustrialTvBaseInfo industrialTvBaseInfo) {
        return industrialTvBaseInfoService.updateById(industrialTvBaseInfo);
    }

    @Override
    public boolean delete(String deviceId) {
        return industrialTvBaseInfoService.removeById(deviceId);
    }

    @Override
    public List<StationOptionDTO> listStationOptions() {
        List<TStationBaseInfo> stationList = stationBaseInfoService.list();
        return stationList.stream().map(station -> {
            StationOptionDTO dto = new StationOptionDTO();
            dto.setStationId(station.getStationId());
            dto.setStationName(station.getStationName());
            dto.setBelongOperationArea(station.getBelongOperationArea());
            dto.setBelongPipeline(station.getBelongPipeline());
            dto.setBelongOperationAreaName(resolveWorkareaName(station.getBelongOperationArea()));
            dto.setBelongPipelineName(resolvePipelineName(station.getBelongPipeline()));
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<AlgorithmOptionDTO> listAlgorithmOptions() {
        List<TConfiguredAlgorithmBaseInfo> list = configuredAlgorithmBaseInfoService.list();
        return list.stream().map(alg -> {
            AlgorithmOptionDTO dto = new AlgorithmOptionDTO();
            dto.setAlgorithmId(alg.getAlgorithmId());
            dto.setAlgorithmName(alg.getAlgorithmName());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<StationAreaOptionDTO> listStationAreaOptions(String stationId) {
        LambdaQueryWrapper<TStationAreaBaseInfo> q = new LambdaQueryWrapper<>();
        if (stationId != null && !stationId.trim().isEmpty()) {
            q.eq(TStationAreaBaseInfo::getBelongStationId, stationId.trim());
        }
        List<TStationAreaBaseInfo> list = stationAreaBaseInfoService.list(q);
        return list.stream().map(area -> {
            StationAreaOptionDTO dto = new StationAreaOptionDTO();
            dto.setAreaId(area.getAreaId());
            dto.setAreaName(area.getAreaName());
            dto.setBelongStationId(area.getBelongStationId());
            return dto;
        }).collect(Collectors.toList());
    }

    private String resolveWorkareaName(String belongOperationArea) {
        if (belongOperationArea == null || belongOperationArea.trim().isEmpty()) {
            return null;
        }
        try {
            Long orgId = Long.parseLong(belongOperationArea.trim());
            HrOrganization org = sysHrOrganizationService.getById(orgId);
            return org != null ? org.getOrgName() : null;
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private String resolvePipelineName(String pipelineId) {
        if (pipelineId == null || pipelineId.trim().isEmpty()) {
            return null;
        }
        TPipelineBaseInfo pipeline = pipelineBaseInfoService.getById(pipelineId);
        return pipeline != null ? pipeline.getPipelineName() : null;
    }

    @Override
    public boolean checkDeviceCodeUnique(String belongStationId, String deviceCode, String deviceIp, String deviceId) {
        if (belongStationId == null || belongStationId.trim().isEmpty()) {
            return false;
        }
        if ((deviceCode == null || deviceCode.trim().isEmpty()) && (deviceIp == null || deviceIp.trim().isEmpty())) {
            return false;
        }

        // 获取或创建该站场的锁对象
        ReentrantLock lock = STATION_LOCKS.computeIfAbsent(belongStationId, k -> new ReentrantLock());

        lock.lock();
        try {
            long count = 0;
            if (deviceCode != null && !deviceCode.trim().isEmpty()) {
                // 查询该站场下是否存在相同设备编码的设备
                LambdaQueryWrapper<TIndustrialTvBaseInfo> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(TIndustrialTvBaseInfo::getBelongStationId, belongStationId.trim())
                        .eq(TIndustrialTvBaseInfo::getDeviceCode, deviceCode.trim());

                // 编辑时排除自身
                if (StringUtils.isNotBlank(deviceId)) {
                    queryWrapper.ne(TIndustrialTvBaseInfo::getDeviceId, deviceId.trim());
                }

                count += industrialTvBaseInfoService.count(queryWrapper);
            }
            if (deviceIp != null && !deviceIp.trim().isEmpty()) {
                // 查询该站场下是否存在相同设备IP的设备
                LambdaQueryWrapper<TIndustrialTvBaseInfo> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(TIndustrialTvBaseInfo::getBelongStationId, belongStationId.trim())
                        .eq(TIndustrialTvBaseInfo::getCameraIp, deviceIp.trim());

                // 编辑时排除自身
                if (StringUtils.isNotBlank(deviceId)) {
                    queryWrapper.ne(TIndustrialTvBaseInfo::getDeviceId, deviceId.trim());
                }

                count += industrialTvBaseInfoService.count(queryWrapper);
            }
            // count为0表示唯一，返回true；否则返回false
            return count == 0;
        } finally {
            lock.unlock();
        }
    }
}
