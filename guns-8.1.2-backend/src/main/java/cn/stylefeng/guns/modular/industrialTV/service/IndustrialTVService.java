package cn.stylefeng.guns.modular.industrialTV.service;

import cn.stylefeng.guns.core.utils.PageToPageResultUtils;
import cn.stylefeng.guns.database.entity.*;
import cn.stylefeng.guns.database.service.*;
import cn.stylefeng.guns.enums.SystemTypeEnum;
import cn.stylefeng.guns.liveGBS.LiveGBSService;
import cn.stylefeng.guns.modular.accesscontrol.request.AccessControlGatewayRequest;
import cn.stylefeng.guns.modular.accesscontrol.service.AccessControlGatewayService;
import cn.stylefeng.guns.modular.broadcast.request.PlayVoiceRequest;
import cn.stylefeng.guns.modular.broadcast.response.OnlineStatisticsResponse;
import cn.stylefeng.guns.modular.broadcast.service.BroadcastService;
import cn.stylefeng.guns.modular.hikvision.request.PresetRequest;
import cn.stylefeng.guns.modular.hikvision.request.PtzControlRequest;
import cn.stylefeng.guns.modular.hikvision.service.HikVisionService;
import cn.stylefeng.guns.modular.industrialTV.request.*;
import cn.stylefeng.guns.modular.industrialTV.response.ImportantAreaMonitorResponse;
import cn.stylefeng.guns.modular.industrialTV.response.ScreenMonitorResponse;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.stylefeng.guns.modular.industrialTV.service.IndustrialTVNodeSystemService.OPERATE_TYPE_ADD;

@Slf4j
@Service
public class IndustrialTVService {

    private final Map<String, Boolean> pollingStatusMap = new ConcurrentHashMap<>();

    @Autowired
    private TStationBaseInfoService tStationBaseInfoService;

    @Autowired
    private TStationAreaBaseInfoService tStationAreaBaseInfoService;

    @Autowired
    private TIndustrialTvBaseInfoService tIndustrialTvBaseInfoService;

    @Autowired
    private TIndustrialTvRollPolingService tIndustrialTvRollPolingService;

    @Autowired
    private TNvrBaseInfoService tNvrBaseInfoService;

    @Autowired
    private LiveGBSService liveGBSService;

    @Autowired
    private TIndustrialTvPresetService tIndustrialTvPresetService;

    @Autowired
    private HikVisionService hikVisionService;

    @Autowired
    private TLinkageAlarmConfigService tLinkageAlarmConfigService;

    @Autowired
    private TDeviceRelationRecordsService tDeviceRelationRecordsService;

    @Autowired
    private TEmergencyBroadcastHostBaseInfoService tEmergencyBroadcastHostBaseInfoService;

    @Autowired
    private BroadcastService broadcastService;

    @Autowired
    private AccessControlGatewayService accessControlGatewayService;

    @Autowired
    private TTagManagementService tTagManagementService;

    @Autowired
    private IndustrialTVNodeSystemService industrialTVNodeSystemService;

    public List<ScreenMonitorResponse> getRealTimeMonitor(String stationId,String deviceName){
        List<TStationBaseInfo> stationList = tStationBaseInfoService.lambdaQuery().eq(StringUtils.isNotBlank(stationId), TStationBaseInfo::getStationId, stationId).list();
        if(CollectionUtils.isEmpty(stationList)) return new ArrayList<>();
        List<TStationAreaBaseInfo> stationAreaList = tStationAreaBaseInfoService.lambdaQuery().in(TStationAreaBaseInfo::getBelongStationId, stationList.stream().map(TStationBaseInfo::getStationId).collect(Collectors.toList())).list();
        List<String> areaIdList = stationAreaList.stream().map(TStationAreaBaseInfo::getAreaId).collect(Collectors.toList());
        List<TIndustrialTvBaseInfo> industrialTvList = CollectionUtils.isEmpty(stationAreaList) ? new ArrayList<>() : tIndustrialTvBaseInfoService.lambdaQuery().in(TIndustrialTvBaseInfo::getBelongStationAreaId, areaIdList).like(StringUtils.isNotEmpty(deviceName), TIndustrialTvBaseInfo::getDeviceName, deviceName).list();
        List<ScreenMonitorResponse> result = new ArrayList<>();
        // 构建站场层级结构
        for (TStationBaseInfo station : stationList) {
            ScreenMonitorResponse stationResponse = new ScreenMonitorResponse();
            BeanUtils.copyProperties(station, stationResponse);

            // 查找属于当前站场的区域
            List<TStationAreaBaseInfo> currentStationAreas = stationAreaList.stream()
                    .filter(area -> station.getStationId().equals(area.getBelongStationId()))
                    .collect(Collectors.toList());

            List<ScreenMonitorResponse.StationAreaInfo> areaResponses = new ArrayList<>();
            for (TStationAreaBaseInfo area : currentStationAreas) {
                ScreenMonitorResponse.StationAreaInfo areaResponse = new ScreenMonitorResponse.StationAreaInfo();
                BeanUtils.copyProperties(area, areaResponse);

                // 查找属于当前区域的工业电视设备
                List<TIndustrialTvBaseInfo> currentAreaTvList = industrialTvList.stream()
                        .filter(tv -> area.getAreaId().equals(tv.getBelongStationAreaId()) && station.getStationId().equals(tv.getBelongStationId()))
                        .collect(Collectors.toList());
                areaResponse.setIndustrialTvBaseInfoList(currentAreaTvList);

                areaResponses.add(areaResponse);
            }

            stationResponse.setStationAreaInfoList(areaResponses);
            result.add(stationResponse);
        }
        return result;
    }

    public OnlineStatisticsResponse getOnlineStatistics(String stationId){
        List<TStationBaseInfo> stationList = tStationBaseInfoService.lambdaQuery().eq(StringUtils.isNotBlank(stationId), TStationBaseInfo::getStationId, stationId).list();
        if(CollectionUtils.isEmpty(stationList)) return new OnlineStatisticsResponse(0, 0);
        List<TStationAreaBaseInfo> stationAreaList = tStationAreaBaseInfoService.lambdaQuery().in(TStationAreaBaseInfo::getBelongStationId, stationList.stream().map(TStationBaseInfo::getStationId).collect(Collectors.toList())).list();
        List<String> areaIdList = stationAreaList.stream().map(TStationAreaBaseInfo::getAreaId).collect(Collectors.toList());
        List<TIndustrialTvBaseInfo> industrialTvList = CollectionUtils.isEmpty(stationAreaList) ? new ArrayList<>() : tIndustrialTvBaseInfoService.lambdaQuery().in(TIndustrialTvBaseInfo::getBelongStationAreaId, areaIdList).list();
        int totalNum = industrialTvList.size();
        int onlineNum = (int) industrialTvList.stream().filter(item -> StringUtils.isNotEmpty(item.getOnlineStatus()) && item.getOnlineStatus().equals("1")).count();
        return new OnlineStatisticsResponse(totalNum, onlineNum);
    }

    public PageResult<TIndustrialTvRollPoling> getPollingPlan(RollPolingRequest request){
        Page<TIndustrialTvRollPoling> page = new Page<>(request.getPageNo(), request.getPageSize());
        LambdaQueryWrapper<TIndustrialTvRollPoling> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotBlank(request.getStationId()), TIndustrialTvRollPoling::getStationId, request.getStationId());
        Page<TIndustrialTvRollPoling> res = tIndustrialTvRollPolingService.page(page, queryWrapper);
        Set<String> unitIdSet = res.getRecords().stream().map(TIndustrialTvRollPoling::getBelongUnit).collect(Collectors.toSet());
        if(CollectionUtils.isNotEmpty(unitIdSet)){
            List<TStationBaseInfo> stationBaseInfoList = tStationBaseInfoService.lambdaQuery().in(TStationBaseInfo::getStationId, unitIdSet).list();
            Map<String, TStationBaseInfo> stationBaseInfoMap = stationBaseInfoList.stream().collect(Collectors.toMap(TStationBaseInfo::getStationId, Function.identity()));
            res.getRecords().forEach(item -> {
                TStationBaseInfo stationBaseInfo = stationBaseInfoMap.get(item.getBelongUnit());
                if(stationBaseInfo != null){
                    item.setBelongUnitName(stationBaseInfo.getStationName());
                }
            });
        }
        return PageToPageResultUtils.pageToPageResult(res);
    }

    public boolean editPollingPlan(TIndustrialTvRollPoling request){
        if(StringUtils.isEmpty(request.getRollPolingId())){
            request.setRollPolingId(IdWorker.getIdStr());
            return tIndustrialTvRollPolingService.save(request);
        }else{
            return tIndustrialTvRollPolingService.updateById(request);
        }
    }

    public boolean deletePollingPlan(TIndustrialTvRollPoling request){
        return tIndustrialTvRollPolingService.removeById(request.getRollPolingId());
    }

    public boolean controlPollingPlan(PollingPlanControlRequest request){
        if(StringUtils.isBlank(request.getStationId())){
            throw new RuntimeException("站场ID不能为空");
        }
        if(StringUtils.isBlank(request.getCommand())){
            throw new RuntimeException("指令不能为空");
        }

        boolean isStart = "start".equalsIgnoreCase(request.getCommand());
        if(!isStart && !"stop".equalsIgnoreCase(request.getCommand())){
            throw new RuntimeException("无效的指令，指令应为start或stop");
        }

        pollingStatusMap.put(request.getStationId(), isStart);
        log.info("控制轮询计划: 站场ID={}, 指令={}, 当前状态={}", request.getStationId(), request.getCommand(), isStart);
        return true;
    }

    public Boolean pollingPlanStatus(String stationId){
        Boolean isPollingEnabled = pollingStatusMap.get(stationId);
        return isPollingEnabled != null && isPollingEnabled;
    }

    public ImportantAreaMonitorResponse getImportantAreaMonitor(String stationId){
        Boolean isPollingEnabled = pollingStatusMap.get(stationId);
        if(isPollingEnabled == null || !isPollingEnabled){
            return null;
        }

        List<TIndustrialTvRollPoling> list = tIndustrialTvRollPolingService.lambdaQuery()
                .eq(TIndustrialTvRollPoling::getStationId, stationId)
                .list();

        TIndustrialTvRollPoling tIndustrialTvRollPoling = list.stream().findFirst().orElse(null);
        if(tIndustrialTvRollPoling == null){
            return null;
        }

        ImportantAreaMonitorResponse response = new ImportantAreaMonitorResponse();
        BeanUtils.copyProperties(tIndustrialTvRollPoling, response);

        if(StringUtils.isNotBlank(tIndustrialTvRollPoling.getRelatedTv())){
            List<String> relatedTVIds = Arrays.stream(tIndustrialTvRollPoling.getRelatedTv().split(","))
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.toList());

            if(CollectionUtils.isNotEmpty(relatedTVIds)){
                List<TIndustrialTvBaseInfo> relatedTVList = tIndustrialTvBaseInfoService.lambdaQuery()
                        .in(TIndustrialTvBaseInfo::getDeviceId, relatedTVIds)
                        .list();
                response.setIndustrialTvBaseInfoList(relatedTVList);
            }
        }

        return response;
    }

    public List<TNvrBaseInfo> getDeviceHistoryVideo(DeviceHistoryVideoRequest request){
        return tNvrBaseInfoService.lambdaQuery().eq(TNvrBaseInfo::getDeviceId, request.getDeviceId())
                .ge(request.getStartTime() != null, TNvrBaseInfo::getCreateTime, request.getStartTime())
                .le(request.getEndTime() != null, TNvrBaseInfo::getCreateTime, request.getEndTime())
                .list();
    }

    public Boolean industrialTVControl(ControlPtzRequest request){
        TIndustrialTvBaseInfo camera = tIndustrialTvBaseInfoService.getById(request.getDeviceId());
        if(camera == null){
            throw new RuntimeException("工业电视不存在");
        }
//        ControlPtzRequestDTO controlPtzRequestDTO = new ControlPtzRequestDTO();
//        controlPtzRequestDTO.setSerial(camera.getGbCode());
//        controlPtzRequestDTO.setCode(camera.getStreamChannel());
//        controlPtzRequestDTO.setCommand(request.getCommand());
//        return liveGBSService.controlPtz(controlPtzRequestDTO);
        PtzControlRequest ptzControlRequest = new PtzControlRequest();
        ptzControlRequest.setDeviceId(camera.getDeviceId());
        ptzControlRequest.setCommand(request.getCommand());
        if(request.getCommand().equals("stop")){
            ptzControlRequest.setStop(1);
        }
        return hikVisionService.ptzControl(ptzControlRequest);
    }

    public Boolean industrialTVControlPreset(ControlPresetRequest request){
        TIndustrialTvBaseInfo camera = tIndustrialTvBaseInfoService.getById(request.getDeviceId());
        if(camera == null){
            throw new RuntimeException("工业电视不存在");
        }
//        ControlPresetRequestDTO controlPresetRequestDTO = new ControlPresetRequestDTO();
//        controlPresetRequestDTO.setSerial(camera.getGbCode());
//        controlPresetRequestDTO.setCode(camera.getStreamChannel());
//        controlPresetRequestDTO.setCommand(request.getCommand());
        TIndustrialTvPreset preset = tIndustrialTvPresetService.getById(request.getPresetId());
        if(preset == null){
            throw new RuntimeException("预置位不存在");
        }
//        controlPresetRequestDTO.setPreset(preset.getPresetCode());
//        return liveGBSService.controlPreset(controlPresetRequestDTO);
        PresetRequest presetRequest = new PresetRequest();
        presetRequest.setDeviceId(camera.getDeviceId());
        presetRequest.setPresetIndex(preset.getPresetCode());
        presetRequest.setPresetName(preset.getPresetName());
        switch (request.getCommand()) {
            case "set":
                return hikVisionService.setPreset(presetRequest);
            case "remove":
                return hikVisionService.removePreset(presetRequest);
            case "goto":
                return hikVisionService.gotoPreset(presetRequest);
        }
        return false;
    }

    /**
     * 按摄像头类型分组查询所有工业电视
     */
    public Map<String, List<TIndustrialTvBaseInfo>> getIndustrialTVGroupByCameraType(){
        List<TIndustrialTvBaseInfo> allList = tIndustrialTvBaseInfoService.list();
        if(CollectionUtils.isEmpty(allList)){
            return new HashMap<>();
        }
        Set<String> deviceIdSet = allList.stream().map(TIndustrialTvBaseInfo::getDeviceId).collect(Collectors.toSet());
        List<TTagManagement> tagList = tTagManagementService.lambdaQuery().in(TTagManagement::getDeviceId, deviceIdSet)
                .eq(TTagManagement::getSubsystemType, SystemTypeEnum.GYDS.getCode())
                .list();
        Map<String, TTagManagement> tagMap = tagList.stream().collect(Collectors.toMap(TTagManagement::getDeviceId, Function.identity()));
        allList.forEach(tv -> {
            TTagManagement tag = tagMap.get(tv.getDeviceId());
            if (tag != null) {
                tv.setTag(tag);
            }
        });
        return allList.stream()
                .filter(tv -> StringUtils.isNotBlank(tv.getCameraType()))
                .collect(Collectors.groupingBy(TIndustrialTvBaseInfo::getCameraType));
    }

    public boolean reportToProvince(){
        List<TIndustrialTvBaseInfo> list = tIndustrialTvBaseInfoService.list();
        return industrialTVNodeSystemService.sendDeviceInventory(list,OPERATE_TYPE_ADD);
    }

    /**
     * 工业电视联动报警
     *
     * 实现逻辑：
     * 1. 查询联动报警配置，判断是否需要联动摄像头和音频报警
     * 2. 根据设备关联关系表查询预设位ID和应急广播设备ID
     * 3. 如果有预设位，控制摄像头转到指定预设位
     * 4. 如果需要抓图，调用海康SDK进行抓图
     * 5. 如果需要播放音频，调用广播服务播放音频
     *
     * @param request 联动报警请求（工业电视ID和报警类型编码）
     * @return 是否成功
     */
    public Boolean linkageAlarm(LinkageAlarmRequest request) {
        if (StringUtils.isBlank(request.getIndustrialTvId())) {
            throw new RuntimeException("工业电视ID不能为空");
        }
        if (StringUtils.isBlank(request.getAlarmType())) {
            throw new RuntimeException("报警类型编码不能为空");
        }

        // 1. 查询联动报警配置
        TLinkageAlarmConfig linkageConfig = tLinkageAlarmConfigService.lambdaQuery()
                .eq(TLinkageAlarmConfig::getSubsystemType, SystemTypeEnum.GYDS.getCode())
                .eq(TLinkageAlarmConfig::getAlarmType, request.getAlarmType())
                .eq(TLinkageAlarmConfig::getStatus, "1") // 开启状态
                .one();

        if (linkageConfig == null) {
            log.info("未找到联动报警配置，工业电视ID: {}, 报警类型: {}", request.getIndustrialTvId(), request.getAlarmType());
            return true;
        }

        // 2. 查询设备关联关系
        List<TDeviceRelationRecords> relationList = tDeviceRelationRecordsService.lambdaQuery()
                .eq(TDeviceRelationRecords::getRelatedDeviceId, request.getIndustrialTvId())
                .eq(TDeviceRelationRecords::getSubsystemType, SystemTypeEnum.GYDS.getCode())
                .list();

        if (CollectionUtils.isEmpty(relationList)) {
            log.info("未找到设备关联关系，工业电视ID: {}", request.getIndustrialTvId());
            return true;
        }

        // 3. 处理预设位联动 - 控制摄像头转到指定预设位
        List<String> presetIds = relationList.stream()
                .map(TDeviceRelationRecords::getPresetId)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(presetIds)) {
            // 查询预设位对应的工业电视
            List<TIndustrialTvPreset> presetList = tIndustrialTvPresetService.lambdaQuery()
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
                    industrialTVControlPreset(presetRequest);
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
