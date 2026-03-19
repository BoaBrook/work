package cn.stylefeng.guns.modular.firegas.service;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.stylefeng.guns.core.consts.AlarmResultConstants;
import cn.stylefeng.guns.database.entity.TAlarmResultRecords;
import cn.stylefeng.guns.database.entity.TDeviceRelationRecords;
import cn.stylefeng.guns.database.entity.TFireGasHostBaseInfo;
import cn.stylefeng.guns.database.entity.TFireGasSensorBaseInfo;
import cn.stylefeng.guns.database.entity.TIndustrialTvPreset;
import cn.stylefeng.guns.database.entity.TLinkageAlarmConfig;
import cn.stylefeng.guns.database.entity.TStationAreaBaseInfo;
import cn.stylefeng.guns.database.entity.TStationBaseInfo;
import cn.stylefeng.guns.database.service.TAlarmResultRecordsService;
import cn.stylefeng.guns.database.service.TDeviceRelationRecordsService;
import cn.stylefeng.guns.database.service.TFireGasHostBaseInfoService;
import cn.stylefeng.guns.database.service.TFireGasSensorBaseInfoService;
import cn.stylefeng.guns.database.service.TIndustrialTvPresetService;
import cn.stylefeng.guns.database.service.TLinkageAlarmConfigService;
import cn.stylefeng.guns.database.service.TStationAreaBaseInfoService;
import cn.stylefeng.guns.database.service.TStationBaseInfoService;
import cn.stylefeng.guns.enums.SystemTypeEnum;
import cn.stylefeng.guns.modular.accesscontrol.request.AccessControlGatewayRequest;
import cn.stylefeng.guns.modular.accesscontrol.service.AccessControlGatewayService;
import cn.stylefeng.guns.modular.broadcast.request.PlayVoiceRequest;
import cn.stylefeng.guns.modular.broadcast.service.BroadcastService;
import cn.stylefeng.guns.modular.firegas.dto.FireGasAlarmQueryRequest;
import cn.stylefeng.guns.modular.hikvision.service.HikVisionService;
import cn.stylefeng.guns.modular.datimsien.websocketClient.dto.DatimsienWebSocketRecord;
import cn.stylefeng.guns.modular.datimsien.websocketClient.dto.DatimsienWebSocketRecord.Update;
import cn.stylefeng.guns.modular.industrialTV.request.ControlPresetRequest;
import cn.stylefeng.guns.modular.industrialTV.service.IndustrialTVService;
import cn.stylefeng.guns.modular.nodeSystem.constants.dict.DeviceTypeEnum;
import cn.stylefeng.guns.modular.nodeSystem.dto.AlarmRawDTO;
import cn.stylefeng.guns.modular.nodeSystem.service.NodeSystemService;
import cn.stylefeng.roses.kernel.db.api.factory.PageFactory;
import cn.stylefeng.roses.kernel.db.api.factory.PageResultFactory;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;

/**
 * 火气系统报警服务
 * 处理WebSocket消息，判断报警并保存到数据库
 * 
 * @author system
 */
@Slf4j
@Service
public class FireGasAlarmService {

    @Autowired
    private TFireGasHostBaseInfoService fireGasHostBaseInfoService;

    @Autowired
    private TFireGasSensorBaseInfoService fireGasSensorBaseInfoService;

    @Autowired
    private TStationAreaBaseInfoService stationAreaBaseInfoService;

    @Autowired
    private TStationBaseInfoService stationBaseInfoService;

    @Autowired
    private TAlarmResultRecordsService alarmResultRecordsService;

    @Autowired
    private NodeSystemService nodeSystemService;

    @Autowired
    private TLinkageAlarmConfigService linkageAlarmConfigService;

    @Autowired
    private TDeviceRelationRecordsService deviceRelationRecordsService;

    @Autowired
    private TIndustrialTvPresetService industrialTvPresetService;

    @Autowired
    private IndustrialTVService industrialTVService;

    @Autowired
    private HikVisionService hikVisionService;

    @Autowired
    private BroadcastService broadcastService;

    @Autowired
    private AccessControlGatewayService accessControlGatewayService;

    /**
     * 处理WebSocket消息
     * 判断value是否等于1，等于1就是报警
     * 
     * @param records WebSocket消息记录列表
     */
    public void processAlarm(List<DatimsienWebSocketRecord> records) {
        try {
            if (records == null || records.isEmpty()) {
                return;
            }
            
            // 批量查询所有相关的采集单元ID
            Set<String> unitIds = records.stream()
                    .map(r -> r.getUpdate().getUnitId())
                    .filter(unitId -> unitId != null && !unitId.trim().isEmpty())
                    .collect(Collectors.toSet());
            
            if (unitIds.isEmpty()) {
                log.warn("未找到有效的采集单元ID");
                return;
            }
            
            // 根据acq_unit_id查询主机设备
            List<TFireGasHostBaseInfo> hostList = fireGasHostBaseInfoService.list(
                    new LambdaQueryWrapper<TFireGasHostBaseInfo>()
                            .in(TFireGasHostBaseInfo::getAcqUnitId, unitIds)
            );
            
            if (hostList.isEmpty()) {
                log.warn("未找到对应的主机设备，unitIds: {}", unitIds);
                return;
            }
            
            // 创建unitId到主机设备的映射
            Map<String, List<TFireGasHostBaseInfo>> unitIdToHostMap = hostList.stream()
                    .collect(Collectors.groupingBy(TFireGasHostBaseInfo::getAcqUnitId));
            
            // 获取所有主机设备ID
            Set<String> hostIds = hostList.stream()
                    .map(TFireGasHostBaseInfo::getDeviceId)
                    .filter(hostId -> hostId != null && !hostId.trim().isEmpty())
                    .collect(Collectors.toSet());
            
            // 根据主机设备ID查询所有传感器设备（需要先检查hostIds是否为空）
            Map<String, List<TFireGasSensorBaseInfo>> hostIdToSensorMap = new HashMap<>();
            if (!hostIds.isEmpty()) {
                hostIdToSensorMap = fireGasSensorBaseInfoService.list(
                        new LambdaQueryWrapper<TFireGasSensorBaseInfo>()
                                .in(TFireGasSensorBaseInfo::getFireGasHostId, hostIds)
                ).stream()
                .collect(Collectors.groupingBy(TFireGasSensorBaseInfo::getFireGasHostId));
            }

            // 获取所有传感器，从传感器表获取区域ID
            List<TFireGasSensorBaseInfo> allSensors = new ArrayList<>();
            for (List<TFireGasSensorBaseInfo> sensors : hostIdToSensorMap.values()) {
                allSensors.addAll(sensors);
            }

            // 获取所有区域ID，从传感器表获取
            Set<String> areaIds = allSensors.stream()
                    .map(TFireGasSensorBaseInfo::getBelongStationAreaId)
                    .filter(areaId -> areaId != null && !areaId.trim().isEmpty())
                    .collect(Collectors.toSet());
            
            // 批量查询区域信息，创建areaId到areaName的映射
            Map<String, String> areaIdToNameMap = new HashMap<>();
            // 批量查询区域信息，创建areaId到area的映射
            Map<String, TStationAreaBaseInfo> areaIdToAreaMap = new HashMap<>();
            if (!areaIds.isEmpty()) {
                List<TStationAreaBaseInfo> areaList = stationAreaBaseInfoService.list(
                        new LambdaQueryWrapper<TStationAreaBaseInfo>()
                                .in(TStationAreaBaseInfo::getAreaId, areaIds)
                );
                areaIdToNameMap = areaList.stream()
                        .collect(Collectors.toMap(
                                TStationAreaBaseInfo::getAreaId,
                                TStationAreaBaseInfo::getAreaName,
                                (existing, replacement) -> existing
                        ));
                areaIdToAreaMap = areaList.stream()
                        .collect(Collectors.toMap(
                                TStationAreaBaseInfo::getAreaId,
                                area -> area,
                                (existing, replacement) -> existing
                        ));
            }
            
            // 批量查询站场信息，创建stationId到station的映射
            Set<String> stationIds = areaIdToAreaMap.values().stream()
                    .map(TStationAreaBaseInfo::getBelongStationId)
                    .filter(stationId -> stationId != null && !stationId.trim().isEmpty())
                    .collect(Collectors.toSet());
            
            Map<String, TStationBaseInfo> stationIdToStationMap = new HashMap<>();
            if (!stationIds.isEmpty()) {
                List<TStationBaseInfo> stationList = stationBaseInfoService.list(
                        new LambdaQueryWrapper<TStationBaseInfo>()
                                .in(TStationBaseInfo::getStationId, stationIds)
                );
                stationIdToStationMap = stationList.stream()
                        .collect(Collectors.toMap(
                                TStationBaseInfo::getStationId,
                                station -> station,
                                (existing, replacement) -> existing
                        ));
            }

            // 处理每条记录
            for (DatimsienWebSocketRecord record : records) {
                Update update = record.getUpdate();
                String unitId = update.getUnitId();
                String[] tags = update.getTags();
                Object[] values = update.getValues();
                Long alarmTime = update.getTime();

                // 获取该采集单元对应的主机设备列表
                List<TFireGasHostBaseInfo> hosts = unitIdToHostMap.get(unitId);
                if (hosts == null || hosts.isEmpty()) {
                    log.warn("未找到对应的主机设备，unitId: {}", unitId);
                    continue;
                }

                // 遍历每个主机设备
                for (TFireGasHostBaseInfo host : hosts) {
                    // 获取该主机下的所有传感器设备
                    List<TFireGasSensorBaseInfo> sensors = hostIdToSensorMap.get(host.getDeviceId());
                    if (sensors == null || sensors.isEmpty()) {
                        continue;
                    }

                    // 创建offset_address到传感器的映射
                    Map<String, TFireGasSensorBaseInfo> offsetToSensorMap = sensors.stream()
                            .filter(sensor -> sensor.getOffsetAddress() != null)
                            .collect(Collectors.toMap(
                                    TFireGasSensorBaseInfo::getOffsetAddress,
                                    sensor -> sensor,
                                    (existing, replacement) -> existing
                            ));

                    // 遍历每个tag和value
                    for (int i = 0; i < tags.length && i < values.length; i++) {
                        String tag = tags[i];
                        Object value = values[i];

                        // 根据tag判断是否为报警值
                        if (isAlarmValue(tag, value)) {
                            // 根据OFFSET_ADDRESS和tag比较，查找对应的传感器设备
                            TFireGasSensorBaseInfo sensor = offsetToSensorMap.get(tag);
                            if (sensor != null) {
                                // 获取站场信息
                                TStationBaseInfo station = null;
                                if (sensor.getBelongStationAreaId() != null) {
                                    TStationAreaBaseInfo area = areaIdToAreaMap.get(sensor.getBelongStationAreaId());
                                    if (area != null && area.getBelongStationId() != null) {
                                        station = stationIdToStationMap.get(area.getBelongStationId());
                                    }
                                }
                                // 创建报警记录（使用传感器的区域ID和设备ID）
                                createAlarmRecord(sensor, alarmTime, areaIdToNameMap, station);
                            } else {
                                log.warn("未找到对应的传感器设备，unitId: {}, hostId: {}, tag: {}", 
                                        unitId, host.getDeviceId(), tag);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("处理火气系统报警失败", e);
        }
    }

    /**
     * 判断值是否为报警值
     * 如果tag包含FA，判断值等于1就报警
     * 如果tag包含AT，判断值大于20报警
     * 
     * @param tag tag名称
     * @param value 值
     * @return 是否为报警值
     */
    private boolean isAlarmValue(String tag, Object value) {
        if (value == null || tag == null) {
            return false;
        }
        
        double doubleValue;
        // 支持多种数值类型判断
        if (value instanceof Number) {
            doubleValue = ((Number) value).doubleValue();
        } else if (value instanceof String) {
            try {
                doubleValue = Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                return false;
            }
        } else {
            return false;
        }
        
        // 如果tag包含FA，判断值等于1就报警
        if (tag.contains("FA")) {
            return doubleValue == 1.0;
        }
        
        // 如果tag包含AT，判断值大于20报警
        if (tag.contains("AT")) {
            return doubleValue > 20.0;
        }
        
        return false;
    }

    /**
     * 创建报警记录
     * 
     * @param sensor 传感器设备信息
     * @param alarmTime 报警时间（时间戳）
     * @param areaIdToNameMap 区域ID到区域名称的映射
     * @param station 站场信息
     */
    private void createAlarmRecord(TFireGasSensorBaseInfo sensor, Long alarmTime, Map<String, String> areaIdToNameMap, 
                                   TStationBaseInfo station) {
        try {
            // 从映射中获取区域名称（使用传感器的区域ID）
            String areaName = "";
            if (sensor.getBelongStationAreaId() != null) {
                areaName = areaIdToNameMap.getOrDefault(sensor.getBelongStationAreaId(), "");
            }
            
            // 创建新的报警记录
            TAlarmResultRecords alarmRecord = new TAlarmResultRecords();
            alarmRecord.setAlarmDeviceId(sensor.getDeviceId());
            alarmRecord.setAlarmDeviceName(sensor.getDeviceName());
            alarmRecord.setAlarmLocation(areaName);
            alarmRecord.setSubsystemType(SystemTypeEnum.HQXT.getCode());
            alarmRecord.setAlarmType("火灾");
            alarmRecord.setAlarmLevel("I");
            alarmRecord.setAlarmContent(alarmRecord.getAlarmType() + "-" + areaName + "-" + sensor.getDeviceName());
            alarmRecord.setAlarmTime(new Date(alarmTime));
            alarmRecord.setDisposalStatus(AlarmResultConstants.DISPOSAL_STATUS_UNDISPOSED);
            
            // 设置创建信息
            alarmRecord.setCreateTime(new Date());
            
            // 保存报警记录
            alarmResultRecordsService.save(alarmRecord);
            log.info("创建火气系统报警记录成功，sensorDeviceId: {}, areaName: {}, alarmId: {}",
                    sensor.getDeviceId(), areaName, alarmRecord.getAlarmId());
            
            // 调用sendAlarmRaw接口上报报警
            sendAlarmRaw(alarmRecord, station);

            // 根据联动报警配置执行联动动作（抓图、门禁、音频）
            handleLinkageOnNewAlarm(sensor, alarmRecord, station);
        } catch (Exception e) {
            log.error("创建火气系统报警记录失败，sensorDeviceId: {}", sensor.getDeviceId(), e);
        }
    }

    /**
     * 根据联动报警配置执行联动动作：
     * 1. 抓图：工业电视转预置位并抓图
     * 2. 门禁：预留门禁联动调用逻辑
     * 3. 音频：调用广播播放语音
     */
    private void handleLinkageOnNewAlarm(TFireGasSensorBaseInfo sensor,
                                         TAlarmResultRecords alarmRecord,
                                         TStationBaseInfo station) {
        try {
            if (station == null || station.getStationId() == null) {
                log.warn("联动报警处理失败：站场信息为空，alarmId={}", alarmRecord.getAlarmId());
                return;
            }

            String stationId = station.getStationId();
            String subsystemType = alarmRecord.getSubsystemType();
            String alarmType = alarmRecord.getAlarmType();
            String alarmLevel = alarmRecord.getAlarmLevel();

            // 查询联动报警配置：站场ID + 子系统类型 + 报警类型 + 报警等级 + 状态=1(开启)
            LambdaQueryWrapper<TLinkageAlarmConfig> configWrapper = new LambdaQueryWrapper<>();
            configWrapper.eq(TLinkageAlarmConfig::getBelongStationId, stationId)
                    .eq(TLinkageAlarmConfig::getSubsystemType, subsystemType)
                    .eq(TLinkageAlarmConfig::getAlarmType, alarmType)
                    .eq(TLinkageAlarmConfig::getAlarmLevel, alarmLevel)
                    .eq(TLinkageAlarmConfig::getStatus, "1");

            TLinkageAlarmConfig config = linkageAlarmConfigService.getOne(configWrapper, false);
            if (config == null) {
                log.info("未找到匹配的联动报警配置，stationId={}, subsystemType={}, alarmType={}, alarmLevel={}",
                        stationId, subsystemType, alarmType, alarmLevel);
                return;
            }

            // 查询设备关联关系：related_device_id = 火气传感器设备ID，subsystem_type = 火气系统
            LambdaQueryWrapper<TDeviceRelationRecords> relationWrapper = new LambdaQueryWrapper<>();
            relationWrapper.eq(TDeviceRelationRecords::getRelatedDeviceId, sensor.getDeviceId())
                    .eq(TDeviceRelationRecords::getSubsystemType, subsystemType);

            List<TDeviceRelationRecords> relationList = deviceRelationRecordsService.list(relationWrapper);
            if (relationList == null || relationList.isEmpty()) {
                log.info("未找到设备关联关系记录，无法执行联动动作，deviceId={}, subsystemType={}",
                        sensor.getDeviceId(), subsystemType);
                return;
            }

            // 处理抓图联动：通过预设位找到工业电视设备，先转预置位再抓图
            if (Boolean.TRUE.equals(config.getIsEnableSnapshot())) {
                try {
                    // 收集所有预设位ID
                    List<String> presetIds = relationList.stream()
                            .map(TDeviceRelationRecords::getPresetId)
                            .filter(id -> id != null && !id.trim().isEmpty())
                            .collect(Collectors.toList());

                    if (!presetIds.isEmpty()) {
                        List<TIndustrialTvPreset> presetList = industrialTvPresetService.list(
                                new LambdaQueryWrapper<TIndustrialTvPreset>()
                                        .in(TIndustrialTvPreset::getPresetId, presetIds)
                        );

                        // 预设位ID -> 工业电视设备ID
                        Map<String, String> presetIdToTvIdMap = presetList.stream()
                                .filter(preset -> preset.getIndustrialTvId() != null
                                        && !preset.getIndustrialTvId().trim().isEmpty())
                                .collect(Collectors.toMap(
                                        TIndustrialTvPreset::getPresetId,
                                        TIndustrialTvPreset::getIndustrialTvId,
                                        (existing, replacement) -> existing
                                ));

                        int maxSnapshotCount = config.getSnapshotCount() != null && config.getSnapshotCount() > 0
                                ? config.getSnapshotCount()
                                : Integer.MAX_VALUE;
                        int snapshotCounter = 0;

                        for (TDeviceRelationRecords relation : relationList) {
                            if (snapshotCounter >= maxSnapshotCount) {
                                break;
                            }
                            String presetId = relation.getPresetId();
                            if (presetId == null || presetId.trim().isEmpty()) {
                                continue;
                            }
                            String tvDeviceId = presetIdToTvIdMap.get(presetId);
                            if (tvDeviceId == null || tvDeviceId.trim().isEmpty()) {
                                continue;
                            }

                            // 工业电视转预置点
                            ControlPresetRequest presetRequest = new ControlPresetRequest();
                            presetRequest.setDeviceId(tvDeviceId);
                            presetRequest.setPresetId(presetId);
                            presetRequest.setCommand("goto");
                            Boolean presetResult = industrialTVService.industrialTVControlPreset(presetRequest);
                            log.info("联动报警-工业电视转预置位结果，alarmId={}, deviceId={}, presetId={}, result={}",
                                    alarmRecord.getAlarmId(), tvDeviceId, presetId, presetResult);

                            // 抓图
                            try {
                                byte[] snapshotBytes = hikVisionService.snapshot(tvDeviceId);
                                log.info("联动报警-工业电视抓图完成，alarmId={}, deviceId={}, snapshotSize={}",
                                        alarmRecord.getAlarmId(), tvDeviceId,
                                        snapshotBytes != null ? snapshotBytes.length : 0);
                            } catch (Exception ex) {
                                log.error("联动报警-工业电视抓图失败，alarmId={}, deviceId={}",
                                        alarmRecord.getAlarmId(), tvDeviceId, ex);
                            }

                            snapshotCounter++;
                        }
                    }
                } catch (Exception ex) {
                    log.error("联动报警-处理抓图联动失败，alarmId={}", alarmRecord.getAlarmId(), ex);
                }
            }

            // 处理门禁联动：根据配置判断是否打开门禁，这里预留实际门禁接口调用
            if (Boolean.TRUE.equals(config.getIsOpenAccessControl())) {
                try {
                    // 门禁联动：deviceIds= t_device_relation_records.access_control_device_id, command=1（打开）
                    List<String> accessControlDeviceIds = relationList.stream()
                            .map(TDeviceRelationRecords::getAccessControlDeviceId)
                            .filter(id -> id != null && !id.trim().isEmpty())
                            .distinct()
                            .collect(Collectors.toList());

                    if (accessControlDeviceIds.isEmpty()) {
                        log.info("联动报警-门禁联动未找到门禁设备，alarmId={}, stationId={}",
                                alarmRecord.getAlarmId(), stationId);
                    } else {
                        AccessControlGatewayRequest gatewayRequest = new AccessControlGatewayRequest();
                        gatewayRequest.setDeviceIds(accessControlDeviceIds);
                        gatewayRequest.setCommand(1);

                        boolean gateResult = accessControlGatewayService.remoteControlGate(gatewayRequest);
                        log.info("联动报警-门禁远程开门结果，alarmId={}, deviceIds={}, result={}",
                                alarmRecord.getAlarmId(), accessControlDeviceIds, gateResult);
                    }
                } catch (Exception ex) {
                    log.error("联动报警-门禁联动处理失败，alarmId={}", alarmRecord.getAlarmId(), ex);
                }
            }

            // 处理音频联动：调用应急广播播放语音
            if (Boolean.TRUE.equals(config.getIsPlayAudio())) {
                try {
                    List<String> emergencyBroadcastIds = relationList.stream()
                            .map(TDeviceRelationRecords::getEmergencyBroadcastId)
                            .filter(id -> id != null && !id.trim().isEmpty())
                            .distinct()
                            .collect(Collectors.toList());

                    if (!emergencyBroadcastIds.isEmpty() && config.getAudioFileId() != null
                            && !config.getAudioFileId().trim().isEmpty()) {
                        PlayVoiceRequest playVoiceRequest = new PlayVoiceRequest();
                        playVoiceRequest.setStationId(stationId);
                        playVoiceRequest.setDeviceIds(emergencyBroadcastIds);
                        playVoiceRequest.setVoiceId(config.getAudioFileId());

                        boolean playResult = broadcastService.playVoice(playVoiceRequest);
                        log.info("联动报警-播放语音完成，alarmId={}, stationId={}, voiceId={}, result={}",
                                alarmRecord.getAlarmId(), stationId, config.getAudioFileId(), playResult);
                    } else {
                        log.info("联动报警-音频联动配置不完整或未找到应急广播设备，alarmId={}", alarmRecord.getAlarmId());
                    }
                } catch (Exception ex) {
                    log.error("联动报警-播放语音失败，alarmId={}", alarmRecord.getAlarmId(), ex);
                }
            }
        } catch (Exception e) {
            log.error("联动报警处理异常，alarmId={}", alarmRecord.getAlarmId(), e);
        }
    }

    /**
     * 调用sendAlarmRaw接口上报报警
     * 
     * @param alarmRecord 报警记录
     * @param station 站场信息
     */
    private void sendAlarmRaw(TAlarmResultRecords alarmRecord, TStationBaseInfo station) {
        try {
            AlarmRawDTO alarmRaw = new AlarmRawDTO();
            
            // 告警唯一标识：节点编码+告警标识
            String nodeCode = nodeSystemService.getNodeCode();
            alarmRaw.setAlarmId(nodeCode + "_" + alarmRecord.getAlarmId());
            
            // 节点编码
            alarmRaw.setNodeCode(nodeCode);
            
            // 设备编码（使用传感器设备编码）
            alarmRaw.setDeviceCode(alarmRecord.getAlarmDeviceId());
            
            // 设备名称（按照格式组装：管道名称-站场名称-设备名称）
            String pipelineName = stationBaseInfoService.getBelongPipelineName(station);
            String stationName = station.getStationName();
            String deviceName = alarmRecord.getAlarmDeviceName();
            // 组装设备名称：管道名称-站场名称-设备名称
            String assembledDeviceName = String.format("%s-%s-%s", pipelineName, stationName, deviceName);
            alarmRaw.setDeviceName(assembledDeviceName);
            
            // 设备点位（位置）
            alarmRaw.setDeviceLocation(alarmRecord.getAlarmLocation());
            
            // 设备类型：火气系统
            alarmRaw.setDeviceType(DeviceTypeEnum.FIRE_GAS_SYSTEM.getCode());
            
            // 告警级别：1-I级
            alarmRaw.setAlarmLevel(1);
            
            // 告警类型
            alarmRaw.setAlarmType(alarmRecord.getAlarmType());
            
            // 告警内容
            alarmRaw.setAlarmContent(alarmRecord.getAlarmContent());
            
            // 告警时间（格式：yyyy-MM-dd HH:mm:ss）
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            alarmRaw.setAlarmTime(sdf.format(alarmRecord.getAlarmTime()));

            // 获取pipelineCode和workAreaCode
            String pipelineCode = stationBaseInfoService.getBelongPipelineCode(station);
            String workAreaCode = stationBaseInfoService.getBelongOperationAreaCode(station);
            // 设置管线编码和作业区编码
            alarmRaw.setPipelineCode(pipelineCode);
            alarmRaw.setWorkAreaCode(workAreaCode);
            
            // 发送报警
            boolean success = nodeSystemService.sendAlarmRaw(alarmRaw);
            if (success) {
                log.info("上报报警成功，alarmId: {}, pipelineCode: {}, workAreaCode: {}", 
                        alarmRaw.getAlarmId(), pipelineCode, workAreaCode);
            } else {
                log.warn("上报报警失败，alarmId: {}", alarmRaw.getAlarmId());
            }
        } catch (Exception e) {
            log.error("调用sendAlarmRaw接口失败，alarmId: {}", alarmRecord.getAlarmId(), e);
        }
    }

    /**
     * 分页查询火气系统的报警记录
     * 
     * @param request 查询请求参数
     * @return 分页结果
     */
    public PageResult<TAlarmResultRecords> getAlarmRecordsPage(FireGasAlarmQueryRequest request) {
        try {
            // 如果request为null，创建默认的request对象
            if (request == null) {
                request = new FireGasAlarmQueryRequest();
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
            
            // 如果systemType为空，默认使用火气系统类型
            String systemType = request.getSystemType();
            if (systemType == null || systemType.trim().isEmpty()) {
                systemType = SystemTypeEnum.HQXT.getCode();
            }
            
            // 构建查询条件
            LambdaQueryWrapper<TAlarmResultRecords> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TAlarmResultRecords::getSubsystemType, systemType);
            
            // 如果提供了stationId，需要通过设备ID关联查询
            String stationId = request.getStationId();
            if (stationId != null && !stationId.trim().isEmpty()) {
                // 1. 先查询该站场下的所有区域
                List<TStationAreaBaseInfo> areaList = stationAreaBaseInfoService.list(
                        new LambdaQueryWrapper<TStationAreaBaseInfo>()
                                .eq(TStationAreaBaseInfo::getBelongStationId, stationId)
                );
                
                if (areaList == null || areaList.isEmpty()) {
                    // 指定站场下没有区域，直接返回空分页结果
                    return PageResultFactory.createPageResult(new Page<>(request.getPageNo(), request.getPageSize(), 0));
                }
                
                // 2. 获取所有区域ID
                Set<String> areaIds = areaList.stream()
                        .map(TStationAreaBaseInfo::getAreaId)
                        .filter(areaId -> areaId != null && !areaId.trim().isEmpty())
                        .collect(Collectors.toSet());
                
                if (areaIds.isEmpty()) {
                    return PageResultFactory.createPageResult(new Page<>(request.getPageNo(), request.getPageSize(), 0));
                }
                
                // 3. 根据区域ID查询传感器设备，获取设备ID集合
                List<TFireGasSensorBaseInfo> sensors = fireGasSensorBaseInfoService.list(
                        new LambdaQueryWrapper<TFireGasSensorBaseInfo>()
                                .in(TFireGasSensorBaseInfo::getBelongStationAreaId, areaIds)
                );
                
                if (sensors == null || sensors.isEmpty()) {
                    // 该站场下没有传感器设备，直接返回空分页结果
                    return PageResultFactory.createPageResult(new Page<>(request.getPageNo(), request.getPageSize(), 0));
                }
                
                // 4. 获取所有设备ID
                Set<String> deviceIds = sensors.stream()
                        .map(TFireGasSensorBaseInfo::getDeviceId)
                        .filter(deviceId -> deviceId != null && !deviceId.trim().isEmpty())
                        .collect(Collectors.toSet());
                
                if (deviceIds.isEmpty()) {
                    return PageResultFactory.createPageResult(new Page<>(request.getPageNo(), request.getPageSize(), 0));
                }
                
                // 5. 在查询条件中添加设备ID过滤
                queryWrapper.in(TAlarmResultRecords::getAlarmDeviceId, deviceIds);
            }
            
            // 按报警时间倒序排列
            queryWrapper.orderByDesc(TAlarmResultRecords::getAlarmTime);
            
            // 分页查询报警记录
            Page<TAlarmResultRecords> pageable = PageFactory.defaultPage(request);
            Page<TAlarmResultRecords> alarmPage = alarmResultRecordsService.page(pageable, queryWrapper);
            
            if (alarmPage == null) {
                return PageResultFactory.createPageResult(new Page<>(request.getPageNo(), request.getPageSize(), 0));
            }
            
            log.info("分页查询报警记录成功，站场ID: {}, 系统类型: {}, 总数: {}, 当前页: {}, 每页大小: {}", 
                    stationId, systemType, alarmPage.getTotal(), alarmPage.getCurrent(), alarmPage.getSize());
            
            return PageResultFactory.createPageResult(alarmPage);
        } catch (Exception e) {
            log.error("分页查询报警记录失败，站场ID: {}, 系统类型: {}", request != null ? request.getStationId() : null, 
                    request != null ? request.getSystemType() : null, e);
            return new PageResult<>();
        }
    }
}
