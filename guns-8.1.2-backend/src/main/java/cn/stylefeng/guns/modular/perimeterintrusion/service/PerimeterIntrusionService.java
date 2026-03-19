package cn.stylefeng.guns.modular.perimeterintrusion.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.stylefeng.guns.core.consts.AlarmResultConstants;
import cn.stylefeng.guns.core.consts.ProjectConstants;
import cn.stylefeng.guns.database.entity.*;
import cn.stylefeng.guns.database.service.*;
import cn.stylefeng.guns.enums.SystemTypeEnum;
import cn.stylefeng.guns.modular.broadcast.request.PlayVoiceRequest;
import cn.stylefeng.guns.modular.broadcast.service.BroadcastService;
import cn.stylefeng.guns.modular.hikvision.service.HikVisionService;
import cn.stylefeng.guns.modular.industrialTV.request.ControlPresetRequest;
import cn.stylefeng.guns.modular.industrialTV.service.IndustrialTVService;
import cn.stylefeng.guns.modular.linkagealarm.request.LinkageAlarmRequest;
import cn.stylefeng.guns.modular.accesscontrol.service.AccessControlGatewayService;
import cn.stylefeng.guns.modular.accesscontrol.request.AccessControlGatewayRequest;
import cn.stylefeng.guns.modular.perimeterintrusion.dto.*;
import cn.stylefeng.guns.modular.perimeterintrusion.enums.PerimeterAlarmTypeEnum;
import cn.stylefeng.guns.modular.perimeterintrusion.remote.client.PerimeterIntrusionClient;
import cn.stylefeng.guns.modular.perimeterintrusion.remote.dto.PerimeterIntrusionArmZoneReq;
import cn.stylefeng.guns.modular.perimeterintrusion.remote.dto.PerimeterIntrusionArmZoneRequest;
import cn.stylefeng.guns.modular.perimeterintrusion.remote.dto.PerimeterIntrusionZone;
import cn.stylefeng.roses.kernel.auth.api.context.LoginContext;
import cn.stylefeng.roses.kernel.auth.api.pojo.login.LoginUser;
import cn.stylefeng.roses.kernel.db.api.factory.PageFactory;
import cn.stylefeng.roses.kernel.db.api.factory.PageResultFactory;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;
import cn.stylefeng.roses.kernel.rule.exception.base.ServiceException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PerimeterIntrusionService {

    /**
     * 站场级别的锁映射，用于校验设备编码唯一性时的并发控制
     * 按站场ID加锁，不同站场的校验可并行执行，同一站场的校验串行执行
     */
    private static final ConcurrentHashMap<String, ReentrantLock> STATION_LOCKS = new ConcurrentHashMap<>();

    private static final String DEVICE_TYPE_HOST = "主机";
    private static final String DEVICE_TYPE_ZONE = "防区";

    @Autowired
    private TPerimeterIntrusionZoneBaseInfoService perimeterIntrusionZoneBaseInfoService;
    @Autowired
    private TPerimeterIntrusionHostBaseInfoService perimeterIntrusionHostBaseInfoService;
    @Autowired
    private TWorkareaBaseInfoService workareaBaseInfoService;
    @Autowired
    private TStationAreaBaseInfoService stationAreaBaseInfoService;
    @Autowired
    private TStationBaseInfoService stationBaseInfoService;
    @Autowired
    private TPerimeterIntrusionZoneStatusRecordsService perimeterIntrusionZoneStatusRecordsService;
    @Autowired
    private TDeviceRelationRecordsService deviceRelationRecordsService;
    @Autowired
    private TIndustrialTvPresetService industrialTvPresetService;
    @Autowired
    private TIndustrialTvBaseInfoService industrialTvBaseInfoService;
    @Autowired
    private TAlarmResultRecordsService alarmResultRecordsService;
    @Autowired
    private PerimeterIntrusionClient perimeterIntrusionClient;
    @Autowired
    private PerimeterIntrusionNodeReportService nodeReportService;
    @Autowired
    private TPipelineBaseInfoService pipelineBaseInfoService;
    @Autowired
    private TLinkageAlarmConfigService tLinkageAlarmConfigService;
    @Autowired
    private TEmergencyBroadcastHostBaseInfoService tEmergencyBroadcastHostBaseInfoService;
    @Autowired
    private IndustrialTVService industrialTVService;
    @Autowired
    private HikVisionService hikVisionService;
    @Autowired
    private BroadcastService broadcastService;

    @Autowired
    private AccessControlGatewayService accessControlGatewayService;

    public Boolean alarm(PerimeterIntrusionRequest request) {
        log.info("*************周界入侵报警请求：{}",request);
        PerimeterAlarmTypeEnum perimeterAlarmTypeEnum = PerimeterAlarmTypeEnum.getByCode(request.getType());
        if (perimeterAlarmTypeEnum == null) {
            log.debug("未配置此报警类型：{}，忽略本次报警", request.getType());
            return true;
        }

        if (StringUtils.isBlank(request.getDefenceAreaName())) {
            throw new ServiceException(ProjectConstants.PROJECT_MODULE_NAME, "0", "请选择防区名称");
        }

        // 防区信息
        TPerimeterIntrusionZoneBaseInfo zoneBaseInfo = perimeterIntrusionZoneBaseInfoService.lambdaQuery()
                .eq(TPerimeterIntrusionZoneBaseInfo::getZoneName, request.getDefenceAreaName()).one();
        if (zoneBaseInfo == null) {
            throw new ServiceException(ProjectConstants.PROJECT_MODULE_NAME, "0", "未找到对应的防区信息，防区名称：" + request.getDefenceAreaName());
        }

        // 周界入侵设备信息
        TPerimeterIntrusionHostBaseInfo hostBaseInfo = perimeterIntrusionHostBaseInfoService.lambdaQuery()
                .eq(TPerimeterIntrusionHostBaseInfo::getDeviceId, zoneBaseInfo.getPerimeterIntrusionHostId()).one();
        if (hostBaseInfo == null) {
            throw new ServiceException(ProjectConstants.PROJECT_MODULE_NAME, "0", "未找到周界入侵设备信息，设备ID：" + zoneBaseInfo.getPerimeterIntrusionHostId());
        }

        // 区域信息
        TStationAreaBaseInfo stationArea = stationAreaBaseInfoService.getById(zoneBaseInfo.getBelongStationAreaId());
        if (stationArea == null) {
            throw new ServiceException(ProjectConstants.PROJECT_MODULE_NAME, "0", "未找到站场区域信息，区域ID：" + zoneBaseInfo.getBelongStationAreaId());
        }

        // 站场信息
        TStationBaseInfo station = stationBaseInfoService.getById(stationArea.getBelongStationId());
        if (station == null) {
            throw new ServiceException(ProjectConstants.PROJECT_MODULE_NAME, "0", "未找到站场信息，站场ID：" + stationArea.getBelongStationId());
        }

        // 管线信息
        TPipelineBaseInfo pipeline = pipelineBaseInfoService.getById(station.getBelongPipeline());
        if (pipeline == null) {
            throw new ServiceException(ProjectConstants.PROJECT_MODULE_NAME, "0", "未找到管线信息，管线ID：" + station.getBelongPipeline());
        }


        TAlarmResultRecords alarmResultRecords = new TAlarmResultRecords();
        alarmResultRecords.setAlarmId(request.getAlarmId());
        alarmResultRecords.setAlarmDeviceId(zoneBaseInfo.getPerimeterIntrusionHostId());
        alarmResultRecords.setAlarmLocation(request.getAreaName());
        alarmResultRecords.setSubsystemType(SystemTypeEnum.ZJRQ.getCode());

        alarmResultRecords.setAlarmType(perimeterAlarmTypeEnum.getType());
        alarmResultRecords.setAlarmLevel(perimeterAlarmTypeEnum.getLevel());

        String content = perimeterAlarmTypeEnum.getContent();
        if (perimeterAlarmTypeEnum.getIsZone()) {
            content = String.format(content, pipeline.getPipelineName(), station.getStationName(), zoneBaseInfo.getZoneName());
        } else {
            content = String.format(content, pipeline.getPipelineName(), station.getStationName());
        }
        alarmResultRecords.setAlarmContent(content);

        alarmResultRecords.setAlarmTime(DateUtil.parse(request.getStartTime(), "yyyy-MM-dd HH:mm:ss"));
        alarmResultRecords.setDisposalStatus(AlarmResultConstants.DISPOSAL_STATUS_UNDISPOSED);
        alarmResultRecordsService.save(alarmResultRecords);
        // 触发联动报警
        LinkageAlarmRequest linkageAlarmRequest = new LinkageAlarmRequest();
        linkageAlarmRequest.setHostId(alarmResultRecords.getAlarmDeviceId());
        linkageAlarmRequest.setAlarmType(request.getType());
        linkageAlarm(linkageAlarmRequest);
        // 上报省级平台
        nodeReportService.onNewAlarm(alarmResultRecords, zoneBaseInfo, hostBaseInfo, station);
        return true;
    }

    /**
     * 查询周界入侵主机设备基础信息列表（分页）
     *
     * @param request 查询请求参数
     * @return 主机设备基础信息分页结果
     */
    public PageResult<TPerimeterIntrusionHostBaseInfo> getHostBaseInfoList(HostBaseInfoRequest request) {
        boolean isPage = request.getPageNo() != null && request.getPageSize() != null;
        int pageSize = isPage ? request.getPageSize() : 10;
        int pageNo = isPage ? request.getPageNo() : 1;
        Page<TPerimeterIntrusionHostBaseInfo> emptyPage = new Page<>(pageNo, pageSize);

        List<String> finalStationIdList = null;
        if (StringUtils.isNotBlank(request.getStationId()) || StringUtils.isNotBlank(request.getAreaId()) || StringUtils.isNotBlank(request.getLineId())) {
            List<TStationBaseInfo> stations = stationBaseInfoService.lambdaQuery()
                    .eq(StringUtils.isNotBlank(request.getStationId()), TStationBaseInfo::getStationId, request.getStationId())
                    .eq(StringUtils.isNotBlank(request.getAreaId()), TStationBaseInfo::getBelongOperationArea, request.getAreaId())
                    .eq(StringUtils.isNotBlank(request.getLineId()), TStationBaseInfo::getBelongPipeline, request.getLineId())
                    .list();

            if (CollectionUtils.isEmpty(stations)) {
                return PageResultFactory.createPageResult(emptyPage);
            }

            finalStationIdList = stations.stream()
                    .map(TStationBaseInfo::getStationId)
                    .collect(Collectors.toList());
        }

        LambdaQueryWrapper<TPerimeterIntrusionHostBaseInfo> queryWrapper = new LambdaQueryWrapper<>();

        // 设备编码
        if (StringUtils.isNotBlank(request.getDeviceCode())) {
            queryWrapper.like(TPerimeterIntrusionHostBaseInfo::getDeviceCode, request.getDeviceCode());
        }

        // 设备名称（模糊查询）
        if (StringUtils.isNotBlank(request.getDeviceName())) {
            queryWrapper.like(TPerimeterIntrusionHostBaseInfo::getDeviceName, request.getDeviceName());
        }
        
        // 如果有站场ID列表，添加到查询条件
        if (CollectionUtils.isNotEmpty(finalStationIdList)) {
            queryWrapper.in(TPerimeterIntrusionHostBaseInfo::getBelongStationId, finalStationIdList);
        }

        if (!isPage) {
            // 没传分页查所有的
            long count = perimeterIntrusionHostBaseInfoService.count(queryWrapper);
            if (count == 0L) {
                return PageResultFactory.createPageResult(emptyPage);
            }
            request.setPageNo(1);
            request.setPageSize(Long.valueOf(count).intValue());
        }

        // 分页查询
        Page<TPerimeterIntrusionHostBaseInfo> page = PageFactory.defaultPage(request);
        Page<TPerimeterIntrusionHostBaseInfo> pageResult = perimeterIntrusionHostBaseInfoService.page(page, queryWrapper);
        processHostData(pageResult.getRecords());
        return PageResultFactory.createPageResult(pageResult);
    }

    private void processHostData(List<TPerimeterIntrusionHostBaseInfo> records) {
        if (CollectionUtils.isEmpty(records)) {
            return;
        }

        Set<String> stationIds = records.stream().map(TPerimeterIntrusionHostBaseInfo::getBelongStationId).collect(Collectors.toSet());
        List<TStationBaseInfo> stationList = stationBaseInfoService.listByIds(stationIds);
        if (CollectionUtils.isEmpty(stationList)) {
            return;
        }

        Map<String, TStationBaseInfo> stationMap = new HashMap<>();
        Set<String> workAreaIds = new HashSet<>();
        Set<String> pipeLineIds = new HashSet<>();
        for (TStationBaseInfo station : stationList) {
            workAreaIds.add(station.getBelongOperationArea());
            pipeLineIds.add(station.getBelongPipeline());
            stationMap.put(station.getStationId(), station);
        }
        List<TWorkareaBaseInfo> workAreaList = workareaBaseInfoService.listByIds(workAreaIds);
        List<TPipelineBaseInfo> pipeLineList = pipelineBaseInfoService.listByIds(pipeLineIds);

        Map<String, String> workAreaNameMap = workAreaList.stream().collect(Collectors.toMap(TWorkareaBaseInfo::getWorkareaId, TWorkareaBaseInfo::getWorkareaName, (v1, v2) -> v1));
        Map<String, String> pipeLineNameMap = pipeLineList.stream().collect(Collectors.toMap(TPipelineBaseInfo::getPipelineId, TPipelineBaseInfo::getPipelineName, (v1, v2) -> v1));

        for (TPerimeterIntrusionHostBaseInfo host : records) {
            TStationBaseInfo station = stationMap.get(host.getBelongStationId());
            if (station != null) {
                host.setWorkAreaName(workAreaNameMap.get(station.getBelongOperationArea()));
                host.setPipelineName(pipeLineNameMap.get(station.getBelongPipeline()));
                host.setStationName(station.getStationName());
            }
        }
    }

    /**
     * 查询周界入侵防区基础信息列表（分页）
     *
     * @param request 查询请求参数
     * @return 防区基础信息分页结果
     */
    public PageResult<TPerimeterIntrusionZoneBaseInfo> getZoneBaseInfoList(ZoneBaseInfoRequest request) {
        boolean isPage = request.getPageNo() != null && request.getPageSize() != null;
        int pageSize = isPage ? request.getPageSize() : 10;
        int pageNo = isPage ? request.getPageNo() : 1;
        Page<TPerimeterIntrusionZoneBaseInfo> emptyPage = new Page<>(pageNo, pageSize);

        List<String> finalAreaIdList = null;
        if (StringUtils.isNotBlank(request.getStationId()) || StringUtils.isNotBlank(request.getAreaId()) || StringUtils.isNotBlank(request.getLineId())) {
            List<TStationBaseInfo> stations = stationBaseInfoService.lambdaQuery()
                    .eq(StringUtils.isNotBlank(request.getStationId()), TStationBaseInfo::getStationId, request.getStationId())
                    .eq(StringUtils.isNotBlank(request.getAreaId()), TStationBaseInfo::getBelongOperationArea, request.getAreaId())
                    .eq(StringUtils.isNotBlank(request.getLineId()), TStationBaseInfo::getBelongPipeline, request.getLineId())
                    .list();

            if (CollectionUtils.isEmpty(stations)) {
                return PageResultFactory.createPageResult(emptyPage);
            }

            List<TStationAreaBaseInfo> areaList = stationAreaBaseInfoService.lambdaQuery()
                    .in(TStationAreaBaseInfo::getBelongStationId, stations.stream().map(TStationBaseInfo::getStationId).collect(Collectors.toList()))
                    .list();

            if (CollectionUtils.isEmpty(areaList)) {
                return PageResultFactory.createPageResult(emptyPage);
            }

            finalAreaIdList = areaList.stream()
                    .map(TStationAreaBaseInfo::getAreaId)
                    .collect(Collectors.toList());
        }

        LambdaQueryWrapper<TPerimeterIntrusionZoneBaseInfo> queryWrapper = new LambdaQueryWrapper<>();

        // 防区编码
        if (StringUtils.isNotBlank(request.getZoneCode())) {
            queryWrapper.eq(TPerimeterIntrusionZoneBaseInfo::getZoneCode, request.getZoneCode());
        }

        // 防区名称（模糊查询）
        if (StringUtils.isNotBlank(request.getZoneName())) {
            queryWrapper.like(TPerimeterIntrusionZoneBaseInfo::getZoneName, request.getZoneName());
        }

        // 周界入侵主机设备ID
        if (StringUtils.isNotBlank(request.getHostId())) {
            queryWrapper.eq(TPerimeterIntrusionZoneBaseInfo::getPerimeterIntrusionHostId, request.getHostId());
        }

        if (CollectionUtils.isNotEmpty(finalAreaIdList)) {
            queryWrapper.in(TPerimeterIntrusionZoneBaseInfo::getBelongStationAreaId, finalAreaIdList);
        }

        if (!isPage) {
            // 没传分页查所有的
            long count = perimeterIntrusionZoneBaseInfoService.count(queryWrapper);
            if (count == 0L) {
                return PageResultFactory.createPageResult(emptyPage);
            }
            request.setPageNo(1);
            request.setPageSize(Long.valueOf(count).intValue());
        }

        // 分页查询
        Page<TPerimeterIntrusionZoneBaseInfo> page = PageFactory.defaultPage(request);
        Page<TPerimeterIntrusionZoneBaseInfo> pageResult = perimeterIntrusionZoneBaseInfoService.page(page, queryWrapper);
        processZoneData(pageResult.getRecords());
        return PageResultFactory.createPageResult(pageResult);
    }

    private void processZoneData(List<TPerimeterIntrusionZoneBaseInfo> records) {
        if (CollectionUtils.isEmpty(records)) {
            return;
        }

        Set<String> hostIds = new HashSet<>();
        Set<String> areaIds = new HashSet<>();
        Set<String> zoneIds = new HashSet<>();
        records.forEach(record -> {
            hostIds.add(record.getPerimeterIntrusionHostId());
            areaIds.add(record.getBelongStationAreaId());
            zoneIds.add(record.getZoneId());
        });

        // 周界主机信息
        List<TPerimeterIntrusionHostBaseInfo> hostList = perimeterIntrusionHostBaseInfoService.listByIds(hostIds);

        List<PerimeterIntrusionZone> zoneList = perimeterIntrusionClient.getZoneList();
        Map<String, String> armedStatusMap = zoneList.stream().collect(Collectors.toMap(PerimeterIntrusionZone::getCode, PerimeterIntrusionZone::getDefenceState, (v1, v2) -> v1));

        Map<String, String> hostNameMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(hostList)) {
            hostNameMap = hostList.stream().collect(Collectors.toMap(TPerimeterIntrusionHostBaseInfo::getDeviceId, TPerimeterIntrusionHostBaseInfo::getDeviceName, (v1, v2) -> v1));
        }

        // 摄像头信息
        List<TIndustrialTvBaseInfo> tvBaseInfos = industrialTvBaseInfoService.list();
        Map<String, String> tvNameMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(tvBaseInfos)) {
            tvNameMap = tvBaseInfos.stream().collect(Collectors.toMap(TIndustrialTvBaseInfo::getDeviceId, TIndustrialTvBaseInfo::getDeviceName, (v1, v2) -> v1));
        }
        List<TDeviceRelationRecords> relationRecords = deviceRelationRecordsService.lambdaQuery().in(TDeviceRelationRecords::getRelatedDeviceId, zoneIds).list();
        Map<String, String> presetIdMap = new HashMap<>();
        Set<String> presetIds = new HashSet<>();
        for (TDeviceRelationRecords relationRecord : relationRecords) {
            presetIdMap.put(relationRecord.getRelatedDeviceId(), relationRecord.getPresetId());
            presetIds.add(relationRecord.getPresetId());
        }

        Map<String, TIndustrialTvPreset> presetMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(presetIds)) {
            List<TIndustrialTvPreset> tIndustrialTvPresets = industrialTvPresetService.listByIds(presetIds);
            presetMap = tIndustrialTvPresets.stream().collect(Collectors.toMap(TIndustrialTvPreset::getPresetId, Function.identity(), (v1, v2) -> v1));
        }

        // 站场信息
        List<TStationAreaBaseInfo> stationAreaList = stationAreaBaseInfoService.listByIds(areaIds);

        Set<String> stationIds = new HashSet<>();
        Map<String, TStationAreaBaseInfo> stationAreaMap = new HashMap<>();
        for (TStationAreaBaseInfo stationArea : stationAreaList) {
            stationIds.add(stationArea.getBelongStationId());
            stationAreaMap.put(stationArea.getAreaId(), stationArea);
        }

        List<TStationBaseInfo> stationList = stationBaseInfoService.listByIds(stationIds);

        Map<String, TStationBaseInfo> stationMap = new HashMap<>();
        Set<String> workAreaIds = new HashSet<>();
        Set<String> pipeLineIds = new HashSet<>();
        for (TStationBaseInfo station : stationList) {
            workAreaIds.add(station.getBelongOperationArea());
            pipeLineIds.add(station.getBelongPipeline());
            stationMap.put(station.getStationId(), station);
        }

        Map<String, String> workAreaNameMap = new HashMap<>();
        Map<String, String> pipeLineNameMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(workAreaIds)) {
            List<TWorkareaBaseInfo> workAreaList = workareaBaseInfoService.listByIds(workAreaIds);
            workAreaNameMap = workAreaList.stream().collect(Collectors.toMap(TWorkareaBaseInfo::getWorkareaId, TWorkareaBaseInfo::getWorkareaName, (v1, v2) -> v1));
        }
        if (CollectionUtils.isNotEmpty(pipeLineIds)) {
            List<TPipelineBaseInfo> pipeLineList = pipelineBaseInfoService.listByIds(pipeLineIds);
            pipeLineNameMap = pipeLineList.stream().collect(Collectors.toMap(TPipelineBaseInfo::getPipelineId, TPipelineBaseInfo::getPipelineName, (v1, v2) -> v1));
        }

        for (TPerimeterIntrusionZoneBaseInfo zone : records) {
            zone.setHostDeviceName(hostNameMap.get(zone.getPerimeterIntrusionHostId()));
            zone.setArmedStatus(armedStatusMap.get(zone.getZoneCode()));
            String presetId = presetIdMap.get(zone.getZoneId());
            if (StringUtils.isNotBlank(presetId)) {
                TIndustrialTvPreset preset = presetMap.get(presetId);
                if (preset != null) {
                    zone.setPresetName(preset.getPresetName());
                    zone.setTvName(tvNameMap.get(preset.getIndustrialTvId()));
                }
            }

            TStationAreaBaseInfo stationArea = stationAreaMap.get(zone.getBelongStationAreaId());
            if (stationArea != null) {
                TStationBaseInfo station = stationMap.get(stationArea.getBelongStationId());
                if (station != null) {
                    zone.setWorkAreaName(workAreaNameMap.get(station.getBelongOperationArea()));
                    zone.setPipelineName(pipeLineNameMap.get(station.getBelongPipeline()));
                    zone.setStationName(station.getStationName());
                    zone.setStationId(station.getStationId());
                }
            }
        }
    }

    /**
     * 防区布防/撤防
     *
     * @param request 布防请求参数
     * @return 操作结果
     */
    public Boolean armZone(ZoneArmedRequest request) {
        String armedStatus = request.getArmedStatus();
        if (StringUtils.isBlank(armedStatus)) {
            throw new ServiceException(ProjectConstants.PROJECT_MODULE_NAME, "0", "请选择布防状态");
        }

        List<ZoneArmedRequest.Device> deviceIdList = request.getDevices();
        if (CollectionUtils.isEmpty(deviceIdList)) {
            throw new ServiceException(ProjectConstants.PROJECT_MODULE_NAME, "0", "请选择设备");
        }

        Set<String> hostIds = new HashSet<>();
        Set<String> zoneIds = new HashSet<>();
        for (ZoneArmedRequest.Device device : deviceIdList) {
            if (DEVICE_TYPE_HOST.equals(device.getDeviceType())) {
                hostIds.add(device.getDeviceId());
            } else {
                zoneIds.add(device.getDeviceId());
            }
        }
        // 根据主机进行布防撤防
        List<TPerimeterIntrusionHostBaseInfo> hostLists = perimeterIntrusionHostBaseInfoService.list();
        Map<String, TPerimeterIntrusionHostBaseInfo> hostMap = hostLists.stream()
                .collect(Collectors.toMap(TPerimeterIntrusionHostBaseInfo::getDeviceId,
                        Function.identity(), (v1, v2) -> v1));
        if (!CollectionUtils.isEmpty(hostIds)) {
            // 查询当前主机的防区
            List<TPerimeterIntrusionHostBaseInfo> hostList = hostLists.stream().filter(it -> hostIds.contains(it.getDeviceId())).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(hostList)) {
                throw new ServiceException(ProjectConstants.PROJECT_MODULE_NAME, "0", "未找到周界主机");
            }

            List<TPerimeterIntrusionZoneBaseInfo> zoneInfos = perimeterIntrusionZoneBaseInfoService.lambdaQuery()
                    .in(TPerimeterIntrusionZoneBaseInfo::getPerimeterIntrusionHostId, hostIds)
                    .list();
            if (!CollectionUtils.isEmpty(zoneInfos)) {
                zoneInfos.forEach(zone -> zoneIds.add(zone.getZoneId()));
            }
        }

        if (CollectionUtils.isEmpty(zoneIds)) {
            throw new ServiceException(ProjectConstants.PROJECT_MODULE_NAME, "0", "请选择防区");
        }

        List<TPerimeterIntrusionZoneBaseInfo> zoneInfos = perimeterIntrusionZoneBaseInfoService.listByIds(zoneIds);
        Map<String, String> zoneIdMap = zoneInfos.stream().collect(Collectors.toMap(TPerimeterIntrusionZoneBaseInfo::getZoneCode, TPerimeterIntrusionZoneBaseInfo::getPerimeterIntrusionHostId, (v1, v2) -> v1));
        if (CollectionUtils.isEmpty(zoneInfos)) {
            throw new ServiceException(ProjectConstants.PROJECT_MODULE_NAME, "0", "未找到防区");
        }
        List<String> zoneCodes = zoneInfos.stream()
                .map(TPerimeterIntrusionZoneBaseInfo::getZoneCode)
                .collect(Collectors.toList());

        List<PerimeterIntrusionZone> zoneList = perimeterIntrusionClient.getZoneList();
        List<String> zoneCodeList = new ArrayList<>();
        List<String> ids = new ArrayList<>();
        for (PerimeterIntrusionZone zone : zoneList) {
            if (!zone.getDefenceState().equals(armedStatus) && zoneCodes.contains(zone.getCode())) {
                zoneCodeList.add(zone.getCode());
                ids.add(zone.getId());
            }
        }

        if (CollectionUtils.isNotEmpty(ids)) {
            List<PerimeterIntrusionArmZoneReq> reqList = new ArrayList<>();
            for (String id : ids) {
                TPerimeterIntrusionHostBaseInfo tPerimeterIntrusionHostBaseInfo = hostMap.get(zoneIdMap.get(id));
                if(ObjectUtil.isEmpty(tPerimeterIntrusionHostBaseInfo)){
                    continue;
                }
                boolean b = reqList.stream().anyMatch(r -> r.getIpAddress().equals(tPerimeterIntrusionHostBaseInfo.getIpAddress()));
                if(b){
                    for (PerimeterIntrusionArmZoneReq perimeterIntrusionArmZoneReq : reqList) {
                        if(tPerimeterIntrusionHostBaseInfo.getIpAddress().equals(perimeterIntrusionArmZoneReq.getIpAddress())){
                            perimeterIntrusionArmZoneReq.getRequest().getIds().add(id);
                        }
                    }
                }else{
                    PerimeterIntrusionArmZoneReq requ = new PerimeterIntrusionArmZoneReq();
                    PerimeterIntrusionArmZoneRequest req = new PerimeterIntrusionArmZoneRequest();
                    req.setIds(Collections.singletonList(id));
                    req.setDefenceState(request.getArmedStatus());
                    req.setReason(request.getReason());
                    req.setDisarmEndDate(new Date());
                    requ.setIpAddress(tPerimeterIntrusionHostBaseInfo.getIpAddress());
                    requ.setRequest(req);
                    reqList.add(requ);
                }
            }
            perimeterIntrusionClient.armZone(reqList);
        }

        if (CollectionUtils.isNotEmpty(zoneCodeList)) {
            List<String> zoneIdList = zoneInfos.stream().filter(zone -> zoneCodeList.contains(zone.getZoneCode()))
                    .map(TPerimeterIntrusionZoneBaseInfo::getZoneId).collect(Collectors.toList());
            String modifyUser = getCurrentUser();
            Date currentTime = new Date();
            List<TPerimeterIntrusionZoneStatusRecords> records = new ArrayList<>();
            for (String zoneId : zoneIdList) {
                TPerimeterIntrusionZoneStatusRecords record = new TPerimeterIntrusionZoneStatusRecords();
                record.setZoneId(zoneId);
                record.setArmedStatus(request.getArmedStatus());
                record.setModifyUser(modifyUser);
                record.setModifyTime(currentTime);
                records.add(record);
            }
            perimeterIntrusionZoneStatusRecordsService.saveBatch(records);
        }
        return true;
    }

    private String getCurrentUser() {
        // 获取当前登录用户
        String modifyUser = "system";
        try {
            LoginUser loginUser = LoginContext.me().getLoginUserNullable();
            if (loginUser != null) {
                modifyUser = loginUser.getAccount();
            }
        } catch (Exception e) {
            // 如果获取不到用户信息，使用默认值
        }
        return modifyUser;
    }

    /**
     * 大屏-周界主机防区查询（分页）
     *
     * @param request 查询请求参数
     * @return 主机和防区信息列表（第一条为主机信息，后续为防区信息）
     */
    public PageResult<HostZoneScreenResponse> getHostZoneList(HostZoneQueryRequest request) {
        if (StringUtils.isBlank(request.getStationId())) {
            throw new ServiceException(ProjectConstants.PROJECT_MODULE_NAME, "0", "站点ID不能为空");
        }

        // 查询主机信息
        List<TPerimeterIntrusionHostBaseInfo> hostInfos = perimeterIntrusionHostBaseInfoService.lambdaQuery()
                .eq(TPerimeterIntrusionHostBaseInfo::getBelongStationId, request.getStationId())
                .eq(StringUtils.isNotBlank(request.getHostId()), TPerimeterIntrusionHostBaseInfo::getDeviceId, request.getHostId())
                .list();
        if (CollectionUtils.isEmpty(hostInfos)) {
            return PageResultFactory.createPageResult(new ArrayList<>(), 0L, request.getPageSize(), request.getPageNo());
        }

        String pipelineName = "";
        String stationName = "";
        // 查询站场信息
        TStationBaseInfo stationInfo = stationBaseInfoService.getById(request.getStationId());
        if (stationInfo != null) {
            stationName = StringUtils.isNotBlank(stationInfo.getStationName()) ? stationInfo.getStationName() : "";
            // 查询管线信息
            TPipelineBaseInfo pipelineInfo = pipelineBaseInfoService.getById(stationInfo.getBelongPipeline());
            if (pipelineInfo != null) {
                pipelineName = pipelineInfo.getPipelineName();
            }
        }

        List<String> hostIdList = new ArrayList<>();
        List<String> areaIdList = new ArrayList<>();
        for (TPerimeterIntrusionHostBaseInfo hostInfo : hostInfos) {
            hostIdList.add(hostInfo.getDeviceId());
            areaIdList.add(hostInfo.getBelongStationAreaId());
        }
        List<TPerimeterIntrusionZoneBaseInfo> zoneList = perimeterIntrusionZoneBaseInfoService.lambdaQuery()
                .in(TPerimeterIntrusionZoneBaseInfo::getPerimeterIntrusionHostId, hostIdList)
                .list();

        // 查询防区状态
        Map<String, String> zoneStatusMap = new HashMap<>();
        List<PerimeterIntrusionZone> remoteZoneList = perimeterIntrusionClient.getZoneList();
        if (CollectionUtils.isNotEmpty(remoteZoneList)) {
            zoneStatusMap = remoteZoneList.stream()
                    .collect(Collectors.toMap(
                            PerimeterIntrusionZone::getCode,
                            PerimeterIntrusionZone::getDefenceState,
                            (existing, replacement) -> existing
                    ));
        }

        // 查询区域信息
        Map<String, String> areaNameMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(zoneList)) {
            areaIdList.addAll(zoneList.stream()
                    .map(TPerimeterIntrusionZoneBaseInfo::getBelongStationAreaId)
                    .filter(StringUtils::isNotBlank)
                    .distinct()
                    .collect(Collectors.toList()));
            if (CollectionUtils.isNotEmpty(areaIdList)) {
                List<TStationAreaBaseInfo> areaList = stationAreaBaseInfoService.listByIds(areaIdList);
                areaNameMap = areaList.stream()
                        .collect(Collectors.toMap(
                                TStationAreaBaseInfo::getAreaId,
                                TStationAreaBaseInfo::getAreaName,
                                (existing, replacement) -> existing
                        ));
            }
        }

        // 构建返回结果列表（先添加所有数据）
        List<HostZoneScreenResponse> resultList = new ArrayList<>();

        for (TPerimeterIntrusionHostBaseInfo hostInfo : hostInfos) {
            // 构建主机设备名称：所属管线名称-所属站-原设备名称
            String hostDeviceName = buildDeviceName(pipelineName, stationName, hostInfo.getDeviceName());
            // 第一条：主机信息
            HostZoneScreenResponse hostResponse = new HostZoneScreenResponse();
            hostResponse.setDeviceId(hostInfo.getDeviceId());
            hostResponse.setDeviceName(hostDeviceName);
            hostResponse.setDeviceType(hostInfo.getDeviceType());
            hostResponse.setAreaName(areaNameMap.get(hostInfo.getBelongStationAreaId()));
            hostResponse.setStatus(hostInfo.getStatus());
            resultList.add(hostResponse);
        }
        // 后续：防区信息
        if (CollectionUtils.isNotEmpty(zoneList)) {
            List<String> zoneIds = zoneList.stream().map(TPerimeterIntrusionZoneBaseInfo::getZoneId).collect(Collectors.toList());
            Map<String, List<TIndustrialTvBaseInfo>> map = new HashMap<>();
            zoneIds.forEach(zoneId -> {
                List<TDeviceRelationRecords> list = deviceRelationRecordsService.lambdaQuery().eq(TDeviceRelationRecords::getRelatedDeviceId, zoneId).list();
                if (CollectionUtils.isNotEmpty(list)) {
                    List<String> presetIds = list.stream().map(TDeviceRelationRecords::getPresetId).collect(Collectors.toList());
                    List<TIndustrialTvPreset> presetList = industrialTvPresetService.lambdaQuery().in(TIndustrialTvPreset::getPresetId, presetIds).list();
                    if (CollectionUtils.isNotEmpty(presetList)) {
                        List<String> tvIds = presetList.stream().map(TIndustrialTvPreset::getIndustrialTvId).collect(Collectors.toList());
                        List<TIndustrialTvBaseInfo> tvList = industrialTvBaseInfoService.lambdaQuery().in(TIndustrialTvBaseInfo::getDeviceId, tvIds).list();
                        map.put(zoneId, tvList);
                    }
                }
            });


            for (TPerimeterIntrusionZoneBaseInfo zone : zoneList) {
                String armedStatus = zoneStatusMap.getOrDefault(zone.getZoneCode(), "");
                if (StringUtils.isNotBlank(request.getArmedStatus()) && !armedStatus.equals(request.getArmedStatus())) {
                    continue;
                }

                HostZoneScreenResponse zoneResponse = new HostZoneScreenResponse();
                // 防区使用相同的设备名称格式
                zoneResponse.setDeviceId(zone.getZoneId());
                zoneResponse.setDeviceName(buildDeviceName(pipelineName, stationName, zone.getZoneName()));
                zoneResponse.setDeviceType(zone.getDeviceType());
                zoneResponse.setZoneCode(zone.getZoneCode());
                zoneResponse.setAreaName(areaNameMap.getOrDefault(zone.getBelongStationAreaId(), ""));
                zoneResponse.setStatus(armedStatus);
                zoneResponse.setZonePath(zone.getZonePath());
                zoneResponse.setZoneLocations(zone.getZoneLocations());
                zoneResponse.setTvList(map.get(zone.getZoneId()));
                resultList.add(zoneResponse);
            }
        }

        // 分页处理
        int pageNo = request.getPageNo() != null ? request.getPageNo() : 1;
        int pageSize = request.getPageSize() != null ? request.getPageSize() : 20;
        int total = resultList.size();
        int totalPage = (total + pageSize - 1) / pageSize;
        int start = (pageNo - 1) * pageSize;
        int end = Math.min(start + pageSize, total);
        
        // 根据页数和页大小截取数据
        List<HostZoneScreenResponse> pagedList = new ArrayList<>();
        if (start < total) {
            pagedList = resultList.subList(start, end);
        }

        // 构建分页结果
        PageResult<HostZoneScreenResponse> pageResult = new PageResult<>();
        pageResult.setPageNo(pageNo);
        pageResult.setPageSize(pageSize);
        pageResult.setTotalRows(total);
        pageResult.setTotalPage(totalPage);
        pageResult.setRows(pagedList);
        return pageResult;
    }

    /**
     * 构建设备名称：所属管线名称-所属站-原设备名称
     *
     * @param pipelineName 所属管线名称
     * @param stationName 所属站
     * @param originalDeviceName 原设备名称
     * @return 构建后的设备名称
     */
    private String buildDeviceName(String pipelineName, String stationName, String originalDeviceName) {
        List<String> parts = new ArrayList<>();
        if (StringUtils.isNotBlank(pipelineName)) {
            parts.add(pipelineName);
        }
        if (StringUtils.isNotBlank(stationName)) {
            parts.add(stationName);
        }
        if (StringUtils.isNotBlank(originalDeviceName)) {
            parts.add(originalDeviceName);
        }
        return String.join("-", parts);
    }

    /**
     * 大屏-防区详情查询
     *
     * @param request 查询请求参数
     * @return 防区详情信息
     */
    public ZoneDetailScreenResponse getZoneDetail(ZoneDetailQueryRequest request) {
        if (StringUtils.isBlank(request.getZoneId())) {
            throw new ServiceException(ProjectConstants.PROJECT_MODULE_NAME, "0", "防区ID不能为空");
        }

        // 查询防区信息
        TPerimeterIntrusionZoneBaseInfo zoneInfo = perimeterIntrusionZoneBaseInfoService.getById(request.getZoneId());
        if (zoneInfo == null) {
            throw new ServiceException(ProjectConstants.PROJECT_MODULE_NAME, "0", "未找到对应的防区信息，防区ID：" + request.getZoneId());
        }

        // 查询主机信息
        String hostName = "";
        if (StringUtils.isNotBlank(zoneInfo.getPerimeterIntrusionHostId())) {
            TPerimeterIntrusionHostBaseInfo hostInfo = perimeterIntrusionHostBaseInfoService.getById(zoneInfo.getPerimeterIntrusionHostId());
            if (hostInfo != null) {
                hostName = hostInfo.getDeviceName();
            }
        }

        // 查询防区状态
        List<PerimeterIntrusionZone> zoneList = perimeterIntrusionClient.getZoneList();
        String status = zoneList.stream().filter(zone -> zoneInfo.getZoneCode().equals(zone.getCode())).findFirst().map(PerimeterIntrusionZone::getDefenceState).orElse(null);

        // 查询设备关联关系，获取预设位ID列表
        List<String> presetIdList = new ArrayList<>();
        LambdaQueryWrapper<TDeviceRelationRecords> relationWrapper = new LambdaQueryWrapper<>();
        relationWrapper.eq(TDeviceRelationRecords::getRelatedDeviceId, request.getZoneId());
        List<TDeviceRelationRecords> relationList = deviceRelationRecordsService.list(relationWrapper);
        if (CollectionUtils.isNotEmpty(relationList)) {
            presetIdList = relationList.stream()
                    .map(TDeviceRelationRecords::getPresetId)
                    .filter(StringUtils::isNotBlank)
                    .distinct()
                    .collect(Collectors.toList());
        }

        // 构建返回对象
        ZoneDetailScreenResponse response = new ZoneDetailScreenResponse();
        response.setZoneName(zoneInfo.getZoneName());
        response.setHostName(hostName);
        response.setStatus(status);
        response.setLocationDesp(zoneInfo.getLocationDesp());
        response.setChannelId(zoneInfo.getChannelId());
        response.setStartLocation(zoneInfo.getStartLocation());
        response.setEndLocation(zoneInfo.getEndLocation());

        // 查询工业电视预设位信息
        if (CollectionUtils.isNotEmpty(presetIdList)) {
            List<TIndustrialTvPreset> presetList = industrialTvPresetService.listByIds(presetIdList);
            response.setPresetList(presetList);

            if (CollectionUtils.isNotEmpty(presetList)) {
                List<String> tvId = presetList.stream().map(TIndustrialTvPreset::getIndustrialTvId).distinct().collect(Collectors.toList());
                List<TIndustrialTvBaseInfo> tvBaseInfos = industrialTvBaseInfoService.listByIds(tvId);
                response.setTvList(tvBaseInfos);
            }
        }




        return response;
    }

    /**
     * 大屏-周界主机详情查询
     *
     * @param request 查询请求参数
     * @return 主机详情信息
     */
    public HostDetailScreenResponse getHostDetail(HostDetailQueryRequest request) {
        if (StringUtils.isBlank(request.getHostId())) {
            throw new ServiceException(ProjectConstants.PROJECT_MODULE_NAME, "0", "主机ID不能为空");
        }

        // 查询主机信息
        TPerimeterIntrusionHostBaseInfo hostInfo = perimeterIntrusionHostBaseInfoService.getById(request.getHostId());
        if (hostInfo == null) {
            throw new ServiceException(ProjectConstants.PROJECT_MODULE_NAME, "0", "未找到对应的主机信息，主机ID：" + request.getHostId());
        }

        // 查询站场区域信息
        String areaName = "";
        if (StringUtils.isNotBlank(hostInfo.getBelongStationAreaId())) {
            TStationAreaBaseInfo areaInfo = stationAreaBaseInfoService.getById(hostInfo.getBelongStationAreaId());
            if (areaInfo != null) {
                areaName = areaInfo.getAreaName();
            }
        }

        // 构建返回对象
        HostDetailScreenResponse response = new HostDetailScreenResponse();
        response.setDeviceName(hostInfo.getDeviceName());
        response.setDeviceCode(hostInfo.getDeviceCode());
        response.setDeviceType(hostInfo.getDeviceType());
        response.setIpAddress(hostInfo.getIpAddress());
        response.setPort(hostInfo.getPort());
        response.setBrand(hostInfo.getBrand());
        response.setModel(hostInfo.getModel());
        response.setAreaName(areaName);

        return response;
    }

    public HostStatusResponse getHostOnlineStatus(String stationId) {
        HostStatusResponse response = new HostStatusResponse();

        List<TPerimeterIntrusionHostBaseInfo> list = perimeterIntrusionHostBaseInfoService.lambdaQuery()
                .eq(TPerimeterIntrusionHostBaseInfo::getBelongStationId, stationId)
                .list();
        if (CollectionUtils.isEmpty(list)) {
            return response;
        }
        response.setTotalNum(list.size());
        response.setOnlineNum(Long.valueOf(list.stream().filter(host -> "1".equals(host.getStatus())).count()).intValue());
        return response;
    }

    /**
     * 新增周界入侵主机
     *
     * @param request 新增请求参数
     * @return 操作结果
     */
    public Boolean addHost(HostAddRequest request) {
        // 校验设备编码是否已存在
        LambdaQueryWrapper<TPerimeterIntrusionHostBaseInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TPerimeterIntrusionHostBaseInfo::getDeviceCode, request.getDeviceCode());
        long count = perimeterIntrusionHostBaseInfoService.count(queryWrapper);
        if (count > 0) {
            throw new ServiceException(ProjectConstants.PROJECT_MODULE_NAME, "0", "设备编码已存在：" + request.getDeviceCode());
        }

        // 校验所属站场是否存在
        if (StringUtils.isNotBlank(request.getBelongStationId())) {
            TStationBaseInfo stationInfo = stationBaseInfoService.getById(request.getBelongStationId());
            if (stationInfo == null) {
                throw new ServiceException(ProjectConstants.PROJECT_MODULE_NAME, "0", "所属站场不存在");
            }
        }

        // 校验所属站场区域是否存在
        if (StringUtils.isNotBlank(request.getBelongStationAreaId())) {
            TStationAreaBaseInfo areaInfo = stationAreaBaseInfoService.getById(request.getBelongStationAreaId());
            if (areaInfo == null) {
                throw new ServiceException(ProjectConstants.PROJECT_MODULE_NAME, "0", "所属站场区域不存在");
            }
        }

        // 构建实体对象
        TPerimeterIntrusionHostBaseInfo hostInfo = new TPerimeterIntrusionHostBaseInfo();
        hostInfo.setDeviceId(IdWorker.getIdStr());
        hostInfo.setDeviceCode(request.getDeviceCode());
        hostInfo.setDeviceName(request.getDeviceName());
        hostInfo.setBelongStationId(request.getBelongStationId());
        hostInfo.setBelongStationAreaId(request.getBelongStationAreaId());
        hostInfo.setStatus(StringUtils.isNotBlank(request.getStatus()) ? request.getStatus() : "0");
        hostInfo.setBrand(request.getBrand());
        hostInfo.setModel(request.getModel());
        hostInfo.setIpAddress(request.getIpAddress());
        hostInfo.setPort(request.getPort());
        hostInfo.setDeviceType(StringUtils.isNotBlank(request.getDeviceType()) ? request.getDeviceType() : "主机");
        hostInfo.setRemark(request.getRemark());

        // 保存
        boolean success = perimeterIntrusionHostBaseInfoService.save(hostInfo);
        if (success) {
            nodeReportService.onHostInfoChange(hostInfo, "A");
        }
        return success;
    }

    /**
     * 修改周界入侵主机
     *
     * @param request 修改请求参数
     * @return 操作结果
     */
    public Boolean updateHost(HostUpdateRequest request) {
        // 查询主机是否存在
        TPerimeterIntrusionHostBaseInfo hostInfo = perimeterIntrusionHostBaseInfoService.getById(request.getDeviceId());
        if (hostInfo == null) {
            throw new ServiceException(ProjectConstants.PROJECT_MODULE_NAME, "0", "主机不存在，设备ID：" + request.getDeviceId());
        }

        // 校验设备编码是否已被其他主机使用
        if (!hostInfo.getDeviceCode().equals(request.getDeviceCode())) {
            LambdaQueryWrapper<TPerimeterIntrusionHostBaseInfo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TPerimeterIntrusionHostBaseInfo::getDeviceCode, request.getDeviceCode())
                    .ne(TPerimeterIntrusionHostBaseInfo::getDeviceId, request.getDeviceId());
            long count = perimeterIntrusionHostBaseInfoService.count(queryWrapper);
            if (count > 0) {
                throw new ServiceException(ProjectConstants.PROJECT_MODULE_NAME, "0", "设备编码已被其他主机使用：" + request.getDeviceCode());
            }
        }

        // 校验所属站场是否存在
        if (StringUtils.isNotBlank(request.getBelongStationId())) {
            TStationBaseInfo stationInfo = stationBaseInfoService.getById(request.getBelongStationId());
            if (stationInfo == null) {
                throw new ServiceException(ProjectConstants.PROJECT_MODULE_NAME, "0", "所属站场不存在");
            }
        }

        // 校验所属站场区域是否存在
        if (StringUtils.isNotBlank(request.getBelongStationAreaId())) {
            TStationAreaBaseInfo areaInfo = stationAreaBaseInfoService.getById(request.getBelongStationAreaId());
            if (areaInfo == null) {
                throw new ServiceException(ProjectConstants.PROJECT_MODULE_NAME, "0", "所属站场区域不存在");
            }
        }

        // 更新实体对象
        hostInfo.setDeviceCode(request.getDeviceCode());
        hostInfo.setDeviceName(request.getDeviceName());
        hostInfo.setBelongStationId(request.getBelongStationId());
        hostInfo.setBelongStationAreaId(request.getBelongStationAreaId());
        hostInfo.setStatus(StringUtils.isNotBlank(request.getStatus()) ? request.getStatus() : hostInfo.getStatus());
        hostInfo.setBrand(request.getBrand());
        hostInfo.setModel(request.getModel());
        hostInfo.setIpAddress(request.getIpAddress());
        hostInfo.setPort(request.getPort());
        if (StringUtils.isNotBlank(request.getDeviceType())) {
            hostInfo.setDeviceType(request.getDeviceType());
        }
        hostInfo.setRemark(request.getRemark());

        // 更新
        boolean success = perimeterIntrusionHostBaseInfoService.updateById(hostInfo);
        if (success) {
            nodeReportService.onHostInfoChange(hostInfo, "U");
        }
        return success;
    }

    /**
     * 新增周界入侵防区
     *
     * @param request 新增请求参数
     * @return 操作结果
     */
    public Boolean addZone(ZoneAddRequest request) {
        // 校验防区编码是否已存在
        LambdaQueryWrapper<TPerimeterIntrusionZoneBaseInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TPerimeterIntrusionZoneBaseInfo::getZoneCode, request.getZoneCode());
        long count = perimeterIntrusionZoneBaseInfoService.count(queryWrapper);
        if (count > 0) {
            throw new ServiceException(ProjectConstants.PROJECT_MODULE_NAME, "0", "防区编码已存在：" + request.getZoneCode());
        }

        // 校验所属站场区域是否存在
        if (StringUtils.isNotBlank(request.getBelongStationAreaId())) {
            TStationAreaBaseInfo areaInfo = stationAreaBaseInfoService.getById(request.getBelongStationAreaId());
            if (areaInfo == null) {
                throw new ServiceException(ProjectConstants.PROJECT_MODULE_NAME, "0", "所属站场区域不存在");
            }
        }

        // 校验主机是否存在
        if (StringUtils.isNotBlank(request.getPerimeterIntrusionHostId())) {
            TPerimeterIntrusionHostBaseInfo hostInfo = perimeterIntrusionHostBaseInfoService.getById(request.getPerimeterIntrusionHostId());
            if (hostInfo == null) {
                throw new ServiceException(ProjectConstants.PROJECT_MODULE_NAME, "0", "周界入侵主机不存在");
            }
        }

        // 构建实体对象
        TPerimeterIntrusionZoneBaseInfo zoneInfo = new TPerimeterIntrusionZoneBaseInfo();
        zoneInfo.setZoneId(IdWorker.getIdStr());
        zoneInfo.setZoneCode(request.getZoneCode());
        zoneInfo.setZoneName(request.getZoneName());
        zoneInfo.setBelongStationAreaId(request.getBelongStationAreaId());
        zoneInfo.setPerimeterIntrusionHostId(request.getPerimeterIntrusionHostId());
        zoneInfo.setZonePath(request.getZonePath());
        zoneInfo.setLocationDesp(request.getLocationDesp());
        zoneInfo.setStartLocation(request.getStartLocation());
        zoneInfo.setEndLocation(request.getEndLocation());
        zoneInfo.setChannelId(request.getChannelId());
        zoneInfo.setDeviceType(request.getDeviceType());
        zoneInfo.setRemark(request.getRemark());

        // 保存
        boolean success = perimeterIntrusionZoneBaseInfoService.save(zoneInfo);
        if (success) {
            nodeReportService.onZoneInfoChange(zoneInfo, "A");
        }
        return success;
    }

    /**
     * 修改周界入侵防区
     *
     * @param request 修改请求参数
     * @return 操作结果
     */
    public Boolean updateZone(ZoneUpdateRequest request) {
        // 查询防区是否存在
        TPerimeterIntrusionZoneBaseInfo zoneInfo = perimeterIntrusionZoneBaseInfoService.getById(request.getZoneId());
        if (zoneInfo == null) {
            throw new ServiceException(ProjectConstants.PROJECT_MODULE_NAME, "0", "防区不存在，防区ID：" + request.getZoneId());
        }

        // 校验防区编码是否已被其他防区使用
        if (!zoneInfo.getZoneCode().equals(request.getZoneCode())) {
            LambdaQueryWrapper<TPerimeterIntrusionZoneBaseInfo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TPerimeterIntrusionZoneBaseInfo::getZoneCode, request.getZoneCode())
                    .ne(TPerimeterIntrusionZoneBaseInfo::getZoneId, request.getZoneId());
            long count = perimeterIntrusionZoneBaseInfoService.count(queryWrapper);
            if (count > 0) {
                throw new ServiceException(ProjectConstants.PROJECT_MODULE_NAME, "0", "防区编码已被其他防区使用：" + request.getZoneCode());
            }
        }

        // 校验所属站场区域是否存在
        if (StringUtils.isNotBlank(request.getBelongStationAreaId())) {
            TStationAreaBaseInfo areaInfo = stationAreaBaseInfoService.getById(request.getBelongStationAreaId());
            if (areaInfo == null) {
                throw new ServiceException(ProjectConstants.PROJECT_MODULE_NAME, "0", "所属站场区域不存在");
            }
        }

        // 校验主机是否存在
        if (StringUtils.isNotBlank(request.getPerimeterIntrusionHostId())) {
            TPerimeterIntrusionHostBaseInfo hostInfo = perimeterIntrusionHostBaseInfoService.getById(request.getPerimeterIntrusionHostId());
            if (hostInfo == null) {
                throw new ServiceException(ProjectConstants.PROJECT_MODULE_NAME, "0", "周界入侵主机不存在");
            }
        }

        // 更新实体对象
        zoneInfo.setZoneCode(request.getZoneCode());
        zoneInfo.setZoneName(request.getZoneName());
        zoneInfo.setBelongStationAreaId(request.getBelongStationAreaId());
        zoneInfo.setPerimeterIntrusionHostId(request.getPerimeterIntrusionHostId());
        zoneInfo.setZonePath(request.getZonePath());
        zoneInfo.setLocationDesp(request.getLocationDesp());
        zoneInfo.setStartLocation(request.getStartLocation());
        zoneInfo.setEndLocation(request.getEndLocation());
        zoneInfo.setChannelId(request.getChannelId());
        if (StringUtils.isNotBlank(request.getDeviceType())) {
            zoneInfo.setDeviceType(request.getDeviceType());
        }
        zoneInfo.setRemark(request.getRemark());

        // 更新
        boolean success = perimeterIntrusionZoneBaseInfoService.updateById(zoneInfo);
        if (success) {
            nodeReportService.onZoneInfoChange(zoneInfo, "U");
        }
        return success;
    }

    /**
     * 删除周界入侵主机
     *
     * @param deviceId 设备ID
     * @return 操作结果
     */
    public Boolean deleteHost(String deviceId) {
        if (StringUtils.isBlank(deviceId)) {
            throw new ServiceException(ProjectConstants.PROJECT_MODULE_NAME, "0", "设备ID不能为空");
        }

        // 查询主机是否存在
        TPerimeterIntrusionHostBaseInfo hostInfo = perimeterIntrusionHostBaseInfoService.getById(deviceId);
        if (hostInfo == null) {
            throw new ServiceException(ProjectConstants.PROJECT_MODULE_NAME, "0", "主机不存在，设备ID：" + deviceId);
        }

        // 检查是否有关联的防区
        LambdaQueryWrapper<TPerimeterIntrusionZoneBaseInfo> zoneWrapper = new LambdaQueryWrapper<>();
        zoneWrapper.eq(TPerimeterIntrusionZoneBaseInfo::getPerimeterIntrusionHostId, deviceId);
        long zoneCount = perimeterIntrusionZoneBaseInfoService.count(zoneWrapper);
        if (zoneCount > 0) {
            throw new ServiceException(ProjectConstants.PROJECT_MODULE_NAME, "0", "该主机下存在关联的防区，无法删除");
        }

        // 删除主机
        boolean success = perimeterIntrusionHostBaseInfoService.removeById(deviceId);
        if (success) {
            // 删除设备关联关系
            LambdaQueryWrapper<TDeviceRelationRecords> relationWrapper = new LambdaQueryWrapper<>();
            relationWrapper.eq(TDeviceRelationRecords::getRelatedDeviceId, deviceId);
            deviceRelationRecordsService.remove(relationWrapper);

            nodeReportService.onHostInfoChange(hostInfo, "D");
        }
        return success;
    }

    /**
     * 删除周界入侵防区
     *
     * @param zoneId 防区ID
     * @return 操作结果
     */
    public Boolean deleteZone(String zoneId) {
        if (StringUtils.isBlank(zoneId)) {
            throw new ServiceException(ProjectConstants.PROJECT_MODULE_NAME, "0", "防区ID不能为空");
        }

        // 查询防区是否存在
        TPerimeterIntrusionZoneBaseInfo zoneInfo = perimeterIntrusionZoneBaseInfoService.getById(zoneId);
        if (zoneInfo == null) {
            throw new ServiceException(ProjectConstants.PROJECT_MODULE_NAME, "0", "防区不存在，防区ID：" + zoneId);
        }

        // 删除防区状态记录
        LambdaQueryWrapper<TPerimeterIntrusionZoneStatusRecords> statusWrapper = new LambdaQueryWrapper<>();
        statusWrapper.eq(TPerimeterIntrusionZoneStatusRecords::getZoneId, zoneId);
        perimeterIntrusionZoneStatusRecordsService.remove(statusWrapper);

        // 删除设备关联关系
        LambdaQueryWrapper<TDeviceRelationRecords> relationWrapper = new LambdaQueryWrapper<>();
        relationWrapper.eq(TDeviceRelationRecords::getRelatedDeviceId, zoneId);
        deviceRelationRecordsService.remove(relationWrapper);

        // 删除防区
        boolean success = perimeterIntrusionZoneBaseInfoService.removeById(zoneId);
        if (success) {
            nodeReportService.onZoneInfoChange(zoneInfo, "D");
        }
        return success;
    }

    /**
     * 校验同一站场下设备编码的唯一性
     *
     * @param belongStationId 站场ID
     * @param deviceCode 设备编码
     * @param deviceId 设备ID（编辑时传入，用于排除自身；新增时传null）
     * @return true-唯一（可以使用），false-不唯一（已存在）
     */
    public boolean checkDeviceCodeUnique(String belongStationId, String deviceCode, String deviceIp, String deviceType, String deviceId) {
        if (StringUtils.isBlank(belongStationId) || (StringUtils.isBlank(deviceCode) && StringUtils.isBlank(deviceIp))) {
            return false;
        }

        // 获取或创建该站场的锁对象
        ReentrantLock lock = STATION_LOCKS.computeIfAbsent(belongStationId, k -> new ReentrantLock());

        lock.lock();
        try {
            long count = 0;
            if(DEVICE_TYPE_HOST.equals(deviceType)) {
                if(!StringUtils.isBlank(deviceCode)) {
                    // 查询该站场下是否存在相同设备编码的设备
                    LambdaQueryWrapper<TPerimeterIntrusionHostBaseInfo> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(TPerimeterIntrusionHostBaseInfo::getBelongStationId, belongStationId.trim())
                            .eq(TPerimeterIntrusionHostBaseInfo::getDeviceCode, deviceCode.trim());

                    // 编辑时排除自身
                    if (StringUtils.isNotBlank(deviceId)) {
                        queryWrapper.ne(TPerimeterIntrusionHostBaseInfo::getDeviceId, deviceId.trim());
                    }

                    count += perimeterIntrusionHostBaseInfoService.count(queryWrapper);
                }
                if (!StringUtils.isBlank(deviceIp)){
                    // 查询该站场下是否存在相同设备IP的设备
                    LambdaQueryWrapper<TPerimeterIntrusionHostBaseInfo> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(TPerimeterIntrusionHostBaseInfo::getBelongStationId, belongStationId.trim())
                            .eq(TPerimeterIntrusionHostBaseInfo::getIpAddress, deviceIp.trim());

                    // 编辑时排除自身
                    if (StringUtils.isNotBlank(deviceId)) {
                        queryWrapper.ne(TPerimeterIntrusionHostBaseInfo::getDeviceId, deviceId.trim());
                    }

                    count += perimeterIntrusionHostBaseInfoService.count(queryWrapper);
                }
            }else if(DEVICE_TYPE_ZONE.equals(deviceType)) {
                List<TStationAreaBaseInfo> stationAreaList = stationAreaBaseInfoService.lambdaQuery().eq(TStationAreaBaseInfo::getBelongStationId, belongStationId.trim()).list();
                Set<String> stationAreaIds = stationAreaList.stream().map(TStationAreaBaseInfo::getAreaId).collect(Collectors.toSet());
                if(CollectionUtils.isNotEmpty(stationAreaIds)) {
                    // 查询该站场下是否存在相同设备编码的设备
                    LambdaQueryWrapper<TPerimeterIntrusionZoneBaseInfo> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.in(TPerimeterIntrusionZoneBaseInfo::getBelongStationAreaId, stationAreaIds)
                            .eq(TPerimeterIntrusionZoneBaseInfo::getZoneCode, deviceCode.trim());

                    // 编辑时排除自身
                    if (StringUtils.isNotBlank(deviceId)) {
                        queryWrapper.ne(TPerimeterIntrusionZoneBaseInfo::getZoneId, deviceId.trim());
                    }

                    count = perimeterIntrusionZoneBaseInfoService.count(queryWrapper);
                }
            }
            // count为0表示唯一，返回true；否则返回false
            return count == 0;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 周界入侵联动报警
     *
     * 实现逻辑：
     * 1. 查询联动报警配置，判断是否需要联动摄像头和音频报警
     * 2. 根据设备关联关系表查询预设位ID和应急广播设备ID
     * 3. 如果有预设位，控制摄像头转到指定预设位
     * 4. 如果需要抓图，调用海康SDK进行抓图
     * 5. 如果需要播放音频，调用广播服务播放音频
     *
     * @param request 联动报警请求（周界入侵主机ID和报警类型编码）
     * @return 是否成功
     */
    public Boolean linkageAlarm(LinkageAlarmRequest request) {
        if (StringUtils.isBlank(request.getHostId())) {
            throw new ServiceException(ProjectConstants.PROJECT_MODULE_NAME, "0", "周界入侵主机ID不能为空");
        }
        if (StringUtils.isBlank(request.getAlarmType())) {
            throw new ServiceException(ProjectConstants.PROJECT_MODULE_NAME, "0", "报警类型编码不能为空");
        }

        // 1. 查询联动报警配置
        TLinkageAlarmConfig linkageConfig = tLinkageAlarmConfigService.lambdaQuery()
                .eq(TLinkageAlarmConfig::getSubsystemType, SystemTypeEnum.ZJRQ.getCode())
                .eq(TLinkageAlarmConfig::getAlarmType, request.getAlarmType())
                .eq(TLinkageAlarmConfig::getStatus, "1") // 开启状态
                .one();

        if (linkageConfig == null) {
            log.info("未找到联动报警配置，周界入侵主机ID: {}, 报警类型: {}", request.getHostId(), request.getAlarmType());
            return true;
        }

        // 2. 查询设备关联关系
        List<TDeviceRelationRecords> relationList = deviceRelationRecordsService.lambdaQuery()
                .eq(TDeviceRelationRecords::getRelatedDeviceId, request.getHostId())
                .eq(TDeviceRelationRecords::getSubsystemType, SystemTypeEnum.ZJRQ.getCode())
                .list();

        if (CollectionUtils.isEmpty(relationList)) {
            log.info("未找到设备关联关系，周界入侵主机ID: {}", request.getHostId());
            return true;
        }

        // 3. 处理预设位联动 - 控制摄像头转到指定预设位
        List<String> presetIds = relationList.stream()
                .map(TDeviceRelationRecords::getPresetId)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(presetIds)) {
            // 查询预设位对应的工业电视
            List<TIndustrialTvPreset> presetList = industrialTvPresetService.lambdaQuery()
                    .in(TIndustrialTvPreset::getPresetId, presetIds)
                    .list();

            // 按工业电视ID分组，每个工业电视取第一个预设位
            Map<String, TIndustrialTvPreset> tvPresetMap = presetList.stream()
                    .collect(Collectors.toMap(
                            TIndustrialTvPreset::getIndustrialTvId,
                            p -> p,
                            (p1, p2) -> p1 // 如果有重复，取第一个
                    ));

            // 控制摄像头转到预设位
            for (Map.Entry<String, TIndustrialTvPreset> entry : tvPresetMap.entrySet()) {
                String tvId = entry.getKey();
                String presetId = entry.getValue().getPresetId();

                try {
                    ControlPresetRequest presetRequest = new ControlPresetRequest();
                    presetRequest.setDeviceId(tvId);
                    presetRequest.setPresetId(presetId);
                    presetRequest.setCommand("goto");
                    industrialTVService.industrialTVControlPreset(presetRequest);
                    log.info("联动报警：控制摄像头转到预设位，工业电视ID: {}, 预设位ID: {}", tvId, presetId);
                } catch (Exception e) {
                    log.error("联动报警：控制摄像头转到预设位失败，工业电视ID: {}, 预设位ID: {}, 错误: {}", tvId, presetId, e.getMessage());
                }
            }

            // 4. 判断是否需要抓图
            if (Boolean.TRUE.equals(linkageConfig.getIsEnableSnapshot())) {
                for (String tvId : tvPresetMap.keySet()) {
                    try {
                        byte[] snapshot = hikVisionService.snapshot(tvId);
                        log.info("联动报警：抓图成功，工业电视ID: {}, 图片大小: {} bytes", tvId, snapshot != null ? snapshot.length : 0);
                    } catch (Exception e) {
                        log.error("联动报警：抓图失败，工业电视ID: {}, 错误: {}", tvId, e.getMessage());
                    }
                }
            }
        }

        // 5. 判断是否需要播放音频
        if (Boolean.TRUE.equals(linkageConfig.getIsPlayAudio()) && StringUtils.isNotBlank(linkageConfig.getAudioFileId())) {
            // 收集应急广播设备ID
            List<String> broadcastDeviceIds = relationList.stream()
                    .map(TDeviceRelationRecords::getEmergencyBroadcastId)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(broadcastDeviceIds)) {
                // 查询应急广播设备获取所属站场
                List<TEmergencyBroadcastHostBaseInfo> broadcastList = tEmergencyBroadcastHostBaseInfoService.lambdaQuery()
                        .in(TEmergencyBroadcastHostBaseInfo::getDeviceId, broadcastDeviceIds)
                        .list();

                if (CollectionUtils.isNotEmpty(broadcastList)) {
                    // 按站场分组播放
                    Map<String, List<TEmergencyBroadcastHostBaseInfo>> stationBroadcastMap = broadcastList.stream()
                            .filter(b -> StringUtils.isNotBlank(b.getBelongStationId()))
                            .collect(Collectors.groupingBy(TEmergencyBroadcastHostBaseInfo::getBelongStationId));

                    for (Map.Entry<String, List<TEmergencyBroadcastHostBaseInfo>> entry : stationBroadcastMap.entrySet()) {
                        String stationId = entry.getKey();
                        List<String> deviceIds = entry.getValue().stream()
                                .map(TEmergencyBroadcastHostBaseInfo::getDeviceId)
                                .collect(Collectors.toList());

                        try {
                            PlayVoiceRequest playVoiceRequest = new PlayVoiceRequest();
                            playVoiceRequest.setStationId(stationId);
                            playVoiceRequest.setDeviceIds(deviceIds);
                            playVoiceRequest.setVoiceId(linkageConfig.getAudioFileId());
                            broadcastService.playVoice(playVoiceRequest);
                            log.info("联动报警：播放音频成功，站场ID: {}, 音频文件ID: {}", stationId, linkageConfig.getAudioFileId());
                        } catch (Exception e) {
                            log.error("联动报警：播放音频失败，站场ID: {}, 错误: {}", stationId, e.getMessage());
                        }
                    }
                }
            }
        }

        // 6. 判断是否需要打开门禁
        if (Boolean.TRUE.equals(linkageConfig.getIsOpenAccessControl())) {
            // 收集门禁设备ID
            List<String> accessControlDeviceIds = relationList.stream()
                    .map(TDeviceRelationRecords::getAccessControlDeviceId)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(accessControlDeviceIds)) {
                try {
                    AccessControlGatewayRequest gatewayRequest = new AccessControlGatewayRequest();
                    gatewayRequest.setDeviceIds(accessControlDeviceIds);
                    gatewayRequest.setCommand(1); // 1-打开
                    accessControlGatewayService.remoteControlGate(gatewayRequest);
                    log.info("联动报警：打开门禁成功，门禁设备ID: {}", accessControlDeviceIds);
                } catch (Exception e) {
                    log.error("联动报警：打开门禁失败，错误: {}", e.getMessage());
                }
            }
        }

        return true;
    }
}
