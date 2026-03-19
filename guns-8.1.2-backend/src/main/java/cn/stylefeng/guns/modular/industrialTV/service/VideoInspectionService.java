package cn.stylefeng.guns.modular.industrialTV.service;

import cn.stylefeng.guns.core.consts.AlarmResultConstants;
import cn.stylefeng.guns.core.utils.PageToPageResultUtils;
import cn.stylefeng.guns.core.utils.UrlToMultipartFileUtils;
import cn.stylefeng.guns.database.entity.*;
import cn.stylefeng.guns.database.service.*;
import cn.stylefeng.guns.enums.SystemTypeEnum;
import cn.stylefeng.guns.modular.index.request.AlarmInfoRequest;
import cn.stylefeng.guns.modular.index.service.IndexService;
import cn.stylefeng.guns.modular.industrialTV.request.AlarmRealTimeHandleRequest;
import cn.stylefeng.guns.modular.industrialTV.request.AlarmRealTimePushRequest;
import cn.stylefeng.guns.modular.industrialTV.request.LinkageAlarmRequest;
import cn.stylefeng.guns.modular.industrialTV.request.TaskResultDetailsRequest;
import cn.stylefeng.guns.modular.industrialTV.response.AlarmStatisticsResponse;
import cn.stylefeng.guns.modular.industrialTV.response.AlarmTrendResponse;
import cn.stylefeng.guns.modular.industrialTV.response.InspectionStatisticsResponse;
import cn.stylefeng.guns.modular.industrialTV.response.TaskPlayDetailsResponse;
import cn.stylefeng.guns.modular.nodeSystem.constants.dict.DeviceTypeEnum;
import cn.stylefeng.guns.modular.nodeSystem.dto.AlarmRawDTO;
import cn.stylefeng.guns.modular.nodeSystem.service.NodeSystemService;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;
import cn.stylefeng.roses.kernel.file.api.pojo.request.SysFileInfoRequest;
import cn.stylefeng.roses.kernel.file.api.pojo.response.SysFileInfoResponse;
import cn.stylefeng.roses.kernel.file.modular.service.SysFileInfoService;
import cn.stylefeng.roses.kernel.rule.enums.YesOrNotEnum;
import cn.stylefeng.roses.kernel.rule.pojo.request.BaseRequest;
import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class VideoInspectionService {

    @Autowired
    private IndexService indexService;

    @Autowired
    private TIndustrialTvBaseInfoService tIndustrialTvBaseInfoService;

    @Autowired
    private TAlarmResultRecordsService tAlarmResultRecordsService;

    @Autowired
    private SysFileInfoService sysFileInfoService;

    @Autowired
    private NodeSystemService nodeSystemService;

    @Autowired
    private VideoInspectionService selfService;
    @Autowired
    private TVideoInspectionTasksService tVideoInspectionTasksService;
    @Autowired
    private TVideoInspectionTaskResultService tVideoInspectionTaskResultService;
    @Autowired
    private TVideoInspectionTaskResultRawService tVideoInspectionTaskResultRawService;
    @Autowired
    private TVideoInspectionCameraPresetService tVideoInspectionCameraPresetService;
    @Autowired
    private TIndustrialTvPresetService tIndustrialTvPresetService;
    @Autowired
    private TStationAreaBaseInfoService tStationAreaBaseInfoService;

    @Autowired
    private IndustrialTVService industrialTVService;

    public AlarmStatisticsResponse getAlarmStatistics(String stationId) {
        AlarmInfoRequest request = new AlarmInfoRequest();
        request.setStationId(stationId);
        request.setPageNo(1);
        request.setPageSize(Integer.MAX_VALUE);
        request.setSystemType(SystemTypeEnum.GYDS.getCode());
        // 获取当前日期的起始时间和结束时间
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);
        // 设置请求参数
        request.setAlarmTimeStart(Timestamp.valueOf(startOfDay));
        request.setAlarmTimeEnd(Timestamp.valueOf(endOfDay));
        PageResult<TAlarmResultRecords> alarmInfo = indexService.getAlarmInfo(request);
        List<TAlarmResultRecords> alarmInfoList = alarmInfo.getRows();
        Integer totalAlarmNum = 0;
        Integer handledAlarmNum = 0;
        Integer unhandledAlarmNum = 0;
        Integer falseAlarmNum = 0;
        for (TAlarmResultRecords alarmRecord : alarmInfoList) {
            totalAlarmNum++;
            if (alarmRecord.getDisposalStatus().equals(AlarmResultConstants.DISPOSAL_STATUS_DISPOSED)) {
                handledAlarmNum++;
            } else {
                unhandledAlarmNum++;
            }
            if (alarmRecord.getProcessResult().equals(AlarmResultConstants.PROCESS_RESULT_FALSE_ALARM)) {
                falseAlarmNum++;
            }
        }
        AlarmStatisticsResponse res = new AlarmStatisticsResponse();
        res.setTotalAlarmNum(totalAlarmNum);
        res.setHandledAlarmNum(handledAlarmNum);
        res.setUnhandledAlarmNum(unhandledAlarmNum);
        res.setFalseAlarmNum(falseAlarmNum);
        res.setFalseAlarmRate(handledAlarmNum.equals(0) ? 0.0 : Math.round((falseAlarmNum.doubleValue() / handledAlarmNum) * 10000.0) / 100.0);
        return res;
    }

    public List<TVideoInspectionTasks> getInspectionPlan(String stationId){
//        List<TIndustrialTvBaseInfo> industrialTvList = tIndustrialTvBaseInfoService.lambdaQuery().eq(TIndustrialTvBaseInfo::getBelongStationId, stationId).list();
//        if(industrialTvList.isEmpty()) return new ArrayList<>();
//        industrialTvList.stream().map(TIndustrialTvBaseInfo::getDeviceId)
        return null;
    }

    public List<AlarmTrendResponse> getAlarmTrend(String stationId){
        AlarmInfoRequest request = new AlarmInfoRequest();
        request.setStationId(stationId);
        request.setPageNo(1);
        request.setPageSize(Integer.MAX_VALUE);
        request.setSystemType(SystemTypeEnum.GYDS.getCode());
        // 获取最近7天的起始时间和结束时间
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6); // 最近7天，包含今天
        LocalDateTime startOfDay = startDate.atStartOfDay();
        LocalDateTime endOfDay = endDate.atTime(23, 59, 59);
        // 设置请求参数
        request.setAlarmTimeStart(Timestamp.valueOf(startOfDay));
        request.setAlarmTimeEnd(Timestamp.valueOf(endOfDay));
        PageResult<TAlarmResultRecords> alarmInfo = indexService.getAlarmInfo(request);
        List<TAlarmResultRecords> alarmInfoList = alarmInfo.getRows();
        List<AlarmTrendResponse> trendList = new java.util.ArrayList<>();

        for (int i = 0; i < 7; i++) {
            LocalDate currentDate = endDate.minusDays(6 - i); // 从最早的日期开始遍历
            LocalDateTime dayStart = currentDate.atStartOfDay();
            LocalDateTime dayEnd = currentDate.atTime(23, 59, 59);

            // 过滤当天的告警数据
            List<TAlarmResultRecords> dailyAlarmList = alarmInfoList.stream()
                .filter(alarm -> {
                    Timestamp alarmTime = new Timestamp(alarm.getAlarmTime().getTime());
                    LocalDateTime localDateTime = alarmTime.toLocalDateTime();
                    return !localDateTime.isBefore(dayStart) && !localDateTime.isAfter(dayEnd);
                })
                .collect(java.util.stream.Collectors.toList());

            int handledCount = 0;
            int unhandledCount = 0;

            for (TAlarmResultRecords alarmRecord : dailyAlarmList) {
                if (alarmRecord.getDisposalStatus().equals(AlarmResultConstants.DISPOSAL_STATUS_DISPOSED)) {
                    handledCount++;
                } else {
                    unhandledCount++;
                }
            }

            AlarmTrendResponse trendItem = new AlarmTrendResponse();
            trendItem.setDisposed(handledCount);
            trendItem.setUndisposed(unhandledCount);
            trendList.add(trendItem);
        }
        return trendList;
    }

    private static final String ALARM_IMAGE_BUCKET = "industrial-tv-alarm-image";

    @Transactional
    public boolean inspectionRealTimePush(AlarmRealTimePushRequest request) {
        TIndustrialTvBaseInfo device = tIndustrialTvBaseInfoService.lambdaQuery().eq(TIndustrialTvBaseInfo::getDeviceCode, request.getDevId()).one();
        if (device == null) return false;
        TAlarmResultRecords alarmResultRecords = new TAlarmResultRecords();
        alarmResultRecords.setAlarmId(request.getMsgId());
        alarmResultRecords.setAlarmDeviceId(device.getDeviceId());
        alarmResultRecords.setAlarmTime(request.getDateTime());
        alarmResultRecords.setSubsystemType(SystemTypeEnum.GYDS.getCode());
        alarmResultRecords.setAlarmType(request.getEventType());
        if("1".equals(request.getEventType())){
            alarmResultRecords.setAlarmLevel("Ⅰ");
        }else if("2".equals(request.getEventType())){
            alarmResultRecords.setAlarmLevel("Ⅱ");
        }else if("3".equals(request.getEventType())){
            alarmResultRecords.setAlarmLevel("Ⅲ");
        }
        List<String> eventImageUrlList = request.getEventImageUrl();
        // 查询当前正在进行的巡检任务
        if (CollectionUtils.isNotEmpty(eventImageUrlList)) {
            StringBuilder sb = new StringBuilder();
            for (String imageUrl : eventImageUrlList) {
                try {
                    MultipartFile multipartFile = UrlToMultipartFileUtils.convertUrlToMultipartFile(imageUrl, "alarm_image.jpg");
                    SysFileInfoRequest sysFileInfoRequest = new SysFileInfoRequest();
                    sysFileInfoRequest.setSecretFlag(YesOrNotEnum.N.getCode());
                    sysFileInfoRequest.setFileBucket(ALARM_IMAGE_BUCKET);
                    SysFileInfoResponse sysFileInfoResponse = sysFileInfoService.uploadFile(multipartFile, sysFileInfoRequest);
//                    TVideoInspectionTaskResultRaw videoInspectionTaskResultRaw = new TVideoInspectionTaskResultRaw();
//                    tVideoInspectionTaskResultRawService.save(videoInspectionTaskResultRaw);
                    sb.append(sysFileInfoResponse.getFileId()).append(",");
                } catch (Exception e) {
                    // 记录异常日志
                    System.err.println("下载图片失败: " + imageUrl + ", 错误: " + e.getMessage());
                    return false;
                }
            }
            alarmResultRecords.setAlarmImage(sb.length() > 0 ? sb.substring(0, sb.length() - 1) : "");
        }
        tAlarmResultRecordsService.save(alarmResultRecords);
        // 触发联动报警
        LinkageAlarmRequest linkageAlarmRequest = new LinkageAlarmRequest();
        linkageAlarmRequest.setIndustrialTvId(device.getDeviceId());
        linkageAlarmRequest.setAlarmType(request.getEventType());
        industrialTVService.linkageAlarm(linkageAlarmRequest);
        // 推送省级平台
        boolean success = sendAlarmMessage(alarmResultRecords);
        if (success) {
            log.info("上报报警成功，alarmId: {}", alarmResultRecords.getAlarmId());
        } else {
            log.warn("上报报警失败，alarmId: {}", alarmResultRecords.getAlarmId());
        }
        return true;
    }

    public boolean inspectionRealTimeHandle(AlarmRealTimeHandleRequest request) {
        return true;
    }

    public PageResult<TVideoInspectionTasks> getInspectionTasks(TVideoInspectionTasks query, BaseRequest request){
        Page<TVideoInspectionTasks> page = Page.of(request.getPageNo(), request.getPageSize());
        if(request.getOrderBy() != null && request.getSortBy() != null){
            page.addOrder(new OrderItem(request.getOrderBy(), "asc".equals(request.getSortBy())));
        }
        LambdaQueryWrapper<TVideoInspectionTasks> lambdaQueryWrapper = new LambdaQueryWrapper<>(query);
        IPage<TVideoInspectionTasks> pageResult = tVideoInspectionTasksService.page(page, lambdaQueryWrapper);
        return PageToPageResultUtils.pageToPageResult(pageResult);
    }

    public List<TVideoInspectionTasks> getAllTasks(){
        return tVideoInspectionTasksService.list().stream().filter(it -> it.getTaskStatus().equals(TVideoInspectionTasks.INSPECTION_STATUS_VALID))
                .collect(Collectors.toList());
    }

    public TVideoInspectionTasks getTaskInfo(String videoInspectionId){
        List<TVideoInspectionTasks> tasks = tVideoInspectionTasksService.lambdaQuery().eq(TVideoInspectionTasks::getVideoInspectionId, videoInspectionId).list();
        if(tasks.isEmpty()){
            return null;
        }
        TVideoInspectionTasks task = tasks.get(0);
        List<TVideoInspectionCameraPreset> cameraPresets = tVideoInspectionCameraPresetService
                .lambdaQuery()
                .eq(TVideoInspectionCameraPreset::getVideoInspectionId, videoInspectionId).list();
        Set<String> tvIdSets = cameraPresets.stream().map(TVideoInspectionCameraPreset::getIndustrialTvId).collect(Collectors.toSet());
        List<TIndustrialTvBaseInfo> tvBaseInfos = tIndustrialTvBaseInfoService.lambdaQuery().in(TIndustrialTvBaseInfo::getDeviceId, tvIdSets).list();
        Map<String,TIndustrialTvBaseInfo> baseInfoMap = tvBaseInfos.stream().collect(Collectors.toMap(TIndustrialTvBaseInfo::getDeviceId, Function.identity()));
        Set<String> areaIds = tvBaseInfos.stream().map(TIndustrialTvBaseInfo::getBelongStationAreaId).collect(Collectors.toSet());
        Map<String, String> areaNameMap = tStationAreaBaseInfoService.lambdaQuery().in(TStationAreaBaseInfo::getAreaId, areaIds).list()
                .stream().collect(Collectors.toMap(TStationAreaBaseInfo::getAreaId, TStationAreaBaseInfo::getAreaName));
        for(TVideoInspectionCameraPreset cameraPreset : cameraPresets){
            TIndustrialTvBaseInfo baseInfo = baseInfoMap.get(cameraPreset.getIndustrialTvId());
            if(baseInfo != null){
                cameraPreset.setCameraName(baseInfo.getDeviceName());
                cameraPreset.setCameraType(baseInfo.getCameraType());
                cameraPreset.setCameraArea(areaNameMap.get(baseInfo.getBelongStationAreaId()));
            }
        }
        task.setCameraPresets(cameraPresets);
        return task;
    }

    @Transactional
    public boolean updateTaskInfo(TVideoInspectionTasks task){
        // 1，进行中的任务无法修改;
        TVideoInspectionTaskResult taskResult = selfService.getLatestTaskResult(task.getVideoInspectionId());
        if(taskResult != null && TVideoInspectionTaskResult.INSPECT_STATUS_DOING.equals(taskResult.getInspectionStatus())){
            throw new RuntimeException("运行中的任务无法修改");
        }
        tVideoInspectionTasksService.updateById(task);
        tVideoInspectionCameraPresetService.saveOrUpdateBatch(task.getCameraPresets());
        //2, 如果任务为空或状态
        List<Date> dates = getNextStartDates(task, 2);
        if(dates.isEmpty()){
            throw new RuntimeException("超过自定义时间");
        }
        //为空或者不为待执行(已完成，已取消，巡检错误)
        if(taskResult == null || !TVideoInspectionTaskResult.INSPECT_STATUS_PENDING.equals(taskResult.getInspectionStatus())){
            taskResult = new TVideoInspectionTaskResult();
            taskResult.setInspectionResultId(IdWorker.getIdStr());
            taskResult.setVideoInspectionId(task.getVideoInspectionId());
            taskResult.setStationId(task.getStationId());
            taskResult.setInspectionStatus(TVideoInspectionTaskResult.INSPECT_STATUS_PENDING);
        }
        taskResult.setStartTime(dates.get(0));
        taskResult.setEndTime(dates.get(1));
        tVideoInspectionTaskResultService.saveOrUpdate(taskResult);
        return true;
    }

    public PageResult<TVideoInspectionTaskResult> getInspectionTaskResult(TVideoInspectionTaskResult query, BaseRequest request){
        Page<TVideoInspectionTaskResult> page =  Page.of(request.getPageNo(), request.getPageSize());
        LambdaQueryWrapper<TVideoInspectionTaskResult> queryWrapper = new LambdaQueryWrapper<>(query);
        if(query.getInspectionStatuses() != null){
            queryWrapper.in(TVideoInspectionTaskResult::getInspectionStatus, query.getInspectionStatuses());
        }
        queryWrapper.orderByDesc(TVideoInspectionTaskResult::getEndTime);
        IPage<TVideoInspectionTaskResult> pageResult = tVideoInspectionTaskResultService.page(page, queryWrapper);
        Set<String> videoInspectionIdSet = pageResult.getRecords().stream().map(TVideoInspectionTaskResult::getVideoInspectionId).collect(Collectors.toSet());
        List<TVideoInspectionTasks> taskList = CollectionUtils.isEmpty(videoInspectionIdSet) ? Collections.emptyList() : tVideoInspectionTasksService.lambdaQuery().in(TVideoInspectionTasks::getVideoInspectionId, videoInspectionIdSet).list();
        Map<String, TVideoInspectionTasks> taskMap = taskList.stream().collect(Collectors.toMap(TVideoInspectionTasks::getVideoInspectionId, Function.identity()));
        for (TVideoInspectionTaskResult record : pageResult.getRecords()) {
            if(taskMap.containsKey(record.getVideoInspectionId())) {
                record.setVideoInspectionName(taskMap.get(record.getVideoInspectionId()).getVideoInspectionName());
            }
        }
        return PageToPageResultUtils.pageToPageResult(pageResult);
    }

    public TVideoInspectionTaskResult generateTaskResultPlan(TVideoInspectionTasks task){
        List<Date> dates = getNextStartDates(task,2);
        if(dates.isEmpty()){
            return null;
        }
        TVideoInspectionTaskResult exist = tVideoInspectionTaskResultService.lambdaQuery().eq(TVideoInspectionTaskResult::getVideoInspectionId, task.getVideoInspectionId())
                .eq(TVideoInspectionTaskResult::getStartTime, dates.get(0)).one();
        if(exist != null){
            return exist;
        }
        //利用唯一键插入：videoInspectionId+startTime
        TVideoInspectionTaskResult taskResult = new TVideoInspectionTaskResult();
        taskResult.setInspectionResultId(IdWorker.getIdStr());
        taskResult.setVideoInspectionId(task.getVideoInspectionId());
        taskResult.setStationId(task.getStationId());
        taskResult.setInspectionStatus(TVideoInspectionTaskResult.INSPECT_STATUS_PENDING);
        taskResult.setStartTime(dates.get(0));
        taskResult.setEndTime(dates.get(1));
        try {
            tVideoInspectionTaskResultService.save(taskResult);
            return taskResult;
        }catch (Exception e) {  // 再次查询返回已有记录
            return tVideoInspectionTaskResultService
                    .lambdaQuery()
                    .eq(TVideoInspectionTaskResult::getVideoInspectionId, task.getVideoInspectionId())
                    .eq(TVideoInspectionTaskResult::getStartTime, dates.get(0))
                    .one();
        }
    }

    public TVideoInspectionTaskResult getLatestTaskResult(String videoInspectId){
        List<TVideoInspectionTaskResult> taskResults = tVideoInspectionTaskResultService.
                lambdaQuery().eq(TVideoInspectionTaskResult::getVideoInspectionId, videoInspectId)
                .orderByDesc(TVideoInspectionTaskResult::getUpdateTime).list();
        if(taskResults.isEmpty()){
            return null;
        }
        return taskResults.get(0);
    }

    public List<Date> getNextStartDates(TVideoInspectionTasks task, int count){
        List<Date> dates = calculateNextTimes(task.getInitialInspectionTime(), new Date(), task.getInspectionInterval(), task.getIntervalUnit(), count);
        for(int i =0; i< dates.size(); i++){
            Date date = dates.get(i);
            Date modifiedDate = modifiedDate(date, task);
            if(modifiedDate == null){
                return new ArrayList<>();
            }
            dates.set(i, modifiedDate);
        }
        return dates;
    }

    private Date modifiedDate(Date date, TVideoInspectionTasks task){
        Calendar nextCal = Calendar.getInstance();
        nextCal.setTime(date);
        //修正具体日期
        if(TVideoInspectionTasks.INSPECTION_CYCLE_WORKDAY.equals(task.getInspectionCycle())){//工作日
            int week = nextCal.get(Calendar.DAY_OF_WEEK);
            if(week == Calendar.SATURDAY){
                nextCal.add(Calendar.DATE, 2);
            }else if(week == Calendar.SUNDAY){
                nextCal.add(Calendar.DATE, 1);
            }
        }else if(TVideoInspectionTasks.INSPECTION_CYCLE_WEEKEND.equals(task.getInspectionCycle())){//双休
            int week = nextCal.get(Calendar.DAY_OF_WEEK);
            if(week != Calendar.SATURDAY && week != Calendar.SUNDAY){
                int daysToSaturday = Calendar.SATURDAY - week;
                if (daysToSaturday <= 0) {
                    daysToSaturday += 7;
                }
                int daysToSunday = Calendar.SUNDAY - week;
                if (daysToSunday <= 0) {
                    daysToSunday += 7;
                }
                nextCal.add(Calendar.DATE, Math.min(daysToSaturday, daysToSunday));//取最近的那个
            }
        }else{//自定义
            Date customStart = task.getInspectionCustomStartTime();
            Date customEnd = task.getInspectionCustomEndTime();
            if(customStart != null && customEnd != null){
                //不在自定义时间范围内的跳出不生城
                if(nextCal.getTime().getTime() < customStart.getTime() || nextCal.getTime().getTime() > customEnd.getTime()){
                    task.setTaskStatus(TVideoInspectionTasks.INSPECTION_STATUS_INVALID);
                    tVideoInspectionTasksService.updateById(task);
                    return null;
                }
            }
        }
        return nextCal.getTime();
    }

    /**
     * 连续获取多个未来的执行时间
     * @param startTime 起始时间
     * @param currentTime 当前时间
     * @param interval 间隔
     * @param unit 时间单位
     * @param count 需要获取的时间数量
     * @return 包含多个连续执行时间的列表
     */
    private List<Date> calculateNextTimes(Date startTime, Date currentTime, int interval, String unit, int count) {
        if (count <= 0) {
            return new ArrayList<>();
        }
        List<Date> results = new ArrayList<>();
        long intervalMs = convertToMillis(interval, unit);
        long startMs = startTime.getTime();
        long currentMs = currentTime.getTime();

        // 如果开始时间晚于当前时间
        if (startMs > currentMs) {
            // 第一个就是开始时间
            results.add(startTime);
            // 添加后续的时间
            for (int i = 1; i < count; i++) {
                long nextMs = startMs + i * intervalMs;
                results.add(new Date(nextMs));
            }
            return results;
        }
        // 计算经过了多少个完整间隔
        long elapsedMs = currentMs - startMs;
        long completedIntervals = elapsedMs / intervalMs;
        // 计算第一个未来的执行时间
        long firstNextMs = startMs + (completedIntervals + 1) * intervalMs;
        // 确保第一个时间在当前时间之后（处理边界情况）
        while (firstNextMs <= currentMs) {
            firstNextMs += intervalMs;
        }
        // 添加所有需要的时间
        for (int i = 0; i < count; i++) {
            long nextMs = firstNextMs + i * intervalMs;
            results.add(new Date(nextMs));
        }
        return results;
    }

    private long convertToMillis(int interval, String unit) {
        if (TVideoInspectionTasks.INTERVAL_UNIT_HOUR.equals(unit)) {
            return interval * 60L * 60L * 1000L;
        } else if (TVideoInspectionTasks.INTERVAL_UNIT_DAY.equals(unit)) {
            return interval * 24L * 60L * 60L * 1000L;
        } else if (TVideoInspectionTasks.INTERVAL_UNIT_MONTH.equals(unit)) {
            // 月份处理较复杂，使用Calendar
            Calendar cal = Calendar.getInstance();
            long currentTime = cal.getTimeInMillis();
            cal.add(Calendar.MONTH, interval);
            return cal.getTimeInMillis() - currentTime;
        } else if (TVideoInspectionTasks.INTERVAL_UNIT_YEAR.equals(unit)) {
            Calendar cal = Calendar.getInstance();
            long currentTime = cal.getTimeInMillis();
            cal.add(Calendar.YEAR, interval);
            return cal.getTimeInMillis() - currentTime;
        }
        throw new IllegalArgumentException("不支持的间隔单位: " + unit);
    }


    public List<TVideoInspectionTaskResult> getInspectionTaskResult(List<Integer> statues){
        return tVideoInspectionTaskResultService.lambdaQuery().in(TVideoInspectionTaskResult::getInspectionStatus, statues).list();
    }

    public PageResult<TVideoInspectionTaskResultRaw> getInspectionTaskResultRaws(TVideoInspectionTaskResultRaw query, BaseRequest request){
        Page<TVideoInspectionTaskResultRaw> page = Page.of(request.getPageNo(), request.getPageSize());
        QueryWrapper<TVideoInspectionTaskResultRaw> queryWrapper = new QueryWrapper<>(query);
        if(StringUtils.isEmpty(query.getInspectionResultId())){
            throw new RuntimeException("执行结果ID编号不能为空");
        }
        IPage<TVideoInspectionTaskResultRaw> pageResult = tVideoInspectionTaskResultRawService.page(page, queryWrapper);
        return PageToPageResultUtils.pageToPageResult(pageResult);
    }

    public InspectionStatisticsResponse getInspectionStatistics(String stationId) {
        List<TVideoInspectionTasks> taskList = tVideoInspectionTasksService.lambdaQuery().eq(TVideoInspectionTasks::getStationId, stationId).list();
        int totalNum = 0;
        for (TVideoInspectionTasks task : taskList) {
            Integer inspectionInterval = task.getInspectionInterval();
            totalNum += 24 / inspectionInterval;
        }
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);
        List<TVideoInspectionTaskResult> taskResultList = tVideoInspectionTaskResultService.lambdaQuery().eq(TVideoInspectionTaskResult::getStationId, stationId)
                .between(TVideoInspectionTaskResult::getCreateTime, startOfDay, endOfDay)
                .list();
        int completedNum = (int) taskResultList.stream().filter(result -> result.getInspectionStatus().equals(2)).count();
        return new InspectionStatisticsResponse(totalNum, completedNum, totalNum - completedNum);
    }

    public PageResult<TVideoInspectionTaskResultRaw> getInspectionTaskResultDetails(TaskResultDetailsRequest request){
        TVideoInspectionTaskResult taskResult = tVideoInspectionTaskResultService.getById(request.getTaskId());
        Page<TVideoInspectionTaskResultRaw> page = Page.of(request.getPageNo(), request.getPageSize());
        LambdaQueryWrapper<TVideoInspectionTaskResultRaw> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(TVideoInspectionTaskResultRaw::getInspectionResultId, taskResult.getInspectionResultId());
        Page<TVideoInspectionTaskResultRaw> pageResult = tVideoInspectionTaskResultRawService.page(page, lambdaQueryWrapper);
        Set<String> presetIdSet = pageResult.getRecords().stream().map(TVideoInspectionTaskResultRaw::getPresetId).collect(Collectors.toSet());
        Set<String> industrialTvIdSet = pageResult.getRecords().stream().map(TVideoInspectionTaskResultRaw::getIndustrialTvId).collect(Collectors.toSet());
        List<TIndustrialTvPreset> presetList = CollectionUtils.isEmpty(presetIdSet) ? Collections.emptyList() : tIndustrialTvPresetService.lambdaQuery().in(TIndustrialTvPreset::getPresetId, presetIdSet).list();
        Map<String, TIndustrialTvPreset> presetMap = presetList.stream().collect(Collectors.toMap(TIndustrialTvPreset::getPresetId, Function.identity()));
        List<TIndustrialTvBaseInfo> industrialTvList = CollectionUtils.isEmpty(industrialTvIdSet) ? Collections.emptyList() : tIndustrialTvBaseInfoService.lambdaQuery().in(TIndustrialTvBaseInfo::getDeviceId, industrialTvIdSet).list();
        Map<String, TIndustrialTvBaseInfo> industrialTvMap = industrialTvList.stream().collect(Collectors.toMap(TIndustrialTvBaseInfo::getDeviceId, Function.identity()));
        List<TVideoInspectionCameraPreset> cameraPresetList = CollectionUtils.isEmpty(presetIdSet) ? Collections.emptyList() : tVideoInspectionCameraPresetService.lambdaQuery().in(TVideoInspectionCameraPreset::getPresetId, presetIdSet).list();
        Map<String, TVideoInspectionCameraPreset> cameraPresetMap = cameraPresetList.stream().collect(Collectors.toMap(TVideoInspectionCameraPreset::getPresetId, Function.identity()));
        pageResult.getRecords().forEach(result -> {
            if (presetMap.get(result.getPresetId()) != null) {
                result.setPresetName(presetMap.get(result.getPresetId()).getPresetName());
            }
            if (industrialTvMap.get(result.getIndustrialTvId()) != null) {
                result.setIndustrialTvName(industrialTvMap.get(result.getIndustrialTvId()).getDeviceName());
            }
            if (cameraPresetMap.get(result.getPresetId()) != null) {
                result.setPresetAlgorithm(cameraPresetMap.get(result.getPresetId()).getPresetAlgorithm());
                result.setStayDuration(cameraPresetMap.get(result.getPresetId()).getStayDuration());
            }
        });
        return PageToPageResultUtils.pageToPageResult(pageResult);
    }

    public List<TVideoInspectionCameraPreset> getCameraPresets(String videoInspectionId){
        return tVideoInspectionCameraPresetService
                .lambdaQuery()
                .eq(TVideoInspectionCameraPreset::getVideoInspectionId, videoInspectionId).orderByAsc(TVideoInspectionCameraPreset::getInspectionSerialNumber).list();
    }

    public TaskPlayDetailsResponse getTaskPlayDetails(String stationId){
        List<TVideoInspectionTaskResult> taskResultList = tVideoInspectionTaskResultService.lambdaQuery().eq(TVideoInspectionTaskResult::getStationId, stationId)
                .eq(TVideoInspectionTaskResult::getInspectionStatus, 1)
                .list();
        if (taskResultList.isEmpty()) {
            return null;
        }
        TVideoInspectionTaskResult taskResult = taskResultList.get(0);
        List<TVideoInspectionCameraPreset> cameraList = tVideoInspectionCameraPresetService.lambdaQuery().eq(TVideoInspectionCameraPreset::getVideoInspectionId, taskResult.getVideoInspectionId()).list();
        Set<String> tvIdSet = cameraList.stream().map(TVideoInspectionCameraPreset::getIndustrialTvId).collect(Collectors.toSet());
        List<TIndustrialTvBaseInfo> tvBaseInfoList = CollectionUtils.isEmpty(tvIdSet) ? Collections.emptyList() : tIndustrialTvBaseInfoService.lambdaQuery().in(TIndustrialTvBaseInfo::getDeviceId,tvIdSet).list();
        TaskPlayDetailsResponse  response = new TaskPlayDetailsResponse();
        response.setTaskResult(taskResult);
        response.setCameraPresetList(cameraList);
        response.setIndustrialTvBaseInfoList(tvBaseInfoList);
        return response;
    }

    private boolean sendAlarmMessage(TAlarmResultRecords alarmResultRecords) {
        try {
            AlarmRawDTO alarmRaw = new AlarmRawDTO();

            // 告警唯一标识：节点编码+告警标识
            String nodeCode = nodeSystemService.getNodeCode();
            alarmRaw.setAlarmId(nodeCode + "_" + alarmResultRecords.getAlarmId());

            // 节点编码
            alarmRaw.setNodeCode(nodeCode);

            // 设备编码（使用传感器设备编码）
            TIndustrialTvBaseInfo device = tIndustrialTvBaseInfoService.getById(alarmResultRecords.getAlarmDeviceId());
            alarmRaw.setDeviceCode(device.getDeviceCode());

            // 设备名称（使用传感器设备名称）
            alarmRaw.setDeviceName(device.getDeviceName());

            // 设备点位（位置）
//            alarmRaw.setDeviceLocation(device.getAlarmLocation());

            // 设备类型：火气系统
            alarmRaw.setDeviceType(DeviceTypeEnum.INDUSTRIAL_TELEVISION.getCode());

            // 告警级别：1-I级
            alarmRaw.setAlarmLevel(Integer.parseInt(alarmResultRecords.getAlarmLevel()));

            // 告警类型
            alarmRaw.setAlarmType(alarmResultRecords.getAlarmType());

            // 告警内容
            alarmRaw.setAlarmContent(alarmResultRecords.getAlarmContent());

            // 告警时间（格式：yyyy-MM-dd HH:mm:ss）
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            alarmRaw.setAlarmTime(sdf.format(alarmResultRecords.getAlarmTime()));
            // 发送报警
            boolean success = nodeSystemService.sendAlarmRaw(alarmRaw);
            if (success) {
                log.info("上报报警成功，alarmId: {}", alarmRaw.getAlarmId());
            } else {
                log.warn("上报报警失败，alarmId: {}", alarmRaw.getAlarmId());
            }
            return success;
        } catch (Exception e) {
            log.error("调用sendAlarmRaw接口失败，alarmId: {}", alarmResultRecords.getAlarmId(), e);
            return false;
        }
    }

    public TVideoInspectionTaskResultRaw getPlayStatus(String videoInspectionId){
        TVideoInspectionTaskResult result = selfService.getLatestTaskResult(videoInspectionId);
        if(!result.getInspectionStatus().equals(TVideoInspectionTaskResult.INSPECT_STATUS_DOING)){
            return null;
        }
        List<TVideoInspectionTaskResultRaw> raws = tVideoInspectionTaskResultRawService.lambdaQuery()
                .eq(TVideoInspectionTaskResultRaw::getVideoInspectionId, videoInspectionId).orderByDesc(TVideoInspectionTaskResultRaw::getCreateTime).list();
        if(raws.isEmpty()){
           return null;
        }
        return raws.get(0);
    }
}
