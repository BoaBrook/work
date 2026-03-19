package cn.stylefeng.guns.modular.laserPanTilt.service.impl;

import cn.stylefeng.guns.core.dto.request.IdsRequest;
import cn.stylefeng.guns.core.utils.PageToPageResultUtils;
import cn.stylefeng.guns.database.entity.*;
import cn.stylefeng.guns.database.mapper.TLaserPanTiltBaseInfoMapper;
import cn.stylefeng.guns.database.mapper.TPipelineBaseInfoMapper;
import cn.stylefeng.guns.database.mapper.TStationBaseInfoMapper;
import cn.stylefeng.guns.database.mapper.TWorkareaBaseInfoMapper;
import cn.stylefeng.guns.database.service.TLaserPanTiltBaseInfoService;
import cn.stylefeng.guns.database.service.TPipelineBaseInfoService;
import cn.stylefeng.guns.database.service.TStationBaseInfoService;
import cn.stylefeng.guns.database.service.TThresholdConfigService;
import cn.stylefeng.guns.modular.laserPanTilt.entity.LaserPanTiltListVO;
import cn.stylefeng.guns.modular.laserPanTilt.entity.ThresholdConfigVO;
import cn.stylefeng.guns.modular.laserPanTilt.request.LaserPanTiltRequest;
import cn.stylefeng.guns.modular.laserPanTilt.service.LaserPanTiltService;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;
import cn.stylefeng.roses.kernel.sys.modular.org.entity.HrOrganization;
import cn.stylefeng.roses.kernel.sys.modular.org.service.HrOrganizationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 激光云台设备服务实现
 *
 * @author system
 * @date 2026-02-02
 */
@Service
public class LaserPanTiltServiceImpl extends ServiceImpl<TLaserPanTiltBaseInfoMapper, TLaserPanTiltBaseInfo> implements LaserPanTiltService {

    @Resource
    private TLaserPanTiltBaseInfoMapper tLaserPanTiltBaseInfoMapper;

    @Resource
    private TStationBaseInfoMapper tStationBaseInfoMapper;

    @Resource
    private TStationBaseInfoService tStationBaseInfoService;

    @Resource
    private TPipelineBaseInfoService tPipelineBaseInfoService;

    @Resource
    private HrOrganizationService sysHrOrganizationService;

    @Resource
    private TThresholdConfigService tThresholdConfigService;

    @Resource
    private TLaserPanTiltBaseInfoService tLaserPanTiltBaseInfoService;

    @Resource
    private TWorkareaBaseInfoMapper tWorkareaBaseInfoMapper;

    @Resource
    private TPipelineBaseInfoMapper tPipelineBaseInfoMapper;

    @Override
    public PageResult<?> list(Map<String, Object> params) {
        LambdaQueryWrapper<TLaserPanTiltBaseInfo> queryWrapper = new LambdaQueryWrapper<>();
        applyStationFilter(params, queryWrapper);
        String deviceName = paramStr(params, "deviceName");
        if (deviceName != null) {
            queryWrapper.like(TLaserPanTiltBaseInfo::getDeviceName, deviceName);
        }
        String deviceCode = paramStr(params, "deviceCode");
        if (deviceCode != null) {
            queryWrapper.like(TLaserPanTiltBaseInfo::getDeviceCode, deviceCode);
        }

        Page<TLaserPanTiltBaseInfo> resultPage = this.page(
                new Page<>(getIntParam(params, "pageNo", 1), getIntParam(params, "pageSize", 10)), queryWrapper);
        List<TLaserPanTiltBaseInfo> records = resultPage.getRecords();
        if (!records.isEmpty()) {
            fillStationNames(records);
            fillThresholdConfigs(records);
        }
        List<LaserPanTiltListVO> voList = records.stream().map(this::toListVO).collect(Collectors.toList());
        Page<LaserPanTiltListVO> voPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        voPage.setRecords(voList);
        return PageToPageResultUtils.pageToPageResult(voPage);
    }

    @Override
    public boolean saveThreshold(TThresholdConfig thresholdConfig) {
        String highHighOperator = thresholdConfig.getHighHighOperator();
        if (">=".equals(highHighOperator)) {
            thresholdConfig.setHighOperatorMax("<");
        } else if (">".equals(highHighOperator)) {
            thresholdConfig.setHighOperatorMax("<=");
        }
        return tThresholdConfigService.saveOrUpdate(thresholdConfig);
    }

    private LaserPanTiltListVO toListVO(TLaserPanTiltBaseInfo record) {
        LaserPanTiltListVO vo = new LaserPanTiltListVO();
        org.springframework.beans.BeanUtils.copyProperties(record, vo);
        vo.setThresholdConfig(toThresholdConfigVO(record.getThresholdConfig()));
        return vo;
    }

    private ThresholdConfigVO toThresholdConfigVO(TThresholdConfig config) {
        if (config == null) {
            return null;
        }
        ThresholdConfigVO vo = new ThresholdConfigVO();
        org.springframework.beans.BeanUtils.copyProperties(config, vo);
        vo.setCreateTime(toDate(config.getCreateTime()));
        vo.setUpdateTime(toDate(config.getUpdateTime()));
        return vo;
    }

    private Date toDate(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Date) {
            return (Date) value;
        }
        if (value instanceof Long) {
            return new Date((Long) value);
        }
        return null;
    }

    private void fillStationNames(List<TLaserPanTiltBaseInfo> records) {
        List<String> stationIds = records.stream()
                .map(TLaserPanTiltBaseInfo::getBelongStationId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        if (stationIds.isEmpty()) {
            return;
        }

        List<TStationBaseInfo> stationList = tStationBaseInfoService.listByIds(stationIds);
        Map<String, TStationBaseInfo> stationMap = stationList.stream()
                .collect(Collectors.toMap(TStationBaseInfo::getStationId, s -> s, (a, b) -> a));

        List<String> pipelineIds = stationList.stream()
                .map(TStationBaseInfo::getBelongPipeline)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        List<Long> orgIds = stationList.stream()
                .map(TStationBaseInfo::getBelongOperationArea)
                .filter(Objects::nonNull)
                .filter(s -> !s.isEmpty())
                .distinct()
                .map(this::parseLongOrNull)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        Map<String, String> pipelineIdToName = pipelineIds.isEmpty() ? Collections.emptyMap()
                : tPipelineBaseInfoService.listByIds(pipelineIds).stream()
                .collect(Collectors.toMap(TPipelineBaseInfo::getPipelineId, TPipelineBaseInfo::getPipelineName, (a, b) -> a));
        Map<String, String> orgIdToName = orgIds.isEmpty() ? Collections.emptyMap()
                : sysHrOrganizationService.listByIds(orgIds).stream()
                .collect(Collectors.toMap(org -> String.valueOf(org.getOrgId()), HrOrganization::getOrgName, (a, b) -> a));

        records.forEach(record -> {
            TStationBaseInfo station = stationMap.get(record.getBelongStationId());
            if (station != null) {
                record.setBelongStationName(station.getStationName());
                record.setBelongOperationArea(orgIdToName.get(station.getBelongOperationArea()));
                record.setBelongPipeline(pipelineIdToName.get(station.getBelongPipeline()));
            }
        });
    }

    private void fillThresholdConfigs(List<TLaserPanTiltBaseInfo> records) {
        List<String> deviceIds = records.stream()
                .map(TLaserPanTiltBaseInfo::getDeviceId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        if (deviceIds.isEmpty()) {
            return;
        }
        List<TThresholdConfig> configs = tThresholdConfigService.listByIds(deviceIds);
        Map<String, TThresholdConfig> configMap = configs.stream()
                .collect(Collectors.toMap(TThresholdConfig::getDeviceId, c -> c, (a, b) -> a));
        records.forEach(record -> record.setThresholdConfig(configMap.get(record.getDeviceId())));
    }

    private Long parseLongOrNull(String s) {
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static int getIntParam(Map<String, Object> params, String key, int defaultValue) {
        Object value = params.get(key);
        String str = value == null ? "" : String.valueOf(value).trim();
        if (str.isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private void applyStationFilter(Map<String, Object> params, LambdaQueryWrapper<TLaserPanTiltBaseInfo> queryWrapper) {
        String belongStationId = paramStr(params, "belongStationId");
        String belongOperationArea = paramStr(params, "belongOperationArea");
        String belongPipeline = paramStr(params, "belongPipeline");
        if (belongStationId == null && belongOperationArea == null && belongPipeline == null) {
            return;
        }
        LambdaQueryWrapper<TStationBaseInfo> stationWrapper = new LambdaQueryWrapper<>();
        if (belongStationId != null) {
            stationWrapper.eq(TStationBaseInfo::getStationId, belongStationId);
        }
        if (belongOperationArea != null) {
            stationWrapper.eq(TStationBaseInfo::getBelongOperationArea, belongOperationArea);
        }
        if (belongPipeline != null) {
            stationWrapper.eq(TStationBaseInfo::getBelongPipeline, belongPipeline);
        }
        List<String> stationIds = tStationBaseInfoService.list(stationWrapper).stream()
                .map(TStationBaseInfo::getStationId)
                .filter(id -> id != null && !id.trim().isEmpty())
                .collect(Collectors.toList());
        if (stationIds.isEmpty()) {
            queryWrapper.in(TLaserPanTiltBaseInfo::getDeviceId, new ArrayList<>());
            return;
        }
        queryWrapper.in(TLaserPanTiltBaseInfo::getBelongStationId, stationIds);
    }

    private static String paramStr(Map<String, Object> params, String key) {
        Object v = params.get(key);
        if (v == null) {
            return null;
        }
        String s = String.valueOf(v).trim();
        return s.isEmpty() ? null : s;
    }

    @Override
    public PageResult<TLaserPanTiltBaseInfo> pageList(LaserPanTiltRequest request) {
        LambdaQueryWrapper<TLaserPanTiltBaseInfo> queryWrapper = new LambdaQueryWrapper<>();

        handleOperationAreaAndPipelineQuery(request, queryWrapper);

        queryWrapper.like(StringUtils.isNotBlank(request.getDeviceName()),
                        TLaserPanTiltBaseInfo::getDeviceName, request.getDeviceName())
                .like(StringUtils.isNotBlank(request.getDeviceCode()),
                        TLaserPanTiltBaseInfo::getDeviceCode, request.getDeviceCode())
                .eq(StringUtils.isNotBlank(request.getBelongStationId()),
                        TLaserPanTiltBaseInfo::getBelongStationId, request.getBelongStationId());

        Page<TLaserPanTiltBaseInfo> page = new Page<>(request.getPageNo(), request.getPageSize());
        Page<TLaserPanTiltBaseInfo> resultPage = tLaserPanTiltBaseInfoMapper.selectPage(page, queryWrapper);

        List<TLaserPanTiltBaseInfo> records = resultPage.getRecords();
        if (CollectionUtils.isNotEmpty(records)) {
            // 收集所有站场ID
            List<String> stationIds = records.stream()
                    .map(TLaserPanTiltBaseInfo::getBelongStationId)
                    .filter(StringUtils::isNotBlank)
                    .distinct()
                    .collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(stationIds)) {
                // 批量查询站场信息
                List<TStationBaseInfo> stationList = tStationBaseInfoMapper.selectBatchIds(stationIds);
                Map<String, TStationBaseInfo> stationMap = stationList.stream()
                        .collect(Collectors.toMap(TStationBaseInfo::getStationId, s -> s, (v1, v2) -> v1));

                // 收集作业区ID和管线ID
                List<String> workareaIds = stationList.stream()
                        .map(TStationBaseInfo::getBelongOperationArea)
                        .filter(StringUtils::isNotBlank)
                        .distinct()
                        .collect(Collectors.toList());
                List<String> pipelineIds = stationList.stream()
                        .map(TStationBaseInfo::getBelongPipeline)
                        .filter(StringUtils::isNotBlank)
                        .distinct()
                        .collect(Collectors.toList());

                // 批量查询作业区和管线信息
                Map<String, String> workareaNameMap = null;
                Map<String, String> pipelineNameMap = null;

                if (CollectionUtils.isNotEmpty(workareaIds)) {
                    List<TWorkareaBaseInfo> workareaList = tWorkareaBaseInfoMapper.selectBatchIds(workareaIds);
                    workareaNameMap = workareaList.stream()
                            .collect(Collectors.toMap(TWorkareaBaseInfo::getWorkareaId,
                                    TWorkareaBaseInfo::getWorkareaName, (v1, v2) -> v1));
                }
                if (CollectionUtils.isNotEmpty(pipelineIds)) {
                    List<TPipelineBaseInfo> pipelineList = tPipelineBaseInfoMapper.selectBatchIds(pipelineIds);
                    pipelineNameMap = pipelineList.stream()
                            .collect(Collectors.toMap(TPipelineBaseInfo::getPipelineId,
                                    TPipelineBaseInfo::getPipelineName, (v1, v2) -> v1));
                }

                // 设置关联信息
                for (TLaserPanTiltBaseInfo record : records) {
                    setStationInfoWithNames(record, stationMap, workareaNameMap, pipelineNameMap);
                }
            }
        }

        return PageToPageResultUtils.pageToPageResult(resultPage);
    }

    @Override
    public Boolean updateLaserPanTilt(TLaserPanTiltBaseInfo request) {
        if (StringUtils.isBlank(request.getDeviceId())) {
            // 新增
            request.setDeviceId(IdWorker.getIdStr());
            return tLaserPanTiltBaseInfoService.save(request);
        } else {
            // 修改
            return tLaserPanTiltBaseInfoService.updateById(request);
        }
    }

    @Override
    public Boolean batchDeleteLaserPanTilt(IdsRequest request) {
        List<String> idList = request.getIdList();
        if (CollectionUtils.isEmpty(idList)) {
            return true;
        }
        return tLaserPanTiltBaseInfoService.removeByIds(idList);
    }

    /**
     * 处理作业区和管线查询（Request参数）
     *
     * @param request 查询请求参数
     * @param queryWrapper 查询条件包装器
     */
    private void handleOperationAreaAndPipelineQuery(LaserPanTiltRequest request, LambdaQueryWrapper<TLaserPanTiltBaseInfo> queryWrapper) {
        String belongOperationArea = request.getBelongOperationArea();
        String belongPipeline = request.getBelongPipeline();

        if (StringUtils.isNotBlank(belongOperationArea) || StringUtils.isNotBlank(belongPipeline)) {
            LambdaQueryWrapper<TStationBaseInfo> stationQueryWrapper = new LambdaQueryWrapper<>();

            if (StringUtils.isNotBlank(belongOperationArea)) {
                stationQueryWrapper.eq(TStationBaseInfo::getBelongOperationArea, belongOperationArea);
            }

            if (StringUtils.isNotBlank(belongPipeline)) {
                stationQueryWrapper.eq(TStationBaseInfo::getBelongPipeline, belongPipeline);
            }

            List<TStationBaseInfo> stationList = tStationBaseInfoMapper.selectList(stationQueryWrapper);
            List<String> stationIdList = stationList.stream()
                    .map(TStationBaseInfo::getStationId)
                    .collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(stationIdList)) {
                queryWrapper.in(TLaserPanTiltBaseInfo::getBelongStationId, stationIdList);
            } else {
                queryWrapper.in(TLaserPanTiltBaseInfo::getDeviceId, new ArrayList<>());
            }
        }
    }

    /**
     * 设置设备的站场信息（作业区和管线名称）
     *
     * @param device 激光云台设备
     * @param stationMap 站场Map
     * @param workareaNameMap 作业区名称Map
     * @param pipelineNameMap 管线名称Map
     */
    private void setStationInfoWithNames(TLaserPanTiltBaseInfo device, Map<String, TStationBaseInfo> stationMap,
                                         Map<String, String> workareaNameMap, Map<String, String> pipelineNameMap) {
        if (device.getBelongStationId() != null) {
            TStationBaseInfo station = stationMap.get(device.getBelongStationId());
            if (station != null) {
                device.setBelongStationName(station.getStationName());
                String operationAreaId = station.getBelongOperationArea();
                String pipelineId = station.getBelongPipeline();

                // 设置作业区名称
                if (workareaNameMap != null && operationAreaId != null) {
                    device.setBelongOperationArea(workareaNameMap.get(operationAreaId));
                }

                // 设置管线名称
                if (pipelineNameMap != null && pipelineId != null) {
                    device.setBelongPipeline(pipelineNameMap.get(pipelineId));
                }
            }
        }
    }
}