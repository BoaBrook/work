package cn.stylefeng.guns.modular.alarmrecord.service;

import cn.stylefeng.guns.core.utils.SpringContextHolder;
import cn.stylefeng.guns.database.entity.*;
import cn.stylefeng.guns.database.service.*;
import cn.stylefeng.guns.enums.SystemTypeEnum;
import cn.stylefeng.guns.modular.alarmrecord.dto.AlarmRecordBatchDisposalRequest;
import cn.stylefeng.guns.modular.alarmrecord.dto.AlarmRecordDisposalRequest;
import cn.stylefeng.guns.modular.alarmrecord.dto.AlarmRecordQueryRequest;
import cn.stylefeng.guns.modular.alarmrecord.dto.IndustrialTvWithVideoDTO;
import cn.stylefeng.guns.modular.industrialTV.service.VideoRecordService;
import cn.stylefeng.guns.modular.videoStreamMedia.dto.VideoFileDTO;
import cn.stylefeng.guns.zlmediakit.dto.ZlMediaCacheDTO;
import cn.stylefeng.roses.kernel.db.api.factory.PageFactory;
import cn.stylefeng.roses.kernel.db.api.factory.PageResultFactory;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 报警记录业务服务
 *
 * 提供分页查询以及单条、批量处置能力
 *
 * @author system
 */
@Slf4j
@Service
public class AlarmRecordService {

    @Resource
    private TAlarmResultRecordsService alarmResultRecordsService;

    @Resource
    private TStationBaseInfoService stationBaseInfoService;

    @Resource
    private TWorkareaBaseInfoService workareaBaseInfoService;

    @Resource
    private TPipelineBaseInfoService pipelineBaseInfoService;

    @Resource
    private TDeviceRelationRecordsService deviceRelationRecordsService;

    @Resource
    private TIndustrialTvPresetService industrialTvPresetService;

    @Resource
    private TIndustrialTvBaseInfoService industrialTvBaseInfoService;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private static final SimpleDateFormat ISO_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * 分页查询报警记录
     *
     * 查询条件：
     * 报警ID、报警类型、处置状态、报警开始时间、报警结束时间、关键字（报警内容、处理备注）
     */
    public PageResult<TAlarmResultRecords> page(AlarmRecordQueryRequest request) {
        if (request == null) {
            request = new AlarmRecordQueryRequest();
        }

        Page<TAlarmResultRecords> page = PageFactory.defaultPage(request);

        LambdaQueryWrapper<TAlarmResultRecords> wrapper = new LambdaQueryWrapper<>();

        // 报警ID
        if (request.getAlarmId() != null && !request.getAlarmId().trim().isEmpty()) {
            wrapper.eq(TAlarmResultRecords::getAlarmId, request.getAlarmId());
        }

        // 报警类型
        if (request.getAlarmType() != null && !request.getAlarmType().trim().isEmpty()) {
            wrapper.eq(TAlarmResultRecords::getAlarmType, request.getAlarmType());
        }

        // 处置状态
        if (request.getDisposalStatus() != null && !request.getDisposalStatus().trim().isEmpty()) {
            wrapper.eq(TAlarmResultRecords::getDisposalStatus, request.getDisposalStatus());
        }

        // 报警开始时间
        if (request.getAlarmStartTime() != null) {
            wrapper.ge(TAlarmResultRecords::getAlarmTime, request.getAlarmStartTime());
        }

        // 报警结束时间
        if (request.getAlarmEndTime() != null) {
            wrapper.le(TAlarmResultRecords::getAlarmTime, request.getAlarmEndTime());
        }

        // 关键字（报警内容、处理备注）
        if (request.getKeyword() != null && !request.getKeyword().trim().isEmpty()) {
            String keyword = "%" + request.getKeyword().trim() + "%";
            wrapper.and(w -> w.like(TAlarmResultRecords::getAlarmContent, keyword)
                    .or()
                    .like(TAlarmResultRecords::getProcessRemark, keyword));
        }

        // 按报警时间倒序
        wrapper.orderByDesc(TAlarmResultRecords::getAlarmTime);

        Page<TAlarmResultRecords> resultPage = alarmResultRecordsService.page(page, wrapper);

        // 填充设备名称、所属作业区、所属管线、所属站场等信息
        fillExtraInfo(resultPage.getRecords());

        return PageResultFactory.createPageResult(resultPage);
    }

    /**
     * 获取报警记录详情
     *
     * @param alarmId 报警ID
     * @return 报警记录详情
     */
    public TAlarmResultRecords detail(String alarmId) {
        if (alarmId == null || alarmId.trim().isEmpty()) {
            throw new RuntimeException("报警ID不能为空");
        }

        TAlarmResultRecords record = alarmResultRecordsService.getById(alarmId);
        if (record == null) {
            throw new RuntimeException("未找到对应的报警记录");
        }

        List<TAlarmResultRecords> records = new ArrayList<>();
        records.add(record);
        fillExtraInfo(records);

        return records.get(0);
    }

    /**
     * 单条报警处置
     * 
     * 处置状态固定设置为3，只有处置状态不为3的报警记录才可被处置
     */
    public void dispose(AlarmRecordDisposalRequest request) {
        TAlarmResultRecords record = alarmResultRecordsService.getById(request.getAlarmId());
        if (record == null) {
            throw new RuntimeException("报警处置失败，未找到记录");
        }

        // 只有处置状态不为3的报警记录才可被处置
        if ("3".equals(record.getDisposalStatus())) {
            throw new RuntimeException("报警记录已处置，无法重复处置");
        }
        
        // 处置状态固定设置为3
        record.setDisposalStatus("3");
        record.setProcessTime(request.getProcessTime());
        record.setProcessUser(request.getProcessUser());
        record.setProcessResult(request.getProcessResult());
        record.setProcessRemark(request.getProcessRemark());

        alarmResultRecordsService.updateById(record);
    }

    /**
     * 批量报警处置
     * 
     * 处置状态固定设置为3，只有处置状态不为3的报警记录才可被处置
     */
    public boolean batchDispose(AlarmRecordBatchDisposalRequest request) {
        List<TAlarmResultRecords> records = alarmResultRecordsService.listByIds(request.getAlarmIds());
        if (records == null || records.isEmpty()) {
            throw new RuntimeException("批量报警处置失败，未找到任何记录");
        }

        List<TAlarmResultRecords> toUpdate = new ArrayList<>();
        
        for (TAlarmResultRecords record : records) {
            if (record == null) {
                continue;
            }
            
            // 只有处置状态不为3的报警记录才可被处置
            if ("3".equals(record.getDisposalStatus())) {
                log.warn("报警记录已处置，跳过，alarmId={}", record.getAlarmId());
                continue;
            }
            
            // 处置状态固定设置为3
            record.setDisposalStatus("3");
            record.setProcessTime(request.getProcessTime());
            record.setProcessUser(request.getProcessUser());
            record.setProcessResult(request.getProcessResult());
            record.setProcessRemark(request.getProcessRemark());
            toUpdate.add(record);
        }

        if (!toUpdate.isEmpty()) {
            alarmResultRecordsService.updateBatchById(toUpdate);
        }
        return true;
    }

    /**
     * 为报警记录填充设备名称、所属作业区、所属管线、所属站场、子系统名称等扩展信息
     *
     * 依赖字段：alarmDeviceId、subsystemType
     */
    private void fillExtraInfo(List<TAlarmResultRecords> records) {
        if (records == null || records.isEmpty()) {
            return;
        }

        // 1. 按子系统类型分组，准备批量查询各子系统设备
        Map<String, List<TAlarmResultRecords>> systemTypeMap = new HashMap<>();
        for (TAlarmResultRecords record : records) {
            if (record == null) {
                continue;
            }
            String subsystemType = record.getSubsystemType();
            if (subsystemType == null || subsystemType.trim().isEmpty()) {
                continue;
            }
            systemTypeMap.computeIfAbsent(subsystemType, k -> new ArrayList<>()).add(record);
        }

        // 设备信息映射：key = subsystemType + "#" + deviceId
        Map<String, JSONObject> deviceInfoMap = new HashMap<>();
        // 站场信息去重集合
        Set<String> stationIdSet = new HashSet<>();

        // 2. 按子系统类型批量查询对应设备表
        for (Map.Entry<String, List<TAlarmResultRecords>> entry : systemTypeMap.entrySet()) {
            String subsystemType = entry.getKey();
            List<TAlarmResultRecords> subList = entry.getValue();

            SystemTypeEnum systemTypeEnum = SystemTypeEnum.getByCode(subsystemType);
            if (systemTypeEnum == null) {
                continue;
            }

            Class<? extends IService> serviceClass = systemTypeEnum.getService();
            if (serviceClass == null) {
                continue;
            }

            IService<?> serviceImpl = SpringContextHolder.getBean(serviceClass);
            if (serviceImpl == null) {
                continue;
            }

            // 收集该子系统下所有设备ID
            Set<String> deviceIds = new HashSet<>();
            for (TAlarmResultRecords record : subList) {
                String deviceId = record.getAlarmDeviceId();
                if (deviceId != null && !deviceId.trim().isEmpty()) {
                    deviceIds.add(deviceId);
                }
            }
            if (deviceIds.isEmpty()) {
                continue;
            }

            QueryWrapper queryWrapper = new QueryWrapper<>();
            queryWrapper.in("device_id", deviceIds);
            List<Object> deviceList = serviceImpl.list(queryWrapper);

            for (Object device : deviceList) {
                if (device == null) {
                    continue;
                }
                JSONObject deviceJson = JSONObject.parseObject(JSON.toJSONString(device));
                String deviceId = deviceJson.getString("deviceId");
                if (deviceId == null || deviceId.trim().isEmpty()) {
                    continue;
                }
                String key = subsystemType + "#" + deviceId;
                deviceInfoMap.put(key, deviceJson);

                String stationId = deviceJson.getString("belongStationId");
                if (stationId != null && !stationId.trim().isEmpty()) {
                    stationIdSet.add(stationId);
                }
            }
        }

        // 3. 批量查询站场信息
        Map<String, TStationBaseInfo> stationMap = new HashMap<>();
        if (!stationIdSet.isEmpty()) {
            List<TStationBaseInfo> stationList = stationBaseInfoService.listByIds(stationIdSet);
            for (TStationBaseInfo station : stationList) {
                if (station != null && station.getStationId() != null) {
                    stationMap.put(station.getStationId(), station);
                }
            }
        }

        // 3.1 查询所有作业区和管线
        Map<String, TWorkareaBaseInfo> workareaMap = new HashMap<>();
        List<TWorkareaBaseInfo> workareaList = workareaBaseInfoService.list();
        for (TWorkareaBaseInfo workarea : workareaList) {
            if (workarea != null && workarea.getWorkareaId() != null) {
                workareaMap.put(workarea.getWorkareaId(), workarea);
            }
        }

        Map<String, TPipelineBaseInfo> pipelineMap = new HashMap<>();
        List<TPipelineBaseInfo> pipelineList = pipelineBaseInfoService.list();
        for (TPipelineBaseInfo pipeline : pipelineList) {
            if (pipeline != null && pipeline.getPipelineId() != null) {
                pipelineMap.put(pipeline.getPipelineId(), pipeline);
            }
        }

        // 4. 回填到报警记录对象
        for (TAlarmResultRecords record : records) {
            if (record == null) {
                continue;
            }

            // 子系统类型名称
            String subsystemType = record.getSubsystemType();
            if (subsystemType != null && !subsystemType.trim().isEmpty()) {
                record.setSubsystemTypeName(SystemTypeEnum.getDescriptionByCode(subsystemType));
            }

            String deviceId = record.getAlarmDeviceId();
            if (deviceId == null || deviceId.trim().isEmpty() || subsystemType == null) {
                continue;
            }

            String key = subsystemType + "#" + deviceId;
            JSONObject deviceJson = deviceInfoMap.get(key);
            if (deviceJson == null) {
                continue;
            }

            // 设备名称
            record.setAlarmDeviceName(deviceJson.getString("deviceName"));

            // 所属站场及作业区、管线（通过站场表的ID，再查作业区/管线表名称）
            String stationId = deviceJson.getString("belongStationId");
            if (stationId != null && !stationId.trim().isEmpty()) {
                TStationBaseInfo station = stationMap.get(stationId);
                if (station != null) {
                    record.setStationName(station.getStationName());

                    // 作业区名称
                    String workareaId = station.getBelongOperationArea();
                    if (workareaId != null && !workareaId.trim().isEmpty()) {
                        TWorkareaBaseInfo workarea = workareaMap.get(workareaId);
                        if (workarea != null) {
                            record.setOperationAreaName(workarea.getWorkareaName());
                        }
                    }

                    // 管线名称
                    String pipelineId = station.getBelongPipeline();
                    if (pipelineId != null && !pipelineId.trim().isEmpty()) {
                        TPipelineBaseInfo pipeline = pipelineMap.get(pipelineId);
                        if (pipeline != null) {
                            record.setPipelineName(pipeline.getPipelineName());
                        }
                    }
                }
            }
        }
    }

    /**
     * 根据报警记录ID查询关联的工业电视摄像头列表（包含对应的录像信息）
     *
     * 实现逻辑：
     * 1. 查询报警记录，获取alarm_device_id、subsystem_type和alarm_time
     * 2. 如果报警设备本身就是工业电视（subsystem_type=GYDS），直接返回该工业电视
     * 3. 否则，通过t_device_relation_records表查询关联的预设位，再通过预设位找到对应的工业电视
     * 4. 对每个工业电视，查询报警日期的录像列表，根据lastModified匹配报警时间对应的录像
     *
     * @param alarmId 报警记录ID
     * @return 关联的工业电视列表（包含录像信息）
     */
    public List<IndustrialTvWithVideoDTO> getRelatedIndustrialTv(String alarmId) {
        if (alarmId == null || alarmId.trim().isEmpty()) {
            throw new RuntimeException("报警ID不能为空");
        }

        // 1. 查询报警记录
        TAlarmResultRecords alarmRecord = alarmResultRecordsService.getById(alarmId);
        if (alarmRecord == null) {
            throw new RuntimeException("未找到对应的报警记录");
        }

        String alarmDeviceId = alarmRecord.getAlarmDeviceId();
        String subsystemType = alarmRecord.getSubsystemType();
        Date alarmTime = alarmRecord.getAlarmTime();

        if (alarmDeviceId == null || alarmDeviceId.trim().isEmpty()) {
            return new ArrayList<>();
        }

        Set<String> industrialTvIds = new HashSet<>();

        // 2. 如果报警设备本身就是工业电视，直接返回该设备
        if (SystemTypeEnum.GYDS.getCode().equals(subsystemType)) {
            industrialTvIds.add(alarmDeviceId);
        } else {
            // 3. 查询设备关联关系表，获取预设位ID
            List<TDeviceRelationRecords> relationList = deviceRelationRecordsService.list(
                    new LambdaQueryWrapper<TDeviceRelationRecords>()
                            .eq(TDeviceRelationRecords::getRelatedDeviceId, alarmDeviceId)
                            .eq(TDeviceRelationRecords::getSubsystemType, subsystemType)
            );

            if (relationList == null || relationList.isEmpty()) {
                return new ArrayList<>();
            }

            // 4. 收集预设位ID
            Set<String> presetIds = relationList.stream()
                    .map(TDeviceRelationRecords::getPresetId)
                    .filter(id -> id != null && !id.trim().isEmpty())
                    .collect(Collectors.toSet());

            if (presetIds.isEmpty()) {
                return new ArrayList<>();
            }

            // 5. 通过预设位查询对应的工业电视ID
            List<TIndustrialTvPreset> presetList = industrialTvPresetService.list(
                    new LambdaQueryWrapper<TIndustrialTvPreset>()
                            .in(TIndustrialTvPreset::getPresetId, presetIds)
            );

            industrialTvIds = presetList.stream()
                    .map(TIndustrialTvPreset::getIndustrialTvId)
                    .filter(id -> id != null && !id.trim().isEmpty())
                    .collect(Collectors.toSet());
        }

        if (industrialTvIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 6. 查询工业电视列表
        List<TIndustrialTvBaseInfo> industrialTvList = industrialTvBaseInfoService.list(
                new LambdaQueryWrapper<TIndustrialTvBaseInfo>()
                        .in(TIndustrialTvBaseInfo::getDeviceId, industrialTvIds)
        );

        // 7. 封装结果，查询每个工业电视对应的录像
        List<IndustrialTvWithVideoDTO> result = new ArrayList<>();
        VideoRecordService videoRecordService = SpringContextHolder.getBean(VideoRecordService.class);

        for (TIndustrialTvBaseInfo tv : industrialTvList) {
            IndustrialTvWithVideoDTO dto = new IndustrialTvWithVideoDTO();
            dto.setIndustrialTv(tv);

            // 查询录像
            if (alarmTime != null) {
                try {
                    // 计算报警时间前后一分钟的范围
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(alarmTime);
                    calendar.add(Calendar.MINUTE, -1);
                    Date startTime = calendar.getTime();

                    calendar.setTime(alarmTime);
                    calendar.add(Calendar.MINUTE, 1);
                    Date endTime = calendar.getTime();

                    ZlMediaCacheDTO zlMediaCache = videoRecordService.getVideoRecordStream(tv.getDeviceId(), startTime, endTime);
                    if (zlMediaCache != null) {
                        dto.setZlMediaCacheDTO(zlMediaCache);
                    }
                } catch (Exception e) {
                    log.warn("查询工业电视录像失败, deviceId={}, error={}", tv.getDeviceId(), e.getMessage());
                }
            }

            result.add(dto);
        }

        return result;
    }

    /**
     * 根据报警时间找到匹配的录像文件
     *
     * 匹配规则：找到第一个lastModified时间大于等于报警时间的录像文件
     * 如果没有找到，则取最后一个录像文件
     *
     * @param videoFiles 录像文件列表
     * @param alarmTime 报警时间
     * @return 匹配的录像文件，如果没有匹配则返回null
     */
    private VideoFileDTO findMatchingVideo(List<VideoFileDTO> videoFiles, Date alarmTime) {
        if (videoFiles == null || videoFiles.isEmpty() || alarmTime == null) {
            return null;
        }

        // 按lastModified排序
        List<VideoFileDTO> sortedFiles = videoFiles.stream()
                .filter(v -> v.getLastModified() != null)
                .sorted((v1, v2) -> {
                    try {
                        Date d1 = ISO_FORMAT.parse(v1.getLastModified());
                        Date d2 = ISO_FORMAT.parse(v2.getLastModified());
                        return d1.compareTo(d2);
                    } catch (Exception e) {
                        return 0;
                    }
                })
                .collect(Collectors.toList());

        // 找到第一个lastModified时间大于等于报警时间的录像
        for (VideoFileDTO video : sortedFiles) {
            try {
                Date videoTime = ISO_FORMAT.parse(video.getLastModified());
                if (videoTime.compareTo(alarmTime) >= 0) {
                    return video;
                }
            } catch (Exception e) {
                log.warn("解析录像时间失败, lastModified={}", video.getLastModified());
            }
        }

        // 如果没有找到，返回最后一个录像文件
        if (!sortedFiles.isEmpty()) {
            return sortedFiles.get(sortedFiles.size() - 1);
        }

        return null;
    }
}

