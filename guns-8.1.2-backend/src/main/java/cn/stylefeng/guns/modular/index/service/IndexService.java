package cn.stylefeng.guns.modular.index.service;

import cn.stylefeng.guns.core.consts.AlarmResultConstants;
import cn.stylefeng.guns.core.utils.PageToPageResultUtils;
import cn.stylefeng.guns.core.utils.SpringContextHolder;
import cn.stylefeng.guns.database.entity.TAlarmResultRecords;
import cn.stylefeng.guns.database.entity.TStationAreaBaseInfo;
import cn.stylefeng.guns.database.entity.TStationBaseInfo;
import cn.stylefeng.guns.database.entity.TTagManagement;
import cn.stylefeng.guns.database.service.*;
import cn.stylefeng.guns.enums.SystemTypeEnum;
import cn.stylefeng.guns.modular.index.constants.DeviceStatusEnum;
import cn.stylefeng.guns.modular.index.request.AlarmDisposeRequest;
import cn.stylefeng.guns.modular.index.request.AlarmInfoRequest;
import cn.stylefeng.guns.modular.index.request.TagInfoRequest;
import cn.stylefeng.guns.modular.index.response.AlarmStatisticsResponse;
import cn.stylefeng.guns.modular.index.response.IndexAlarmStatisticsResponse;
import cn.stylefeng.guns.modular.nodeSystem.dto.AlarmRawHandleDTO;
import cn.stylefeng.guns.modular.nodeSystem.service.NodeSystemService;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.commons.collections.map.MultiKeyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class IndexService {

    @Autowired
    private TAlarmResultRecordsService tAlarmResultRecordsService;

    @Autowired
    private TTagManagementService tTagManagementService;
    
    @Autowired
    private TStationBaseInfoService tStationBaseInfoService;

    @Autowired
    private NodeSystemService nodeSystemService;

    @Autowired
    private TStationAreaBaseInfoService tStationAreaBaseInfoService;

    @Autowired
    private TModelMapManagementService tModelMapManagementService;

    public List<TTagManagement> getTagList(TagInfoRequest request){
        List<TTagManagement> tagList = tTagManagementService.lambdaQuery().eq(TTagManagement::getModelId, request.getModelId())
                .eq(StringUtils.isNotEmpty(request.getSystemType()), TTagManagement::getSubsystemType, request.getSystemType())
                .list();
        Map<String, List<TTagManagement>> tagMap = tagList.stream().collect(Collectors.groupingBy(TTagManagement::getSubsystemType));
        for (Map.Entry<String, List<TTagManagement>> tagEntry : tagMap.entrySet()) {
            Class<? extends IService> service = SystemTypeEnum.getServiceByCode(tagEntry.getKey());
            IService serviceImpl = SpringContextHolder.getBean(service);
            Set<String> deviceIdSet = tagEntry.getValue().stream().map(TTagManagement::getDeviceId).collect(Collectors.toSet());
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.in("device_id", deviceIdSet);
            List<Object> deviceList = serviceImpl.list(queryWrapper);
            List<JSONObject> deviceMap = deviceList.stream().map(obj -> JSONObject.parseObject(JSON.toJSONString(obj))).collect(Collectors.toList());
            tagEntry.getValue().forEach(tag -> {
                String state = "0";
                JSONObject device = deviceMap.stream().filter(obj -> obj.getString("deviceId").equals(tag.getDeviceId())).findFirst().orElse(null);
                if (device != null) {
                    if(device.containsKey("state")){
                        state = device.getString("state");
                    }
                    if(device.containsKey("status")){
                        state = device.getString("status");
                    }
                    if(device.containsKey("onlineStatus")){
                        state = device.getString("onlineStatus");
                    }
                    if(state.equals("0")){
                        tag.setStatus(DeviceStatusEnum.OFFLINE.getCode());
                    }else if(state.equals("1")){
                        tag.setStatus(DeviceStatusEnum.ONLINE.getCode());
                    }
                }
            });
        }
        // 判断报警状态
        LambdaQueryWrapper<TAlarmResultRecords> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ne(TAlarmResultRecords::getDisposalStatus, AlarmResultConstants.DISPOSAL_STATUS_UNDISPOSED);
        if (!tagMap.isEmpty()) {
            queryWrapper.and(wrapper -> {
                for (Map.Entry<String, List<TTagManagement>> entry : tagMap.entrySet()) {
                    List<String> deviceIdList = entry.getValue().stream()
                            .map(TTagManagement::getDeviceId)
                            .filter(StringUtils::isNotEmpty)
                            .collect(Collectors.toList());
                    if (!deviceIdList.isEmpty()) {
                        wrapper.or(subWrapper ->
                            subWrapper.eq(TAlarmResultRecords::getSubsystemType, entry.getKey())
                                     .in(TAlarmResultRecords::getAlarmDeviceId, deviceIdList)
                        );
                    }
                }
            });
        }
        List<TAlarmResultRecords> alarmRecords = tAlarmResultRecordsService.list(queryWrapper);
        Map<String, List<TAlarmResultRecords>> alarmDeviceMap = alarmRecords.stream().collect(Collectors.groupingBy(TAlarmResultRecords::getSubsystemType));
        tagList.forEach(tag -> {
            if(alarmDeviceMap.containsKey(tag.getSubsystemType())) {
                List<TAlarmResultRecords> alarmRecordsList = alarmDeviceMap.get(tag.getSubsystemType()).stream().filter(record -> record.getAlarmDeviceId().equals(tag.getDeviceId())).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(alarmRecordsList)) tag.setStatus(DeviceStatusEnum.ALARM.getCode());
            }
        });
        return tagList;
    }

    public PageResult<TAlarmResultRecords> getAlarmInfo(AlarmInfoRequest request) {
        Map<String, List<String>> deviceInfoMap = new HashMap<>();
        MultiKeyMap multiKeyMap = new MultiKeyMap();
        if(StringUtils.isNotEmpty(request.getStationId())){
            for (SystemTypeEnum systemType : SystemTypeEnum.values()) {
                Class<? extends IService> service = systemType.getService();
                IService serviceImpl = SpringContextHolder.getBean(service);
                QueryWrapper queryWrapper = new QueryWrapper();
                queryWrapper.eq("belong_station_id", request.getStationId());
                List<Object> list = serviceImpl.list(queryWrapper);
                List<String> deviceIds = list.stream()
                    .map(obj -> JSONObject.parseObject(JSON.toJSONString(obj)))
                    .peek(jsonObject -> {
                        MultiKey multiKey = new MultiKey(systemType.getCode(), jsonObject.getString("deviceId"));
                        multiKeyMap.put(multiKey, jsonObject.getString("deviceName"));
                    })
                    .map(jsonObject -> jsonObject.getString("deviceId"))
                    .filter(StringUtils::isNotEmpty)
                    .collect(Collectors.toList());
                deviceInfoMap.put(systemType.getCode(), deviceIds);
            }
        }
        Page<TAlarmResultRecords> page = Page.of(request.getPageNo(), request.getPageSize());
        LambdaQueryWrapper<TAlarmResultRecords> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(request.getSystemType())) {
            lambdaQueryWrapper.eq(TAlarmResultRecords::getSubsystemType, request.getSystemType());
        }
        if(StringUtils.isNotEmpty(request.getAlarmLevel())){
            lambdaQueryWrapper.eq(TAlarmResultRecords::getAlarmLevel, request.getAlarmLevel());
        }
        if(StringUtils.isNotEmpty(request.getDisposalStatus())){
            lambdaQueryWrapper.eq(TAlarmResultRecords::getDisposalStatus, request.getDisposalStatus());
        }
        if(request.getAlarmTimeStart() != null){
            lambdaQueryWrapper.ge(TAlarmResultRecords::getAlarmTime, request.getAlarmTimeStart());
        }
        if(request.getAlarmTimeEnd() != null){
            lambdaQueryWrapper.le(TAlarmResultRecords::getAlarmTime, request.getAlarmTimeEnd());
        }
        if(StringUtils.isNotEmpty(request.getStationId())){
            lambdaQueryWrapper.and(wrapper -> {
                for (int i = 0; i < deviceInfoMap.size(); i++) {
                    Map.Entry<String, List<String>> entry = (Map.Entry<String, List<String>>) deviceInfoMap.entrySet().toArray()[i];
                    if(CollectionUtils.isEmpty(entry.getValue())) continue;
                    wrapper.or(wq -> wq.eq(TAlarmResultRecords::getSubsystemType, entry.getKey())
                            .in(TAlarmResultRecords::getAlarmDeviceId, entry.getValue()));
                }
            });
        }
        lambdaQueryWrapper.orderByDesc(TAlarmResultRecords::getAlarmTime);
        Page<TAlarmResultRecords> result = tAlarmResultRecordsService.page(page, lambdaQueryWrapper);
        for (TAlarmResultRecords record : result.getRecords()) {
            record.setSubsystemTypeName(SystemTypeEnum.getDescriptionByCode(record.getSubsystemType()));
            record.setAlarmDeviceName((String) multiKeyMap.get(new MultiKey(record.getSubsystemType(), record.getAlarmDeviceId())));
        }
        return PageToPageResultUtils.pageToPageResult(result);
    }

    public PageResult getDeviceList(AlarmInfoRequest request){
        if(StringUtils.isEmpty(request.getSystemType())){
            throw new RuntimeException("系统类型不能为空");
        }
        Class<? extends IService> service = SystemTypeEnum.getServiceByCode(request.getSystemType());
        IService serviceImpl = SpringContextHolder.getBean(service);
        QueryWrapper queryWrapper = new QueryWrapper();
        if(StringUtils.isNotEmpty(request.getStationId())){
            queryWrapper.eq("belong_station_id", request.getStationId());
        }
        Page page = Page.of(request.getPageNo(), request.getPageSize());
        IPage result = serviceImpl.page(page, queryWrapper);
        List<Object> records = result.getRecords();
        Set<String> belongStationAreaIdSet = records.stream().map(obj -> JSONObject.parseObject(JSON.toJSONString(obj)))
                .map(jsonObject -> jsonObject.getString("belongStationAreaId"))
                .collect(Collectors.toSet());
        List<TStationAreaBaseInfo> stationAreaList = CollectionUtils.isEmpty(belongStationAreaIdSet) ? Collections.emptyList() : tStationAreaBaseInfoService.lambdaQuery().in(TStationAreaBaseInfo::getAreaId, belongStationAreaIdSet).list();
        Map<String, TStationAreaBaseInfo> stationAreaMap = stationAreaList.stream().collect(Collectors.toMap(TStationAreaBaseInfo::getAreaId, Function.identity()));
        for (Object record : result.getRecords()) {
            Class<?> clazz = record.getClass();
            try {
                Field belongStationAreaId = clazz.getDeclaredField("belongStationAreaId");
                belongStationAreaId.setAccessible(true);
                String areaId = belongStationAreaId.get(record).toString();
                if(stationAreaMap.containsKey(areaId)){
                    TStationAreaBaseInfo stationArea = stationAreaMap.get(areaId);
                    Field stationAreaNameField = clazz.getDeclaredField("areaName");
                    stationAreaNameField.setAccessible(true);
                    stationAreaNameField.set(record, stationArea.getAreaName());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return PageToPageResultUtils.pageToPageResult(result);
    }
    
    public TAlarmResultRecords getAlarmDetail(String alarmId) {
        TAlarmResultRecords alarmResultRecords = tAlarmResultRecordsService.getById(alarmId);
        Class<? extends IService> service = SystemTypeEnum.getServiceByCode(alarmResultRecords.getSubsystemType());
        IService serviceImpl = SpringContextHolder.getBean(service);
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("device_id", alarmResultRecords.getAlarmDeviceId());
        Object device = serviceImpl.getOne(queryWrapper);
        JSONObject deviceObject = JSONObject.parseObject(JSON.toJSONString(device));
        alarmResultRecords.setAlarmDeviceName(deviceObject.getString("deviceName"));
        String stationId = deviceObject.getString("belongStationId");
        TStationBaseInfo station = tStationBaseInfoService.getById(stationId);
        alarmResultRecords.setOperationAreaName(station.getBelongOperationArea());
        alarmResultRecords.setStationName(station.getStationName());
        alarmResultRecords.setPipelineName(station.getBelongPipeline());
        return alarmResultRecords;
    }

    @Transactional
    public boolean alarmDispose(AlarmDisposeRequest request){
        boolean dispose = tAlarmResultRecordsService.lambdaUpdate().in(TAlarmResultRecords::getAlarmId, request.getAlarmRecordIds())
                .set(TAlarmResultRecords::getResponseTime, new Date())
                .set(StringUtils.isNotEmpty(request.getProcessResult()), TAlarmResultRecords::getProcessResult, request.getProcessResult())
                .set(request.getProcessTime() != null, TAlarmResultRecords::getProcessTime, request.getProcessTime())
                .set(StringUtils.isNotEmpty(request.getProcessUser()), TAlarmResultRecords::getProcessUser, request.getProcessUser())
                .set(StringUtils.isNotEmpty(request.getProcessRemark()), TAlarmResultRecords::getProcessRemark, request.getProcessRemark())
                .set(TAlarmResultRecords::getDisposalStatus, AlarmResultConstants.DISPOSAL_STATUS_DISPOSED)
                .update();
//        sendAlarmRawHandle(request);
        return dispose;
    }

    public Long alarmTotalNum(String stationId){
        Map<String, List<String>> deviceInfoMap = new HashMap<>();
        MultiKeyMap multiKeyMap = new MultiKeyMap();
        if(StringUtils.isNotEmpty(stationId)){
            for (SystemTypeEnum systemType : SystemTypeEnum.values()) {
                Class<? extends IService> service = systemType.getService();
                IService serviceImpl = SpringContextHolder.getBean(service);
                QueryWrapper queryWrapper = new QueryWrapper();
                queryWrapper.eq("belong_station_id", stationId);
                List<Object> list = serviceImpl.list(queryWrapper);
                List<String> deviceIds = list.stream()
                        .map(obj -> JSONObject.parseObject(JSON.toJSONString(obj)))
                        .peek(jsonObject -> {
                            MultiKey multiKey = new MultiKey(systemType.getCode(), jsonObject.getString("deviceId"));
                            multiKeyMap.put(multiKey, jsonObject.getString("deviceName"));
                        })
                        .map(jsonObject -> jsonObject.getString("deviceId"))
                        .filter(StringUtils::isNotEmpty)
                        .collect(Collectors.toList());
                deviceInfoMap.put(systemType.getCode(), deviceIds);
            }
        }
        LambdaQueryWrapper<TAlarmResultRecords> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.ne(TAlarmResultRecords::getDisposalStatus, AlarmResultConstants.DISPOSAL_STATUS_DISPOSED);
        if(StringUtils.isNotEmpty(stationId)){
            lambdaQueryWrapper.and(wrapper -> {
                for (int i = 0; i < deviceInfoMap.size(); i++) {
                    Map.Entry<String, List<String>> entry = (Map.Entry<String, List<String>>) deviceInfoMap.entrySet().toArray()[i];
                    if(CollectionUtils.isEmpty(entry.getValue())) continue;
                    wrapper.or(wq -> wq.eq(TAlarmResultRecords::getSubsystemType, entry.getKey())
                            .in(TAlarmResultRecords::getAlarmDeviceId, entry.getValue()));
                }
            });
        }
        return tAlarmResultRecordsService.count(lambdaQueryWrapper);
    }

    public boolean alarmResponse(AlarmDisposeRequest request){
        return tAlarmResultRecordsService.lambdaUpdate().in(TAlarmResultRecords::getAlarmId, request.getAlarmRecordIds())
                .set(TAlarmResultRecords::getDisposalStatus, AlarmResultConstants.DISPOSAL_STATUS_RESPONDED)
                .set(TAlarmResultRecords::getResponseTime, new Date())
                .update();
    }

    public IndexAlarmStatisticsResponse alarmStatistics(String stationId){
        List<TAlarmResultRecords> recordList = tAlarmResultRecordsService.list();
        Integer unDisposedNum = 0;
        Integer disposedNum = 0;
        for (TAlarmResultRecords record : recordList) {
            if (StringUtils.isEmpty(record.getDisposalStatus()) || !AlarmResultConstants.DISPOSAL_STATUS_DISPOSED.equals(record.getDisposalStatus())) {
                unDisposedNum++;
            } else {
                disposedNum++;
            }
        }
        return new IndexAlarmStatisticsResponse(unDisposedNum, disposedNum);
    }

    /**
     * 报警统计
     *
     * @param systemType 系统类型
     * @return 报警统计数据
     */
    public AlarmStatisticsResponse getAlarmStatistics(String systemType) {
        AlarmStatisticsResponse response = new AlarmStatisticsResponse();

        // 获取当前日期和时间
        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime todayEnd = today.atTime(23, 59, 59);
        LocalDateTime monthStart = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime monthEnd = today.atTime(23, 59, 59);
        LocalDateTime yearStart = today.withDayOfYear(1).atStartOfDay();
        LocalDateTime yearEnd = today.atTime(23, 59, 59);

        // 转换为Date类型
        Date todayStartDate = Date.from(todayStart.atZone(ZoneId.systemDefault()).toInstant());
        Date todayEndDate = Date.from(todayEnd.atZone(ZoneId.systemDefault()).toInstant());
        Date monthStartDate = Date.from(monthStart.atZone(ZoneId.systemDefault()).toInstant());
        Date monthEndDate = Date.from(monthEnd.atZone(ZoneId.systemDefault()).toInstant());
        Date yearStartDate = Date.from(yearStart.atZone(ZoneId.systemDefault()).toInstant());
        Date yearEndDate = Date.from(yearEnd.atZone(ZoneId.systemDefault()).toInstant());

        // 构建基础查询条件
        LambdaQueryWrapper<TAlarmResultRecords> baseWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(systemType)) {
            baseWrapper.eq(TAlarmResultRecords::getSubsystemType, systemType);
        }

        // 查询当日的所有报警数据
        List<TAlarmResultRecords> todayAlarmList = tAlarmResultRecordsService.lambdaQuery()
                .eq(StringUtils.isNotBlank(systemType), TAlarmResultRecords::getSubsystemType, systemType)
                .ge(TAlarmResultRecords::getAlarmTime, todayStartDate)
                .le(TAlarmResultRecords::getAlarmTime, todayEndDate)
                .list();
        response.setTodayTotalCount((long) todayAlarmList.size());

        // 查询当月的所有报警数据
        List<TAlarmResultRecords> monthAlarmList = tAlarmResultRecordsService.lambdaQuery()
                .eq(StringUtils.isNotBlank(systemType), TAlarmResultRecords::getSubsystemType, systemType)
                .ge(TAlarmResultRecords::getAlarmTime, monthStartDate)
                .le(TAlarmResultRecords::getAlarmTime, monthEndDate)
                .list();
        response.setMonthTotalCount((long) monthAlarmList.size());

        // 查询当年的所有报警数据
        List<TAlarmResultRecords> yearAlarmList = tAlarmResultRecordsService.lambdaQuery()
                .eq(StringUtils.isNotBlank(systemType), TAlarmResultRecords::getSubsystemType, systemType)
                .ge(TAlarmResultRecords::getAlarmTime, yearStartDate)
                .le(TAlarmResultRecords::getAlarmTime, yearEndDate)
                .list();
        response.setYearTotalCount((long) yearAlarmList.size());

        // 基于当日数据统计II级和III级报警
        long todayLevel2Count = todayAlarmList.stream()
                .filter(TAlarmResultRecords::isLevel2)
                .count();
        response.setTodayLevel2Count(todayLevel2Count);

        long todayLevel3Count = todayAlarmList.stream()
                .filter(TAlarmResultRecords::isLevel3)
                .count();
        response.setTodayLevel3Count(todayLevel3Count);

        // 基于当月数据统计II级和III级报警
        long monthLevel2Count = monthAlarmList.stream()
                .filter(TAlarmResultRecords::isLevel2)
                .count();
        response.setMonthLevel2Count(monthLevel2Count);

        long monthLevel3Count = monthAlarmList.stream()
                .filter(TAlarmResultRecords::isLevel3)
                .count();
        response.setMonthLevel3Count(monthLevel3Count);

        // 基于当年数据统计II级和III级报警
        long yearLevel2Count = yearAlarmList.stream()
                .filter(TAlarmResultRecords::isLevel2)
                .count();
        response.setYearLevel2Count(yearLevel2Count);

        long yearLevel3Count = yearAlarmList.stream()
                .filter(TAlarmResultRecords::isLevel3)
                .count();
        response.setYearLevel3Count(yearLevel3Count);

        return response;
    }

    public List<TStationAreaBaseInfo> getAreaInfo(String stationId) {
        return tStationAreaBaseInfoService.lambdaQuery().eq(TStationAreaBaseInfo::getBelongStationId, stationId).list();
    }

    private void sendAlarmRawHandle(AlarmDisposeRequest request) {
        // 告警时间（格式：yyyy-MM-dd HH:mm:ss）
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String alarmTime = sdf.format(request.getProcessTime());
        AlarmRawHandleDTO alarmRawHandle = new AlarmRawHandleDTO();
        for (String alarmRecordId : request.getAlarmRecordIds()) {
            alarmRawHandle.setAlarmId(alarmRecordId);
            alarmRawHandle.setHandleContent(request.getProcessRemark());
            alarmRawHandle.setNodeCode(nodeSystemService.getNodeCode());
            alarmRawHandle.setHandleTime(alarmTime);
            alarmRawHandle.setHandler(request.getProcessUser());
            alarmRawHandle.setHandleStatus(2);
            nodeSystemService.sendAlarmRawHandle(alarmRawHandle);
        }
    }

}
