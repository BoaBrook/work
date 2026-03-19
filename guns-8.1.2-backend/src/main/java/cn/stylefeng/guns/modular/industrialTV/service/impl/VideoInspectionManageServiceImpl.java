package cn.stylefeng.guns.modular.industrialTV.service.impl;

import cn.stylefeng.guns.database.entity.*;
import cn.stylefeng.guns.database.service.*;
import cn.stylefeng.guns.modular.industrialTV.request.VideoInspectionTaskRequest;
import cn.stylefeng.guns.modular.industrialTV.request.VideoInspectionTaskResultRawRequest;
import cn.stylefeng.guns.modular.industrialTV.request.VideoInspectionTaskResultRequest;
import cn.stylefeng.guns.modular.industrialTV.request.VideoInspectionTaskUpdateRequest;
import cn.stylefeng.guns.modular.industrialTV.response.VideoInspectionTaskConfigResponse;
import cn.stylefeng.guns.modular.industrialTV.response.VideoInspectionTaskListResponse;
import cn.stylefeng.guns.modular.industrialTV.response.VideoInspectionTaskResultRawResponse;
import cn.stylefeng.guns.modular.industrialTV.response.VideoInspectionTaskResultResponse;
import cn.stylefeng.guns.modular.industrialTV.service.VideoInspectionManageService;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;
import cn.stylefeng.roses.kernel.sys.modular.user.entity.SysUser;
import cn.stylefeng.roses.kernel.sys.modular.user.service.SysUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 视频巡检任务管理服务实现
 */
@Service
public class VideoInspectionManageServiceImpl implements VideoInspectionManageService {

    @Resource
    private TVideoInspectionTasksService tVideoInspectionTasksService;

    @Resource
    private TVideoInspectionCameraPresetService tVideoInspectionCameraPresetService;

    @Resource
    private TVideoInspectionTaskResultService tVideoInspectionTaskResultService;

    @Resource
    private TVideoInspectionTaskResultRawService tVideoInspectionTaskResultRawService;

    @Resource
    private TStationBaseInfoService tStationBaseInfoService;

    @Resource
    private TIndustrialTvBaseInfoService tIndustrialTvBaseInfoService;

    @Resource
    private TIndustrialTvPresetService tIndustrialTvPresetService;

    @Resource
    private TStationAreaBaseInfoService tStationAreaBaseInfoService;

    @Resource
    private SysUserService sysUserService;

    @Override
    public PageResult<VideoInspectionTaskListResponse> pageListTask(VideoInspectionTaskRequest request) {
        LambdaQueryWrapper<TVideoInspectionTasks> queryWrapper = new LambdaQueryWrapper<>();

        // 处理级联查询：作业区和管线
        handleCascadeQuery(request, queryWrapper);

        // 站场ID
        queryWrapper.eq(StringUtils.isNotBlank(request.getStationId()),
                TVideoInspectionTasks::getStationId, request.getStationId());

        // 巡检名称模糊查询
        queryWrapper.like(StringUtils.isNotBlank(request.getTaskName()),
                TVideoInspectionTasks::getVideoInspectionName, request.getTaskName());

        // 任务状态
        queryWrapper.eq(request.getInspectionStatus() != null,
                TVideoInspectionTasks::getTaskStatus, request.getInspectionStatus());

        queryWrapper.orderByDesc(TVideoInspectionTasks::getCreateTime);

        Page<TVideoInspectionTasks> page = new Page<>(request.getPageNo(), request.getPageSize());
        Page<TVideoInspectionTasks> resultPage = tVideoInspectionTasksService.page(page, queryWrapper);

        List<VideoInspectionTaskListResponse> responseList = new ArrayList<>();
        List<TVideoInspectionTasks> records = resultPage.getRecords();

        if (CollectionUtils.isNotEmpty(records)) {
            // 收集所有创建人ID
            Set<Long> createUserIds = records.stream()
                    .map(TVideoInspectionTasks::getCreateUser)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            // 批量查询用户名
            Map<Long, String> userNameMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(createUserIds)) {
                List<SysUser> sysUsers = sysUserService.listByIds(createUserIds);
                userNameMap = sysUsers.stream().collect(Collectors.toMap(SysUser::getUserId, SysUser::getRealName));
            }

            // 收集所有巡检任务ID
            List<String> taskIds = records.stream()
                    .map(TVideoInspectionTasks::getVideoInspectionId)
                    .collect(Collectors.toList());

            // 查询每个任务的最近执行完成时间和执行状态
            Map<String, Date> lastCompletionTimeMap = new HashMap<>();
            Map<String, Integer> lastInspectionStatusMap = new HashMap<>();

            if (CollectionUtils.isNotEmpty(taskIds)) {
                for (String taskId : taskIds) {
                    LambdaQueryWrapper<TVideoInspectionTaskResult> resultWrapper = new LambdaQueryWrapper<>();
                    resultWrapper.eq(TVideoInspectionTaskResult::getVideoInspectionId, taskId)
                            .eq(TVideoInspectionTaskResult::getInspectionStatus, TVideoInspectionTaskResult.INSPECT_STATUS_FINISHED)
                            .orderByDesc(TVideoInspectionTaskResult::getEndTime)
                            .last("LIMIT 1");
                    TVideoInspectionTaskResult lastResult = tVideoInspectionTaskResultService.getOne(resultWrapper);
                    if (lastResult != null) {
                        lastCompletionTimeMap.put(taskId, lastResult.getEndTime());
                    }

                    // 查询最近一条记录的执行状态
                    LambdaQueryWrapper<TVideoInspectionTaskResult> statusWrapper = new LambdaQueryWrapper<>();
                    statusWrapper.eq(TVideoInspectionTaskResult::getVideoInspectionId, taskId)
                            .orderByDesc(TVideoInspectionTaskResult::getCreateTime)
                            .last("LIMIT 1");
                    TVideoInspectionTaskResult lastStatusResult = tVideoInspectionTaskResultService.getOne(statusWrapper);
                    if (lastStatusResult != null) {
                        lastInspectionStatusMap.put(taskId, lastStatusResult.getInspectionStatus());
                    }
                }
            }

            // 组装响应数据
            for (TVideoInspectionTasks task : records) {
                VideoInspectionTaskListResponse response = new VideoInspectionTaskListResponse();
                // 基本信息
                response.setVideoInspectionId(task.getVideoInspectionId());
                response.setStationId(task.getStationId());
                response.setVideoInspectionName(task.getVideoInspectionName());
                response.setInspectionCycle(task.getInspectionCycle());
                response.setInspectionCycleName(getInspectionCycleName(task.getInspectionCycle()));
                response.setInspectionCustomStartTime(task.getInspectionCustomStartTime());
                response.setInspectionCustomEndTime(task.getInspectionCustomEndTime());
                response.setInitialInspectionTime(task.getInitialInspectionTime());
                response.setInspectionInterval(task.getInspectionInterval());
                response.setIntervalUnit(task.getIntervalUnit());
                response.setIntervalUnitName(getIntervalUnitName(task.getIntervalUnit()));
                response.setRemark(task.getRemark());
                
                // 任务状态（启用/停用）
                response.setTaskStatus(task.getTaskStatus());
                response.setTaskStatusName(task.getTaskStatus() == 0 ? "启用" : "停用");

                // 设置最近完成时间
                Date lastCompletionTime = lastCompletionTimeMap.get(task.getVideoInspectionId());
                if (lastCompletionTime != null) {
                    response.setLastCompletionTime(lastCompletionTime);
                }

                // 执行状态（最近一条执行记录的状态）
                Integer lastInspectionStatus = lastInspectionStatusMap.get(task.getVideoInspectionId());
                if (lastInspectionStatus != null) {
                    response.setInspectionStatus(lastInspectionStatus);
                    response.setInspectionStatusName(getInspectionStatusName(lastInspectionStatus));
                }

                // 设置创建人名称
                if (task.getCreateUser() != null) {
                    response.setCreateUserName(userNameMap.getOrDefault(task.getCreateUser(), ""));
                }

                responseList.add(response);
            }
        }

        PageResult<VideoInspectionTaskListResponse> pageResult = new PageResult<>();
        pageResult.setPageNo((int) resultPage.getCurrent());
        pageResult.setPageSize((int) resultPage.getSize());
        pageResult.setTotalPage((int) resultPage.getPages());
        pageResult.setTotalRows((int) resultPage.getTotal());
        pageResult.setRows(responseList);
        return pageResult;
    }

    @Override
    public List<VideoInspectionTaskConfigResponse> getTaskConfig(String videoInspectionId) {
        // 查询巡检配置
        LambdaQueryWrapper<TVideoInspectionCameraPreset> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TVideoInspectionCameraPreset::getVideoInspectionId, videoInspectionId)
                .orderByAsc(TVideoInspectionCameraPreset::getInspectionSerialNumber);
        List<TVideoInspectionCameraPreset> presetList = tVideoInspectionCameraPresetService.list(queryWrapper);

        if (CollectionUtils.isEmpty(presetList)) {
            return new ArrayList<>();
        }

        // 收集工业电视ID
        List<String> industrialTvIds = presetList.stream()
                .map(TVideoInspectionCameraPreset::getIndustrialTvId)
                .distinct()
                .collect(Collectors.toList());

        // 批量查询工业电视信息
        List<TIndustrialTvBaseInfo> tvList = tIndustrialTvBaseInfoService.listByIds(industrialTvIds);
        Map<String, TIndustrialTvBaseInfo> tvMap = tvList.stream()
                .collect(Collectors.toMap(TIndustrialTvBaseInfo::getDeviceId, t -> t, (v1, v2) -> v1));

        // 收集预设位ID
        List<String> presetIds = presetList.stream()
                .map(TVideoInspectionCameraPreset::getPresetId)
                .distinct()
                .collect(Collectors.toList());

        // 批量查询预设位信息
        List<TIndustrialTvPreset> presetInfoList = tIndustrialTvPresetService.listByIds(presetIds);
        Map<String, TIndustrialTvPreset> presetInfoMap = presetInfoList.stream()
                .collect(Collectors.toMap(TIndustrialTvPreset::getPresetId, p -> p, (v1, v2) -> v1));

        // 收集区域ID
        List<String> areaIds = tvList.stream()
                .map(TIndustrialTvBaseInfo::getBelongStationAreaId)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList());

        // 批量查询区域信息
        Map<String, String> areaNameMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(areaIds)) {
            List<TStationAreaBaseInfo> areaList = tStationAreaBaseInfoService.listByIds(areaIds);
            areaNameMap = areaList.stream()
                    .collect(Collectors.toMap(TStationAreaBaseInfo::getAreaId, TStationAreaBaseInfo::getAreaName, (v1, v2) -> v1));
        }

        // 按工业电视分组
        Map<String, List<TVideoInspectionCameraPreset>> groupByTv = presetList.stream()
                .collect(Collectors.groupingBy(TVideoInspectionCameraPreset::getIndustrialTvId));

        List<VideoInspectionTaskConfigResponse> result = new ArrayList<>();
        for (Map.Entry<String, List<TVideoInspectionCameraPreset>> entry : groupByTv.entrySet()) {
            String tvId = entry.getKey();
            List<TVideoInspectionCameraPreset> presets = entry.getValue();

            VideoInspectionTaskConfigResponse response = new VideoInspectionTaskConfigResponse();
            response.setIndustrialTvId(tvId);

            TIndustrialTvBaseInfo tv = tvMap.get(tvId);
            if (tv != null) {
                response.setCameraName(tv.getDeviceName());
                response.setCameraType(tv.getCameraType());
                response.setAreaName(areaNameMap.getOrDefault(tv.getBelongStationAreaId(), ""));
            }

            // 构建预设位配置列表
            List<VideoInspectionTaskConfigResponse.PresetConfig> presetConfigs = new ArrayList<>();
            for (TVideoInspectionCameraPreset preset : presets) {
                VideoInspectionTaskConfigResponse.PresetConfig config = new VideoInspectionTaskConfigResponse.PresetConfig();
                config.setCameraPresetId(preset.getCameraPresetId());
                config.setPresetId(preset.getPresetId());
                config.setPresetAlgorithm(preset.getPresetAlgorithm());
                config.setInspectionSerialNumber(preset.getInspectionSerialNumber());
                config.setStayDuration(preset.getStayDuration());

                TIndustrialTvPreset presetInfo = presetInfoMap.get(preset.getPresetId());
                if (presetInfo != null) {
                    config.setPresetName(presetInfo.getPresetName());
                }

                presetConfigs.add(config);
            }
            response.setPresetConfigs(presetConfigs);
            result.add(response);
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateTask(VideoInspectionTaskUpdateRequest request) {
        TVideoInspectionTasks task = new TVideoInspectionTasks();
        boolean isNew = StringUtils.isBlank(request.getVideoInspectionId());

        if (isNew) {
            // 新增
            task.setVideoInspectionId(IdWorker.getIdStr());
        } else {
            // 修改
            task.setVideoInspectionId(request.getVideoInspectionId());
        }

        task.setStationId(request.getStationId());
        task.setVideoInspectionName(request.getVideoInspectionName());
        task.setInspectionCycle(request.getInspectionCycle());
        task.setInspectionCustomStartTime(request.getInspectionCustomStartTime());
        task.setInspectionCustomEndTime(request.getInspectionCustomEndTime());
        task.setInitialInspectionTime(request.getInitialInspectionTime());
        task.setInspectionInterval(request.getInspectionInterval());
        task.setIntervalUnit(request.getIntervalUnit());
        task.setRemark(request.getRemark());
        task.setTaskStatus(request.getTaskStatus() != null ? request.getTaskStatus() : 0);

        boolean taskResult;
        if (isNew) {
            taskResult = tVideoInspectionTasksService.save(task);
        } else {
            taskResult = tVideoInspectionTasksService.updateById(task);
        }

        // 处理巡检配置
        if (taskResult && request.getCameraConfigs() != null) {
            String taskId = task.getVideoInspectionId();

            // 查询存量配置
            LambdaQueryWrapper<TVideoInspectionCameraPreset> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TVideoInspectionCameraPreset::getVideoInspectionId, taskId);
            List<TVideoInspectionCameraPreset> existingPresets = tVideoInspectionCameraPresetService.list(queryWrapper);

            // 收集入参中的所有主键（从二级结构中提取）
            Set<String> inputIds = new HashSet<>();
            for (VideoInspectionTaskUpdateRequest.CameraConfig cameraConfig : request.getCameraConfigs()) {
                if (cameraConfig.getPresetConfigs() != null) {
                    for (VideoInspectionTaskUpdateRequest.CameraConfig.PresetConfig presetConfig : cameraConfig.getPresetConfigs()) {
                        if (StringUtils.isNotBlank(presetConfig.getCameraPresetId())) {
                            inputIds.add(presetConfig.getCameraPresetId());
                        }
                    }
                }
            }

            // 需要删除的配置
            List<String> toDelete = existingPresets.stream()
                    .map(TVideoInspectionCameraPreset::getCameraPresetId)
                    .filter(id -> !inputIds.contains(id))
                    .collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(toDelete)) {
                tVideoInspectionCameraPresetService.removeByIds(toDelete);
            }

            // 构建新增和更新的数据列表
            List<TVideoInspectionCameraPreset> toSaveList = new ArrayList<>();
            List<TVideoInspectionCameraPreset> toUpdateList = new ArrayList<>();

            for (VideoInspectionTaskUpdateRequest.CameraConfig cameraConfig : request.getCameraConfigs()) {
                if (cameraConfig.getPresetConfigs() == null) {
                    continue;
                }
                for (VideoInspectionTaskUpdateRequest.CameraConfig.PresetConfig presetConfig : cameraConfig.getPresetConfigs()) {
                    TVideoInspectionCameraPreset preset = new TVideoInspectionCameraPreset();
                    preset.setVideoInspectionId(taskId);
                    preset.setIndustrialTvId(cameraConfig.getIndustrialTvId());
                    preset.setPresetId(presetConfig.getPresetId());
                    preset.setPresetAlgorithm(presetConfig.getPresetAlgorithm());
                    preset.setInspectionSerialNumber(presetConfig.getInspectionSerialNumber());
                    preset.setStayDuration(presetConfig.getStayDuration());

                    if (StringUtils.isBlank(presetConfig.getCameraPresetId())) {
                        // 新增
                        preset.setCameraPresetId(IdWorker.getIdStr());
                        toSaveList.add(preset);
                    } else {
                        // 修改
                        preset.setCameraPresetId(presetConfig.getCameraPresetId());
                        toUpdateList.add(preset);
                    }
                }
            }

            // 批量保存新增的数据
            if (!toSaveList.isEmpty()) {
                tVideoInspectionCameraPresetService.saveBatch(toSaveList);
            }

            // 批量更新已有的数据
            if (!toUpdateList.isEmpty()) {
                tVideoInspectionCameraPresetService.updateBatchById(toUpdateList);
            }
        }

        return taskResult;
    }

    @Override
    public PageResult<VideoInspectionTaskResultResponse> pageListTaskResult(VideoInspectionTaskResultRequest request) {
        LambdaQueryWrapper<TVideoInspectionTaskResult> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(StringUtils.isNotBlank(request.getVideoInspectionId()),
                TVideoInspectionTaskResult::getVideoInspectionId, request.getVideoInspectionId());

        queryWrapper.eq(request.getInspectionStatus() != null,
                TVideoInspectionTaskResult::getInspectionStatus, request.getInspectionStatus());

        // 开始时间筛选
        if (request.getExecuteStartTime() != null) {
            try {
                Date startTime = request.getExecuteStartTime();
                queryWrapper.ge(TVideoInspectionTaskResult::getStartTime, startTime);
            } catch (Exception ignored) {
            }
        }
        if (request.getExecuteEndTime() != null) {
            try {
                Date startTime = request.getExecuteEndTime();
                queryWrapper.le(TVideoInspectionTaskResult::getStartTime, startTime);
            } catch (Exception ignored) {
            }
        }

        // 结束时间筛选
        if (request.getFinishStartTime() != null) {
            try {
                Date endTime = request.getFinishStartTime();
                queryWrapper.ge(TVideoInspectionTaskResult::getEndTime, endTime);
            } catch (Exception ignored) {
            }
        }
        if (request.getFinishEndTime() != null) {
            try {
                Date endTime = request.getFinishEndTime();
                queryWrapper.le(TVideoInspectionTaskResult::getEndTime, endTime);
            } catch (Exception ignored) {
            }
        }

        queryWrapper.orderByDesc(TVideoInspectionTaskResult::getCreateTime);

        Page<TVideoInspectionTaskResult> page = new Page<>(request.getPageNo(), request.getPageSize());
        Page<TVideoInspectionTaskResult> resultPage = tVideoInspectionTaskResultService.page(page, queryWrapper);

        List<VideoInspectionTaskResultResponse> responseList = new ArrayList<>();
        List<TVideoInspectionTaskResult> records = resultPage.getRecords();

        if (CollectionUtils.isNotEmpty(records)) {
            // 收集巡检任务ID
            List<String> taskIds = records.stream()
                    .map(TVideoInspectionTaskResult::getVideoInspectionId)
                    .distinct()
                    .collect(Collectors.toList());

            // 批量查询巡检任务名称
            Map<String, String> taskNameMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(taskIds)) {
                List<TVideoInspectionTasks> tasks = tVideoInspectionTasksService.listByIds(taskIds);
                taskNameMap = tasks.stream()
                        .collect(Collectors.toMap(TVideoInspectionTasks::getVideoInspectionId,
                                TVideoInspectionTasks::getVideoInspectionName, (v1, v2) -> v1));
            }

            // 收集结果ID
            List<String> resultIds = records.stream()
                    .map(TVideoInspectionTaskResult::getInspectionResultId)
                    .collect(Collectors.toList());

            // 查询每个结果的执行记录数量和执行结果
            Map<String, Integer> countMap = new HashMap<>();
            Map<String, String> resultStatusMap = new HashMap<>();

            if (CollectionUtils.isNotEmpty(resultIds)) {
                for (String resultId : resultIds) {
                    LambdaQueryWrapper<TVideoInspectionTaskResultRaw> rawWrapper = new LambdaQueryWrapper<>();
                    rawWrapper.eq(TVideoInspectionTaskResultRaw::getInspectionResultId, resultId);
                    long count = tVideoInspectionTaskResultRawService.count(rawWrapper);
                    countMap.put(resultId, (int) count);

                    // 查询执行结果：是否全部正常
                    LambdaQueryWrapper<TVideoInspectionTaskResultRaw> statusWrapper = new LambdaQueryWrapper<>();
                    statusWrapper.eq(TVideoInspectionTaskResultRaw::getInspectionResultId, resultId)
                            .ne(TVideoInspectionTaskResultRaw::getPresetInspectResultStatus,
                                    TVideoInspectionTaskResultRaw.PRESET_INSPECT_RESULT_STATUS_SUCCESS);
                    long errorCount = tVideoInspectionTaskResultRawService.count(statusWrapper);
                    resultStatusMap.put(resultId, errorCount == 0 ? "正常" : "异常");
                }
            }

            // 组装响应数据
            for (TVideoInspectionTaskResult result : records) {
                VideoInspectionTaskResultResponse response = new VideoInspectionTaskResultResponse();
                response.setInspectionResultId(result.getInspectionResultId());
                response.setVideoInspectionId(result.getVideoInspectionId());
                response.setVideoInspectionName(taskNameMap.getOrDefault(result.getVideoInspectionId(), ""));
                response.setInspectionStatus(result.getInspectionStatus());
                response.setInspectionStatusName(getInspectionStatusName(result.getInspectionStatus()));

                if (result.getStartTime() != null) {
                    response.setStartTime(result.getStartTime());
                }
                if (result.getEndTime() != null) {
                    response.setEndTime(result.getEndTime());
                }

                // 设置总巡检项数
                response.setTotalInspectCount(countMap.getOrDefault(result.getInspectionResultId(), 0));

                // 计算总耗时
                if (result.getStartTime() != null && result.getEndTime() != null) {
                    long duration = (result.getEndTime().getTime() - result.getStartTime().getTime()) / (1000 * 60);
                    response.setTotalDuration(duration + "分钟");
                }

                // 设置执行结果
                response.setInspectResult(resultStatusMap.getOrDefault(result.getInspectionResultId(), ""));

                responseList.add(response);
            }
        }

        PageResult<VideoInspectionTaskResultResponse> pageResult = new PageResult<>();
        pageResult.setPageNo((int) resultPage.getCurrent());
        pageResult.setPageSize((int) resultPage.getSize());
        pageResult.setTotalPage((int) resultPage.getPages());
        pageResult.setTotalRows((int) resultPage.getTotal());
        pageResult.setRows(responseList);
        return pageResult;
    }

    @Override
    public PageResult<VideoInspectionTaskResultRawResponse> pageListTaskResultRaw(VideoInspectionTaskResultRawRequest request) {
        LambdaQueryWrapper<TVideoInspectionTaskResultRaw> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotBlank(request.getInspectionResultId()),
                TVideoInspectionTaskResultRaw::getInspectionResultId, request.getInspectionResultId());

        Page<TVideoInspectionTaskResultRaw> page = new Page<>(request.getPageNo(), request.getPageSize());
        Page<TVideoInspectionTaskResultRaw> resultPage = tVideoInspectionTaskResultRawService.page(page, queryWrapper);

        List<VideoInspectionTaskResultRawResponse> responseList = new ArrayList<>();
        List<TVideoInspectionTaskResultRaw> records = resultPage.getRecords();

        if (CollectionUtils.isNotEmpty(records)) {
            // 收集工业电视ID
            List<String> tvIds = records.stream()
                    .map(TVideoInspectionTaskResultRaw::getIndustrialTvId)
                    .distinct()
                    .collect(Collectors.toList());

            // 批量查询工业电视名称
            Map<String, String> tvNameMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(tvIds)) {
                List<TIndustrialTvBaseInfo> tvList = tIndustrialTvBaseInfoService.listByIds(tvIds);
                tvNameMap = tvList.stream()
                        .collect(Collectors.toMap(TIndustrialTvBaseInfo::getDeviceId,
                                TIndustrialTvBaseInfo::getDeviceName, (v1, v2) -> v1));
            }

            // 收集预设位ID
            List<String> presetIds = records.stream()
                    .map(TVideoInspectionTaskResultRaw::getPresetId)
                    .distinct()
                    .collect(Collectors.toList());

            // 批量查询预设位名称
            Map<String, String> presetNameMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(presetIds)) {
                List<TIndustrialTvPreset> presetList = tIndustrialTvPresetService.listByIds(presetIds);
                presetNameMap = presetList.stream()
                        .collect(Collectors.toMap(TIndustrialTvPreset::getPresetId,
                                TIndustrialTvPreset::getPresetName, (v1, v2) -> v1));
            }

            // 查询巡检配置获取算法和停留时长
            Map<String, TVideoInspectionCameraPreset> configMap = new HashMap<>();
            for (TVideoInspectionTaskResultRaw raw : records) {
                LambdaQueryWrapper<TVideoInspectionCameraPreset> configWrapper = new LambdaQueryWrapper<>();
                configWrapper.eq(TVideoInspectionCameraPreset::getVideoInspectionId, raw.getVideoInspectionId())
                        .eq(TVideoInspectionCameraPreset::getIndustrialTvId, raw.getIndustrialTvId())
                        .eq(TVideoInspectionCameraPreset::getPresetId, raw.getPresetId())
                        .last("LIMIT 1");
                TVideoInspectionCameraPreset config = tVideoInspectionCameraPresetService.getOne(configWrapper);
                if (config != null) {
                    String key = raw.getVideoInspectionId() + "_" + raw.getIndustrialTvId() + "_" + raw.getPresetId();
                    configMap.put(key, config);
                }
            }

            // 组装响应数据
            for (TVideoInspectionTaskResultRaw raw : records) {
                VideoInspectionTaskResultRawResponse response = new VideoInspectionTaskResultRawResponse();
                response.setInspectionResultRawId(raw.getInspectionResultRawId());
                response.setIndustrialTvId(raw.getIndustrialTvId());
                response.setCameraName(tvNameMap.getOrDefault(raw.getIndustrialTvId(), ""));
                response.setPresetId(raw.getPresetId());
                response.setPresetName(presetNameMap.getOrDefault(raw.getPresetId(), ""));
                response.setPresetInspectResultStatus(raw.getPresetInspectResultStatus());
                response.setPresetInspectResultStatusName(getPresetResultStatusName(raw.getPresetInspectResultStatus()));
                response.setPresetInspectResultPic(raw.getPresetInspectResultPic());
                response.setRemark(raw.getRemark());
                response.setCreateTime(raw.getCreateTime());

                // 从配置中获取算法和停留时长
                String key = raw.getVideoInspectionId() + "_" + raw.getIndustrialTvId() + "_" + raw.getPresetId();
                TVideoInspectionCameraPreset config = configMap.get(key);
                if (config != null) {
                    response.setPresetAlgorithm(config.getPresetAlgorithm());
                    response.setStayDuration(config.getStayDuration());
                }

                responseList.add(response);
            }
        }

        PageResult<VideoInspectionTaskResultRawResponse> pageResult = new PageResult<>();
        pageResult.setPageNo((int) resultPage.getCurrent());
        pageResult.setPageSize((int) resultPage.getSize());
        pageResult.setTotalPage((int) resultPage.getPages());
        pageResult.setTotalRows((int) resultPage.getTotal());
        pageResult.setRows(responseList);
        return pageResult;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteTask(String videoInspectionId) {
        // 删除巡检配置
        LambdaQueryWrapper<TVideoInspectionCameraPreset> presetWrapper = new LambdaQueryWrapper<>();
        presetWrapper.eq(TVideoInspectionCameraPreset::getVideoInspectionId, videoInspectionId);
        tVideoInspectionCameraPresetService.remove(presetWrapper);

        // 删除巡检任务
        return tVideoInspectionTasksService.removeById(videoInspectionId);
    }

    /**
     * 处理级联查询：作业区和管线
     */
    private void handleCascadeQuery(VideoInspectionTaskRequest request, LambdaQueryWrapper<TVideoInspectionTasks> queryWrapper) {
        String belongOperationArea = request.getWorkAreaId();
        String belongPipeline = request.getPipelineId();

        if (StringUtils.isNotBlank(belongOperationArea) || StringUtils.isNotBlank(belongPipeline)) {
            LambdaQueryWrapper<TStationBaseInfo> stationWrapper = new LambdaQueryWrapper<>();
            if (StringUtils.isNotBlank(belongOperationArea)) {
                stationWrapper.eq(TStationBaseInfo::getBelongOperationArea, belongOperationArea);
            }
            if (StringUtils.isNotBlank(belongPipeline)) {
                stationWrapper.eq(TStationBaseInfo::getBelongPipeline, belongPipeline);
            }

            List<TStationBaseInfo> stationList = tStationBaseInfoService.list(stationWrapper);
            List<String> stationIds = stationList.stream()
                    .map(TStationBaseInfo::getStationId)
                    .collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(stationIds)) {
                queryWrapper.in(TVideoInspectionTasks::getStationId, stationIds);
            }else{
                queryWrapper.apply("1=2");
            }
        }
    }

    /**
     * 获取巡检周期名称
     */
    private String getInspectionCycleName(String cycle) {
        if (cycle == null) return "";
        switch (cycle) {
            case "daily": return "每日";
            case "workday": return "工作日";
            case "weekend": return "周末";
            case "custom": return "自定义";
            default: return cycle;
        }
    }

    /**
     * 获取间隔单位名称
     */
    private String getIntervalUnitName(String unit) {
        if (unit == null) return "";
        switch (unit) {
            case "hour": return "小时";
            case "day": return "天";
            case "month": return "月";
            case "year": return "年";
            default: return unit;
        }
    }

    /**
     * 获取巡检状态名称
     */
    private String getInspectionStatusName(Integer status) {
        if (status == null) return "";
        switch (status) {
            case 0: return "待执行";
            case 1: return "执行中";
            case 2: return "已完成";
            case 3: return "已取消";
            case 4: return "巡检错误";
            default: return String.valueOf(status);
        }
    }

    /**
     * 获取预设位巡检结果状态名称
     */
    private String getPresetResultStatusName(Integer status) {
        if (status == null) return "";
        switch (status) {
            case 0: return "正常";
            case 1: return "异常";
            case 2: return "离线";
            default: return String.valueOf(status);
        }
    }

}
