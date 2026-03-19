package cn.stylefeng.guns.modular.firegas.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.stylefeng.guns.database.entity.TDeviceRelationRecords;
import cn.stylefeng.guns.database.entity.TFireGasHostBaseInfo;
import cn.stylefeng.guns.database.entity.TFireGasSensorBaseInfo;
import cn.stylefeng.guns.database.entity.TIndustrialTvBaseInfo;
import cn.stylefeng.guns.database.entity.TIndustrialTvPreset;
import cn.stylefeng.guns.database.entity.TStationAreaBaseInfo;
import cn.stylefeng.guns.database.entity.TStationBaseInfo;
import cn.stylefeng.guns.database.service.TDeviceRelationRecordsService;
import cn.stylefeng.guns.database.service.TFireGasHostBaseInfoService;
import cn.stylefeng.guns.database.service.TFireGasSensorBaseInfoService;
import cn.stylefeng.guns.database.service.TIndustrialTvBaseInfoService;
import cn.stylefeng.guns.database.service.TIndustrialTvPresetService;
import cn.stylefeng.guns.database.service.TStationAreaBaseInfoService;
import cn.stylefeng.guns.database.service.TStationBaseInfoService;
import cn.stylefeng.guns.modular.datimsien.dto.DatimsienRequestRt;
import cn.stylefeng.guns.modular.datimsien.dto.DatimsienRequestUnit;
import cn.stylefeng.guns.modular.datimsien.dto.DatimsienResponseRt;
import cn.stylefeng.guns.modular.datimsien.service.DatimsienRtService;
import cn.stylefeng.guns.modular.firegas.dto.FireGasSensorOnlineStatusResponse;
import cn.stylefeng.guns.modular.firegas.dto.FireGasSensorQueryRequest;
import cn.stylefeng.roses.kernel.db.api.factory.PageFactory;
import cn.stylefeng.roses.kernel.db.api.factory.PageResultFactory;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 火气系统传感器服务
 * 
 * @author system
 */
@Slf4j
@Service
public class FireGasSensorService {

    @Autowired
    private TFireGasSensorBaseInfoService fireGasSensorBaseInfoService;

    @Autowired
    private TStationAreaBaseInfoService stationAreaBaseInfoService;

    @Autowired
    private TFireGasHostBaseInfoService fireGasHostBaseInfoService;

    @Autowired
    private TStationBaseInfoService stationBaseInfoService;

    @Autowired(required = false)
    private DatimsienRtService datimsienRtService;

    @Autowired
    private TDeviceRelationRecordsService deviceRelationRecordsService;

    @Autowired
    private TIndustrialTvPresetService industrialTvPresetService;

    @Autowired
    private TIndustrialTvBaseInfoService industrialTvBaseInfoService;

    /**
     * 分页查询传感器设备列表（带关联信息）
     * 
     * @param request 查询请求参数
     * @return 分页结果
     */
    public PageResult<TFireGasSensorBaseInfo> getSensorPage(FireGasSensorQueryRequest request) {
        try {
            // 如果request为null，创建默认的request对象
            if (request == null) {
                request = new FireGasSensorQueryRequest();
                request.setPageNo(1);
                request.setPageSize(1000);
            }
            
            // 如果分页参数为空，设置默认值
            if (request.getPageNo() == null || request.getPageNo() < 1) {
                request.setPageNo(1);
            }
            if (request.getPageSize() == null || request.getPageSize() < 1) {
                request.setPageSize(1000);
            }
            
            // 构建查询条件
            LambdaQueryWrapper<TFireGasSensorBaseInfo> queryWrapper = new LambdaQueryWrapper<>();
            
            // 按所属站场过滤（通过区域表反查区域ID集合）
            if (request.getBelongStationId() != null && !request.getBelongStationId().trim().isEmpty()) {
                List<TStationAreaBaseInfo> areaList = stationAreaBaseInfoService.list(
                        new LambdaQueryWrapper<TStationAreaBaseInfo>()
                                .eq(TStationAreaBaseInfo::getBelongStationId, request.getBelongStationId())
                );

                if (areaList == null || areaList.isEmpty()) {
                    // 指定站场下没有区域，直接返回空分页结果
                    return PageResultFactory.createPageResult(new Page<>(request.getPageNo(), request.getPageSize(), 0));
                }

                Set<String> areaIdsForStation = areaList.stream()
                        .map(TStationAreaBaseInfo::getAreaId)
                        .filter(id -> id != null && !id.trim().isEmpty())
                        .collect(Collectors.toSet());

                if (areaIdsForStation.isEmpty()) {
                    return PageResultFactory.createPageResult(new Page<>(request.getPageNo(), request.getPageSize(), 0));
                }

                queryWrapper.in(TFireGasSensorBaseInfo::getBelongStationAreaId, areaIdsForStation);
            }

            if (request.getFireGasImageId() != null && !request.getFireGasImageId().trim().isEmpty()) {
                queryWrapper.eq(TFireGasSensorBaseInfo::getFireGasImageId, request.getFireGasImageId());
            }
            
            if (request.getFireGasHostId() != null && !request.getFireGasHostId().trim().isEmpty()) {
                queryWrapper.eq(TFireGasSensorBaseInfo::getFireGasHostId, request.getFireGasHostId());
            }
            
            // 分页查询传感器
            Page<TFireGasSensorBaseInfo> pageable = PageFactory.defaultPage(request);
            Page<TFireGasSensorBaseInfo> sensorPage = fireGasSensorBaseInfoService.page(pageable, queryWrapper);
            
            if (sensorPage == null) {
                return PageResultFactory.createPageResult(new Page<>(request.getPageNo(), request.getPageSize(), 0));
            }
            
            List<TFireGasSensorBaseInfo> sensors = sensorPage.getRecords();
            
            // 批量查询关联信息
            // 1. 获取所有区域ID和主机ID
            Set<String> areaIds = sensors.stream()
                    .map(TFireGasSensorBaseInfo::getBelongStationAreaId)
                    .filter(id -> id != null && !id.trim().isEmpty())
                    .collect(Collectors.toSet());
            
            Set<String> hostIds = sensors.stream()
                    .map(TFireGasSensorBaseInfo::getFireGasHostId)
                    .filter(id -> id != null && !id.trim().isEmpty())
                    .collect(Collectors.toSet());
            
            // 2. 批量查询区域信息
            Map<String, TStationAreaBaseInfo> areaMap = Collections.emptyMap();
            if (!areaIds.isEmpty()) {
                List<TStationAreaBaseInfo> areaList = stationAreaBaseInfoService.list(
                        new LambdaQueryWrapper<TStationAreaBaseInfo>()
                                .in(TStationAreaBaseInfo::getAreaId, areaIds)
                );
                areaMap = areaList.stream()
                        .collect(Collectors.toMap(
                                TStationAreaBaseInfo::getAreaId,
                                area -> area,
                                (existing, replacement) -> existing
                        ));
            }
            
            // 3. 批量查询主机信息
            Map<String, TFireGasHostBaseInfo> hostMap = Collections.emptyMap();
            if (!hostIds.isEmpty()) {
                List<TFireGasHostBaseInfo> hostList = fireGasHostBaseInfoService.list(
                        new LambdaQueryWrapper<TFireGasHostBaseInfo>()
                                .in(TFireGasHostBaseInfo::getDeviceId, hostIds)
                );
                hostMap = hostList.stream()
                        .collect(Collectors.toMap(
                                TFireGasHostBaseInfo::getDeviceId,
                                host -> host,
                                (existing, replacement) -> existing
                        ));
            }
            
            // 4. 获取所有站场ID，批量查询站场信息
            Set<String> stationIds = areaMap.values().stream()
                    .map(TStationAreaBaseInfo::getBelongStationId)
                    .filter(id -> id != null && !id.trim().isEmpty())
                    .collect(Collectors.toSet());
            
            Map<String, TStationBaseInfo> stationMap = Collections.emptyMap();
            if (!stationIds.isEmpty()) {
                List<TStationBaseInfo> stationList = stationBaseInfoService.list(
                        new LambdaQueryWrapper<TStationBaseInfo>()
                                .in(TStationBaseInfo::getStationId, stationIds)
                );
                stationMap = stationList.stream()
                        .collect(Collectors.toMap(
                                TStationBaseInfo::getStationId,
                                station -> station,
                                (existing, replacement) -> existing
                        ));
            }
            
            // 5. 批量查询流媒体地址
            // 5.1 获取所有传感器设备ID
            Set<String> sensorDeviceIds = sensors.stream()
                    .map(TFireGasSensorBaseInfo::getDeviceId)
                    .filter(id -> id != null && !id.trim().isEmpty())
                    .collect(Collectors.toSet());
            
            // 5.2 批量查询设备关联关系，获取presetId
            Map<String, String> deviceIdToPresetIdMap = Collections.emptyMap();
            if (!sensorDeviceIds.isEmpty()) {
                List<TDeviceRelationRecords> relationList = deviceRelationRecordsService.list(
                        new LambdaQueryWrapper<TDeviceRelationRecords>()
                                .in(TDeviceRelationRecords::getRelatedDeviceId, sensorDeviceIds)
                );
                deviceIdToPresetIdMap = relationList.stream()
                        .filter(relation -> relation.getPresetId() != null && !relation.getPresetId().trim().isEmpty())
                        .collect(Collectors.toMap(
                                TDeviceRelationRecords::getRelatedDeviceId,
                                TDeviceRelationRecords::getPresetId,
                                (existing, replacement) -> existing
                        ));
            }
            
            // 5.3 批量查询预设位信息，获取industrialTvId
            Map<String, String> presetIdToIndustrialTvIdMap = Collections.emptyMap();
            if (!deviceIdToPresetIdMap.isEmpty()) {
                Set<String> presetIds = deviceIdToPresetIdMap.values().stream()
                        .filter(id -> id != null && !id.trim().isEmpty())
                        .collect(Collectors.toSet());
                if (!presetIds.isEmpty()) {
                    List<TIndustrialTvPreset> presetList = industrialTvPresetService.list(
                            new LambdaQueryWrapper<TIndustrialTvPreset>()
                                    .in(TIndustrialTvPreset::getPresetId, presetIds)
                    );
                    presetIdToIndustrialTvIdMap = presetList.stream()
                            .filter(preset -> preset.getIndustrialTvId() != null && !preset.getIndustrialTvId().trim().isEmpty())
                            .collect(Collectors.toMap(
                                    TIndustrialTvPreset::getPresetId,
                                    TIndustrialTvPreset::getIndustrialTvId,
                                    (existing, replacement) -> existing
                            ));
                }
            }
            
            // 5.4 批量查询工业电视信息，获取streamAddress和onlineStatus（摄像头状态）
            Map<String, String> industrialTvIdToStreamAddressMap = Collections.emptyMap();
            Map<String, String> industrialTvIdToOnlineStatusMap = Collections.emptyMap();
            if (!presetIdToIndustrialTvIdMap.isEmpty()) {
                Set<String> industrialTvIds = presetIdToIndustrialTvIdMap.values().stream()
                        .filter(id -> id != null && !id.trim().isEmpty())
                        .collect(Collectors.toSet());
                if (!industrialTvIds.isEmpty()) {
                    List<TIndustrialTvBaseInfo> industrialTvList = industrialTvBaseInfoService.list(
                            new LambdaQueryWrapper<TIndustrialTvBaseInfo>()
                                    .in(TIndustrialTvBaseInfo::getDeviceId, industrialTvIds)
                    );
                    industrialTvIdToStreamAddressMap = industrialTvList.stream()
                            .filter(tv -> tv.getStreamAddress() != null && !tv.getStreamAddress().trim().isEmpty())
                            .collect(Collectors.toMap(
                                    TIndustrialTvBaseInfo::getDeviceId,
                                    TIndustrialTvBaseInfo::getStreamAddress,
                                    (existing, replacement) -> existing
                            ));
                    industrialTvIdToOnlineStatusMap = industrialTvList.stream()
                            .filter(tv -> tv.getDeviceId() != null)
                            .collect(Collectors.toMap(
                                    TIndustrialTvBaseInfo::getDeviceId,
                                    tv -> tv.getOnlineStatus() != null ? tv.getOnlineStatus() : "",
                                    (existing, replacement) -> existing
                            ));
                }
            }

            // 5.5 建立设备ID到流媒体地址、摄像头状态的映射
            Map<String, String> deviceIdToStreamAddressMap = new HashMap<>();
            Map<String, String> deviceIdToCameraOnlineStatusMap = new HashMap<>();
            for (Map.Entry<String, String> entry : deviceIdToPresetIdMap.entrySet()) {
                String deviceId = entry.getKey();
                String presetId = entry.getValue();
                String industrialTvId = presetIdToIndustrialTvIdMap.get(presetId);
                if (industrialTvId != null) {
                    String streamAddress = industrialTvIdToStreamAddressMap.get(industrialTvId);
                    if (streamAddress != null) {
                        deviceIdToStreamAddressMap.put(deviceId, streamAddress);
                    }
                    String cameraOnlineStatus = industrialTvIdToOnlineStatusMap.get(industrialTvId);
                    if (cameraOnlineStatus != null) {
                        deviceIdToCameraOnlineStatusMap.put(deviceId, cameraOnlineStatus);
                    }
                }
            }
            
            // 6. 组装返回结果，设置关联信息到实体类字段
            for (TFireGasSensorBaseInfo sensor : sensors) {
                // 设置区域名称
                TStationAreaBaseInfo area = areaMap.get(sensor.getBelongStationAreaId());
                if (area != null) {
                    sensor.setAreaName(area.getAreaName());
                    
                    // 设置站名称
                    TStationBaseInfo station = stationMap.get(area.getBelongStationId());
                    if (station != null) {
                        sensor.setStationName(station.getStationName());
                    }
                }
                
                // 设置主机名称和acq_unit_id
                TFireGasHostBaseInfo host = hostMap.get(sensor.getFireGasHostId());
                if (host != null) {
                    sensor.setHostName(host.getDeviceName());
                    sensor.setAcqUnitId(host.getAcqUnitId());
                }
                
                // 设置流媒体地址
                String streamAddress = deviceIdToStreamAddressMap.get(sensor.getDeviceId());
                if (streamAddress != null) {
                    sensor.setStreamAddress(streamAddress);
                }

                // 设置摄像头状态（来自TIndustrialTvBaseInfo.onlineStatus）
                String cameraOnlineStatus = deviceIdToCameraOnlineStatusMap.get(sensor.getDeviceId());
                if (cameraOnlineStatus != null) {
                    sensor.setCameraOnlineStatus(cameraOnlineStatus);
                }
            }
            
            log.info("分页查询传感器设备成功，总数: {}, 当前页: {}, 每页大小: {}", 
                    sensorPage.getTotal(), sensorPage.getCurrent(), sensorPage.getSize());
            
            return PageResultFactory.createPageResult(sensorPage);
        } catch (Exception e) {
            log.error("分页查询传感器设备失败", e);
            return new PageResult<>();
        }
    }

    /**
     * 统计所有传感器的在线状态
     * 
     * @return 在线状态统计结果
     */
    public FireGasSensorOnlineStatusResponse getSensorOnlineStatus(String belongStationId) {
        FireGasSensorOnlineStatusResponse response = new FireGasSensorOnlineStatusResponse();
        response.setTotalCount(0);
        response.setOnlineCount(0);

        try {
            if (datimsienRtService == null) {
                log.warn("DatimsienRtService未配置，无法查询在线状态");
                return response;
            }

            // 1. 查询所有传感器（可按站场过滤）
            List<TFireGasSensorBaseInfo> allSensors;
            if (belongStationId != null && !belongStationId.trim().isEmpty()) {
                // 先查出该站场下的所有区域
                List<TStationAreaBaseInfo> areaList = stationAreaBaseInfoService.list(
                        new LambdaQueryWrapper<TStationAreaBaseInfo>()
                                .eq(TStationAreaBaseInfo::getBelongStationId, belongStationId)
                );

                if (areaList == null || areaList.isEmpty()) {
                    log.info("指定站场下未找到区域信息，belongStationId: {}", belongStationId);
                    return response;
                }

                Set<String> areaIdsForStation = areaList.stream()
                        .map(TStationAreaBaseInfo::getAreaId)
                        .filter(id -> id != null && !id.trim().isEmpty())
                        .collect(Collectors.toSet());

                if (areaIdsForStation.isEmpty()) {
                    log.info("指定站场下区域ID集合为空，belongStationId: {}", belongStationId);
                    return response;
                }

                allSensors = fireGasSensorBaseInfoService.list(
                        new LambdaQueryWrapper<TFireGasSensorBaseInfo>()
                                .in(TFireGasSensorBaseInfo::getBelongStationAreaId, areaIdsForStation)
                );
            } else {
                // 不按站场过滤，查询所有传感器
                allSensors = fireGasSensorBaseInfoService.list();
            }
            if (allSensors == null || allSensors.isEmpty()) {
                log.info("未找到传感器设备");
                return response;
            }

            // 过滤掉没有偏移地址的传感器
            List<TFireGasSensorBaseInfo> validSensors = allSensors.stream()
                    .filter(sensor -> sensor.getOffsetAddress() != null && !sensor.getOffsetAddress().trim().isEmpty())
                    .collect(Collectors.toList());

            response.setTotalCount(validSensors.size());

            if (validSensors.isEmpty()) {
                return response;
            }

            // 2. 查询所有主机，获取acq_unit_id
            Set<String> hostIds = validSensors.stream()
                    .map(TFireGasSensorBaseInfo::getFireGasHostId)
                    .filter(id -> id != null && !id.trim().isEmpty())
                    .collect(Collectors.toSet());

            if (hostIds.isEmpty()) {
                return response;
            }

            List<TFireGasHostBaseInfo> hostList = fireGasHostBaseInfoService.list(
                    new LambdaQueryWrapper<TFireGasHostBaseInfo>()
                            .in(TFireGasHostBaseInfo::getDeviceId, hostIds)
            );

            // 创建主机ID到acq_unit_id的映射
            Map<String, String> hostIdToAcqUnitIdMap = hostList.stream()
                    .filter(host -> host.getAcqUnitId() != null && !host.getAcqUnitId().trim().isEmpty())
                    .collect(Collectors.toMap(
                            TFireGasHostBaseInfo::getDeviceId,
                            TFireGasHostBaseInfo::getAcqUnitId,
                            (existing, replacement) -> existing
                    ));

            // 3. 按主机分组传感器，构建rt请求
            Map<String, List<TFireGasSensorBaseInfo>> hostIdToSensorsMap = validSensors.stream()
                    .filter(sensor -> hostIdToAcqUnitIdMap.containsKey(sensor.getFireGasHostId()))
                    .collect(Collectors.groupingBy(TFireGasSensorBaseInfo::getFireGasHostId));

            // 构建rt请求单元列表
            List<DatimsienRequestUnit> requestUnits = new ArrayList<>();
            for (Map.Entry<String, List<TFireGasSensorBaseInfo>> entry : hostIdToSensorsMap.entrySet()) {
                String hostId = entry.getKey();
                String acqUnitId = hostIdToAcqUnitIdMap.get(hostId);
                List<TFireGasSensorBaseInfo> sensors = entry.getValue();

                DatimsienRequestUnit requestUnit = new DatimsienRequestUnit();
                requestUnit.setUnitId(acqUnitId);
                
                // 添加所有传感器的偏移地址作为tags
                List<String> tags = sensors.stream()
                        .map(TFireGasSensorBaseInfo::getOffsetAddress)
                        .filter(tag -> tag != null && !tag.trim().isEmpty())
                        .collect(Collectors.toList());
                requestUnit.setTags(tags);

                if (!tags.isEmpty()) {
                    requestUnits.add(requestUnit);
                }
            }

            if (requestUnits.isEmpty()) {
                log.warn("未找到有效的请求单元");
                return response;
            }

            // 4. 调用rt接口查询状态
            DatimsienRequestRt rtRequest = new DatimsienRequestRt(requestUnits);
            List<DatimsienResponseRt> rtResponses = datimsienRtService.getRtDataRemote(rtRequest);

            if (rtResponses == null || rtResponses.isEmpty()) {
                log.warn("rt接口返回空数据");
                return response;
            }

            // 5. 创建unitId+tag到传感器的映射，用于匹配返回结果
            Map<String, TFireGasSensorBaseInfo> unitTagToSensorMap = new HashMap<>();
            for (Map.Entry<String, List<TFireGasSensorBaseInfo>> entry : hostIdToSensorsMap.entrySet()) {
                String hostId = entry.getKey();
                String acqUnitId = hostIdToAcqUnitIdMap.get(hostId);
                List<TFireGasSensorBaseInfo> sensors = entry.getValue();

                for (TFireGasSensorBaseInfo sensor : sensors) {
                    if (sensor.getOffsetAddress() != null) {
                        String key = acqUnitId + "_" + sensor.getOffsetAddress();
                        unitTagToSensorMap.put(key, sensor);
                    }
                }
            }

            // 6. 解析返回结果，统计在线数量
            int onlineCount = 0;
            Set<String> processedSensors = new java.util.HashSet<>();

            for (DatimsienResponseRt rtResponse : rtResponses) {
                String unitId = rtResponse.getUnitId();
                Object[] values = rtResponse.getValues();
                
                if (values == null) {
                    continue;
                }
                
                // 需要知道tags的顺序，从请求中获取
                DatimsienRequestUnit requestUnit = requestUnits.stream()
                        .filter(unit -> unit.getUnitId().equals(unitId))
                        .findFirst()
                        .orElse(null);

                if (requestUnit == null || requestUnit.getTags() == null || requestUnit.getTags().isEmpty()) {
                    continue;
                }

                List<String> tags = requestUnit.getTags();
                
                // 遍历values，判断是否在线
                for (int i = 0; i < tags.size() && i < values.length; i++) {
                    String tag = tags.get(i);
                    Object value = values[i];
                    
                    String key = unitId + "_" + tag;
                    TFireGasSensorBaseInfo sensor = unitTagToSensorMap.get(key);
                    
                    if (sensor != null && !processedSensors.contains(sensor.getDeviceId())) {
                        processedSensors.add(sensor.getDeviceId());
                        
                        // value有值代表在线，null代表离线
                        if (value != null) {
                            onlineCount++;
                        }
                    }
                }
            }

            response.setOnlineCount(onlineCount);

            log.info("统计传感器在线状态完成，总数: {}, 在线数: {}", response.getTotalCount(), response.getOnlineCount());

            return response;
        } catch (Exception e) {
            log.error("统计传感器在线状态失败", e);
            return response;
        }
    }
}
