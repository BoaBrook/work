package cn.stylefeng.guns.modular.broadcast.service;

import cn.stylefeng.guns.core.utils.PageToPageResultUtils;
import cn.stylefeng.guns.database.entity.*;
import cn.stylefeng.guns.database.service.*;
import cn.stylefeng.guns.modular.broadcast.remotePlayVoice.BroadcastPlayer;
import cn.stylefeng.guns.modular.broadcast.request.BroadcastHostRequest;
import cn.stylefeng.guns.modular.broadcast.request.PlayVoiceRequest;
import cn.stylefeng.guns.modular.broadcast.response.OnlineStatisticsResponse;
import cn.stylefeng.guns.modular.broadcast.response.ScreenBroadcastResponse;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class BroadcastService {

    /**
     * 站场级别的锁映射，用于校验设备编码唯一性时的并发控制
     * 按站场ID加锁，不同站场的校验可并行执行，同一站场的校验串行执行
     */
    private static final ConcurrentHashMap<String, ReentrantLock> STATION_LOCKS = new ConcurrentHashMap<>();

    @Autowired
    private TStationBaseInfoService tStationBaseInfoService;

    @Autowired
    private TStationAreaBaseInfoService tStationAreaBaseInfoService;

    @Autowired
    private TEmergencyBroadcastHostBaseInfoService tEmergencyBroadcastHostBaseInfoService;

    @Autowired
    private BroadcastPlayer broadcastPlayer;

    @Autowired
    private TWorkareaBaseInfoService tWorkareaBaseInfoService;

    @Autowired
    private TPipelineBaseInfoService tPipelineBaseInfoService;

    public List<ScreenBroadcastResponse> getScreenBroadCast(String stationId) {
        List<TStationBaseInfo> stationList = tStationBaseInfoService.lambdaQuery().eq(StringUtils.isNotBlank(stationId), TStationBaseInfo::getStationId, stationId).list();
        if(CollectionUtils.isEmpty(stationList)) return new ArrayList<>();
        List<TStationAreaBaseInfo> stationAreaList = tStationAreaBaseInfoService.lambdaQuery().in(TStationAreaBaseInfo::getBelongStationId, stationList.stream().map(TStationBaseInfo::getStationId).collect(Collectors.toList())).list();
        List<String> areaIdList = stationAreaList.stream().map(TStationAreaBaseInfo::getAreaId).collect(Collectors.toList());
        List<TEmergencyBroadcastHostBaseInfo> emergencyBroadcastHostBaseInfoList = CollectionUtils.isEmpty(stationAreaList) ? new ArrayList<>() : tEmergencyBroadcastHostBaseInfoService.lambdaQuery().in(TEmergencyBroadcastHostBaseInfo::getBelongStationAreaId, areaIdList).list();
        List<ScreenBroadcastResponse> result = new ArrayList<>();
        // 构建站场层级结构
        for (TStationBaseInfo station : stationList) {
            ScreenBroadcastResponse stationResponse = new ScreenBroadcastResponse();
            BeanUtils.copyProperties(station, stationResponse);

            // 查找属于当前站场的区域
            List<TStationAreaBaseInfo> currentStationAreas = stationAreaList.stream()
                    .filter(area -> station.getStationId().equals(area.getBelongStationId()))
                    .collect(Collectors.toList());

            List<ScreenBroadcastResponse.StationAreaInfo> areaResponses = new ArrayList<>();
            for (TStationAreaBaseInfo area : currentStationAreas) {
                ScreenBroadcastResponse.StationAreaInfo areaResponse = new ScreenBroadcastResponse.StationAreaInfo();
                BeanUtils.copyProperties(area, areaResponse);

                // 查找属于当前区域的工业电视设备
                List<TEmergencyBroadcastHostBaseInfo> currentAreaTvList = emergencyBroadcastHostBaseInfoList.stream()
                        .filter(tv -> area.getAreaId().equals(tv.getBelongStationAreaId()) && station.getStationId().equals(tv.getBelongStationId()))
                        .collect(Collectors.toList());
                areaResponse.setEmergencyBroadcastHostBaseInfoList(currentAreaTvList);

                areaResponses.add(areaResponse);
            }

            stationResponse.setStationAreaInfoList(areaResponses);
            result.add(stationResponse);
        }
        return result;
    }

    public boolean playVoice(PlayVoiceRequest request) throws IOException {
        List<TEmergencyBroadcastHostBaseInfo> broadcastList = tEmergencyBroadcastHostBaseInfoService.lambdaQuery().eq(TEmergencyBroadcastHostBaseInfo::getBelongStationId, request.getStationId())
                .in(TEmergencyBroadcastHostBaseInfo::getDeviceId, request.getDeviceIds())
                .list();
        if (CollectionUtils.isEmpty(broadcastList)) return false;
        try {
            return broadcastPlayer.play(broadcastList, request.getVoiceId());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public OnlineStatisticsResponse getScreenOnlineStatistics(String stationId){
        List<TStationBaseInfo> stationList = tStationBaseInfoService.lambdaQuery().eq(StringUtils.isNotBlank(stationId), TStationBaseInfo::getStationId, stationId).list();
        if(CollectionUtils.isEmpty(stationList)) return new OnlineStatisticsResponse(0, 0);
        List<TStationAreaBaseInfo> stationAreaList = tStationAreaBaseInfoService.lambdaQuery().in(TStationAreaBaseInfo::getBelongStationId, stationList.stream().map(TStationBaseInfo::getStationId).collect(Collectors.toList())).list();
        List<String> areaIdList = stationAreaList.stream().map(TStationAreaBaseInfo::getAreaId).collect(Collectors.toList());
        List<TEmergencyBroadcastHostBaseInfo> emergencyBroadcastHostBaseInfoList = CollectionUtils.isEmpty(stationAreaList) ? new ArrayList<>() : tEmergencyBroadcastHostBaseInfoService.lambdaQuery().in(TEmergencyBroadcastHostBaseInfo::getBelongStationAreaId, areaIdList).list();
        int totalNum = emergencyBroadcastHostBaseInfoList.size();
        int onlineNum = (int) emergencyBroadcastHostBaseInfoList.stream().filter(item -> StringUtils.isNotEmpty(item.getOnlineStatus()) && item.getOnlineStatus().equals("1")).count();
        return new OnlineStatisticsResponse(totalNum, onlineNum);
    }

    /**
     * 应急广播主机更新（新增/修改）
     */
    public Boolean updateBroadcastHost(TEmergencyBroadcastHostBaseInfo request) {
        if (StringUtils.isBlank(request.getDeviceId())) {
            // 新增
            return tEmergencyBroadcastHostBaseInfoService.save(request);
        } else {
            // 修改
            return tEmergencyBroadcastHostBaseInfoService.updateById(request);
        }
    }

    /**
     * 应急广播主机分页查询
     */
    public PageResult<TEmergencyBroadcastHostBaseInfo> getBroadcastHostList(BroadcastHostRequest request) {
        LambdaQueryWrapper<TEmergencyBroadcastHostBaseInfo> queryWrapper = new LambdaQueryWrapper<>();
        
        // 根据所属管线过滤（需要级联查询站场）
        Set<String> stationIdsByPipeline = null;
        if (StringUtils.isNotBlank(request.getPipelineId())) {
            List<TStationBaseInfo> stationsByPipeline = tStationBaseInfoService.lambdaQuery()
                    .eq(TStationBaseInfo::getBelongPipeline, request.getPipelineId())
                    .list();
            stationIdsByPipeline = stationsByPipeline.stream()
                    .map(TStationBaseInfo::getStationId)
                    .collect(Collectors.toSet());
            // 如果没有匹配的站场，返回空结果
            if (CollectionUtils.isEmpty(stationIdsByPipeline)) {
                Page<TEmergencyBroadcastHostBaseInfo> emptyPage = new Page<>(request.getPageNo(), request.getPageSize());
                emptyPage.setRecords(new ArrayList<>());
                emptyPage.setTotal(0);
                return PageToPageResultUtils.pageToPageResult(emptyPage);
            }
        }
        
        // 根据所属作业区过滤（需要级联查询站场）
        Set<String> stationIdsByWorkarea = null;
        if (StringUtils.isNotBlank(request.getWorkareaId())) {
            List<TStationBaseInfo> stationsByWorkarea = tStationBaseInfoService.lambdaQuery()
                    .eq(TStationBaseInfo::getBelongOperationArea, request.getWorkareaId())
                    .list();
            stationIdsByWorkarea = stationsByWorkarea.stream()
                    .map(TStationBaseInfo::getStationId)
                    .collect(Collectors.toSet());
            // 如果没有匹配的站场，返回空结果
            if (CollectionUtils.isEmpty(stationIdsByWorkarea)) {
                Page<TEmergencyBroadcastHostBaseInfo> emptyPage = new Page<>(request.getPageNo(), request.getPageSize());
                emptyPage.setRecords(new ArrayList<>());
                emptyPage.setTotal(0);
                return PageToPageResultUtils.pageToPageResult(emptyPage);
            }
        }
        
        // 计算最终的站场ID过滤条件
        Set<String> finalStationIds = null;
        if (stationIdsByPipeline != null && stationIdsByWorkarea != null) {
            // 取交集
            finalStationIds = stationIdsByPipeline.stream()
                    .filter(stationIdsByWorkarea::contains)
                    .collect(Collectors.toSet());
            if (CollectionUtils.isEmpty(finalStationIds)) {
                Page<TEmergencyBroadcastHostBaseInfo> emptyPage = new Page<>(request.getPageNo(), request.getPageSize());
                emptyPage.setRecords(new ArrayList<>());
                emptyPage.setTotal(0);
                return PageToPageResultUtils.pageToPageResult(emptyPage);
            }
        } else if (stationIdsByPipeline != null) {
            finalStationIds = stationIdsByPipeline;
        } else if (stationIdsByWorkarea != null) {
            finalStationIds = stationIdsByWorkarea;
        }
        
        // 构建查询条件
        queryWrapper.like(StringUtils.isNotBlank(request.getDeviceName()), TEmergencyBroadcastHostBaseInfo::getDeviceName, request.getDeviceName())
                .like(StringUtils.isNotBlank(request.getDeviceCode()), TEmergencyBroadcastHostBaseInfo::getDeviceCode, request.getDeviceCode())
                .eq(StringUtils.isNotBlank(request.getBelongStationId()), TEmergencyBroadcastHostBaseInfo::getBelongStationId, request.getBelongStationId());

        // 如果有级联过滤的站场ID列表
        if (finalStationIds != null) {
            queryWrapper.in(TEmergencyBroadcastHostBaseInfo::getBelongStationId, finalStationIds);
        }
        
        Page<TEmergencyBroadcastHostBaseInfo> page = new Page<>(request.getPageNo(), request.getPageSize());
        IPage<TEmergencyBroadcastHostBaseInfo> result = tEmergencyBroadcastHostBaseInfoService.page(page, queryWrapper);
        
        // 填充站场名称、区域名称、作业区名称
        if (CollectionUtils.isNotEmpty(result.getRecords())) {
            fillExtraInfo(result.getRecords());
        }
        
        return PageToPageResultUtils.pageToPageResult(result);
    }

    /**
     * 填充额外信息：站场名称、区域名称、作业区名称、管线名称
     */
    private void fillExtraInfo(List<TEmergencyBroadcastHostBaseInfo> records) {
        // 收集所有站场ID
        Set<String> stationIds = records.stream()
                .map(TEmergencyBroadcastHostBaseInfo::getBelongStationId)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());
        
        // 收集所有区域ID
        Set<String> areaIds = records.stream()
                .map(TEmergencyBroadcastHostBaseInfo::getBelongStationAreaId)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());
        
        // 查询站场信息
        Map<String, TStationBaseInfo> stationMap = new java.util.HashMap<>();
        Set<String> workareaIds = new java.util.HashSet<>();
        Set<String> pipelineIds = new java.util.HashSet<>();
        if (CollectionUtils.isNotEmpty(stationIds)) {
            List<TStationBaseInfo> stationList = tStationBaseInfoService.listByIds(stationIds);
            stationMap = stationList.stream().collect(Collectors.toMap(TStationBaseInfo::getStationId, Function.identity()));
            // 收集作业区ID
            workareaIds = stationList.stream()
                    .map(TStationBaseInfo::getBelongOperationArea)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.toSet());
            // 收集管线ID
            pipelineIds = stationList.stream()
                    .map(TStationBaseInfo::getBelongPipeline)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.toSet());
        }
        
        // 查询区域信息
        Map<String, TStationAreaBaseInfo> areaMap = new java.util.HashMap<>();
        if (CollectionUtils.isNotEmpty(areaIds)) {
            areaMap = tStationAreaBaseInfoService.listByIds(areaIds).stream().collect(Collectors.toMap(TStationAreaBaseInfo::getAreaId, Function.identity()));
        }
        
        // 查询作业区信息
        Map<String, TWorkareaBaseInfo> workareaMap = new java.util.HashMap<>();
        if (CollectionUtils.isNotEmpty(workareaIds)) {
            workareaMap = tWorkareaBaseInfoService.listByIds(workareaIds).stream().collect(Collectors.toMap(TWorkareaBaseInfo::getWorkareaId, Function.identity()));
        }
        
        // 查询管线信息
        Map<String, TPipelineBaseInfo> pipelineMap = new java.util.HashMap<>();
        if (CollectionUtils.isNotEmpty(pipelineIds)) {
            pipelineMap = tPipelineBaseInfoService.listByIds(pipelineIds).stream().collect(Collectors.toMap(TPipelineBaseInfo::getPipelineId, Function.identity()));
        }
        
        // 填充信息
        for (TEmergencyBroadcastHostBaseInfo record : records) {
            // 密码用非明文
            record.setPassword("******");
            // 填充站场名称
            TStationBaseInfo station = stationMap.get(record.getBelongStationId());
            if (station != null) {
                record.setStationName(station.getStationName());
                // 填充作业区名称
                TWorkareaBaseInfo workarea = workareaMap.get(station.getBelongOperationArea());
                if (workarea != null) {
                    record.setWorkAreaName(workarea.getWorkareaName());
                }
                // 填充管线名称
                TPipelineBaseInfo pipeline = pipelineMap.get(station.getBelongPipeline());
                if (pipeline != null) {
                    record.setPipelineName(pipeline.getPipelineName());
                }
            }
            // 填充区域名称
            TStationAreaBaseInfo area = areaMap.get(record.getBelongStationAreaId());
            if (area != null) {
                record.setAreaName(area.getAreaName());
            }
        }
    }

    /**
     * 应急广播主机批量删除
     */
    public Boolean batchDeleteBroadcastHost(List<String> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return true;
        }
        return tEmergencyBroadcastHostBaseInfoService.removeByIds(idList);
    }

    /**
     * 校验同一站场下设备编码的唯一性
     *
     * @param belongStationId 站场ID
     * @param deviceCode 设备编码
     * @param deviceId 设备ID（编辑时传入，用于排除自身；新增时传null）
     * @return true-唯一（可以使用），false-不唯一（已存在）
     */
    public boolean checkDeviceCodeUnique(String belongStationId, String deviceCode, String deviceIp, String deviceId) {
        if (StringUtils.isBlank(belongStationId) || (StringUtils.isBlank(deviceCode) && StringUtils.isBlank(deviceIp))) {
            return false;
        }

        // 获取或创建该站场的锁对象
        ReentrantLock lock = STATION_LOCKS.computeIfAbsent(belongStationId, k -> new ReentrantLock());

        lock.lock();
        try {
            long count = 0;
            if (!StringUtils.isBlank(deviceCode)) {
                // 查询该站场下是否存在相同设备编码的设备
                LambdaQueryWrapper<TEmergencyBroadcastHostBaseInfo> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(TEmergencyBroadcastHostBaseInfo::getBelongStationId, belongStationId.trim())
                        .eq(TEmergencyBroadcastHostBaseInfo::getDeviceCode, deviceCode.trim());

                // 编辑时排除自身
                if (StringUtils.isNotBlank(deviceId)) {
                    queryWrapper.ne(TEmergencyBroadcastHostBaseInfo::getDeviceId, deviceId.trim());
                }

                count += tEmergencyBroadcastHostBaseInfoService.count(queryWrapper);
            }
            if (!StringUtils.isBlank(deviceIp)) {
                // 查询该站场下是否存在相同设备IP的设备
                LambdaQueryWrapper<TEmergencyBroadcastHostBaseInfo> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(TEmergencyBroadcastHostBaseInfo::getBelongStationId, belongStationId.trim())
                        .eq(TEmergencyBroadcastHostBaseInfo::getIpAddress, deviceIp.trim());

                // 编辑时排除自身
                if (StringUtils.isNotBlank(deviceId)) {
                    queryWrapper.ne(TEmergencyBroadcastHostBaseInfo::getDeviceId, deviceId.trim());
                }

                count += tEmergencyBroadcastHostBaseInfoService.count(queryWrapper);
            }
            // count为0表示唯一，返回true；否则返回false
            return count == 0;
        } finally {
            lock.unlock();
        }
    }

}
