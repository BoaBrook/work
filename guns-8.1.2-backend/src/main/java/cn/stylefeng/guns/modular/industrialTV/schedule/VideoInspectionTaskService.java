package cn.stylefeng.guns.modular.industrialTV.schedule;

import cn.stylefeng.guns.database.entity.TVideoInspectionTaskResult;
import cn.stylefeng.guns.database.entity.TVideoInspectionTasks;
import cn.stylefeng.guns.database.service.TVideoInspectionTaskResultService;
import cn.stylefeng.guns.database.service.TVideoInspectionTasksService;
import cn.stylefeng.guns.modular.industrialTV.service.VideoInspectionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class VideoInspectionTaskService {
    @Autowired
    private VideoInspectionTaskService selfService;
    @Autowired
    private VideoInspectionService videoInspectionService;
    @Autowired
    private TaskScheduler taskScheduler;
    @Autowired
    private TVideoInspectionTasksService tVideoInspectionTasksService;
    @Autowired
    private TVideoInspectionTaskResultService tVideoInspectionTaskResultService;

    private final Map<String, VideoInspectionScheduledTask> scheduledTasks = new ConcurrentHashMap<>(16);

    @PreDestroy
    private void destory(){ //关闭
       //获取正在执行的任务，全部都关闭
        Date date = new Date();
        List<TVideoInspectionTaskResult> doingTasks = videoInspectionService.getInspectionTaskResult(Arrays.asList(TVideoInspectionTaskResult.INSPECT_STATUS_DOING));
        if(!doingTasks.isEmpty()){
            for(TVideoInspectionTaskResult taskResult : doingTasks){
                taskResult.setEndTime(date);
                taskResult.setInspectionStatus(TVideoInspectionTaskResult.INSPECT_STATUS_CANCELED);
            }
            tVideoInspectionTaskResultService.updateBatchById(doingTasks);
        }
    }



    public void stopTask(String videoInspectId){
        scheduledTasks.get(videoInspectId).cancel();
        TVideoInspectionTaskResult taskResult = videoInspectionService.getLatestTaskResult(videoInspectId);
        if(taskResult != null){
            taskResult.setInspectionStatus(TVideoInspectionTaskResult.INSPECT_STATUS_CANCELED);
            tVideoInspectionTaskResultService.updateById(taskResult);
        }
    }

    /**
     * 每分钟执行一次
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void scheduleTask(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        List<TVideoInspectionTasks> allTasks = videoInspectionService.getAllTasks();
        List<TVideoInspectionTaskResult> pendingAndDoingTasks = videoInspectionService.getInspectionTaskResult(Arrays.asList(TVideoInspectionTaskResult.INSPECT_STATUS_PENDING, TVideoInspectionTaskResult.INSPECT_STATUS_DOING));
        Map<String, TVideoInspectionTaskResult> pendingAndDoingTaskMap = pendingAndDoingTasks.stream().collect(Collectors.toMap(TVideoInspectionTaskResult::getVideoInspectionId,Function.identity()));
        List<TVideoInspectionTaskResult> executeTasks = new ArrayList<>();
        for(TVideoInspectionTasks task : allTasks){
            TVideoInspectionTaskResult taskResult = pendingAndDoingTaskMap.get(task.getVideoInspectionId());
            if(taskResult == null){
                taskResult = videoInspectionService.generateTaskResultPlan(task);
                if(taskResult != null){
                    executeTasks.add(taskResult);
                }
            }else if(taskResult.getInspectionStatus().equals(TVideoInspectionTaskResult.INSPECT_STATUS_PENDING)){
                executeTasks.add(taskResult);
            }
        }
        if(executeTasks.isEmpty()){
            log.info("no task exist");
            return ;
        }
        Map<String, TVideoInspectionTasks> taskMap = allTasks.stream().collect(Collectors.toMap(TVideoInspectionTasks::getVideoInspectionId, Function.identity()));
        for(Map.Entry<String, TVideoInspectionTasks> entry : taskMap.entrySet()){
            entry.getValue().setCameraPresets(videoInspectionService.getCameraPresets(entry.getKey()));
        }
        for(TVideoInspectionTaskResult taskResult : executeTasks){
            Calendar startCal = Calendar.getInstance();
            startCal.setTime(taskResult.getStartTime());
            startCal.set(Calendar.SECOND, 0);
            startCal.set(Calendar.MILLISECOND, 0);
            if(startCal.getTime().getTime() < cal.getTime().getTime()){//表示异常情况，重新生成下一轮任务
                List<Date> dates = videoInspectionService.getNextStartDates(taskMap.get(taskResult.getVideoInspectionId()), 2);
                if(!dates.isEmpty()){
                    taskResult.setStartTime(dates.get(0));
                    taskResult.setEndTime(dates.get(1));
                    tVideoInspectionTaskResultService.updateById(taskResult);
                }
            }else if(startCal.getTime().getTime() == cal.getTime().getTime()){
                scheduledTasks.put(taskResult.getVideoInspectionId(), selfService.scheduleTaskNow(taskMap.get(taskResult.getVideoInspectionId()), taskResult));
            }
        }
    }

    public VideoInspectionScheduledTask scheduleTaskNow(TVideoInspectionTasks task, TVideoInspectionTaskResult taskResult) {
        VideoInspectionScheduledTask scheduledTask = new VideoInspectionScheduledTask();
        scheduledTask.future = this.taskScheduler.schedule(
                new VideoInspectionRunnable(task, taskResult), new Date());
        return scheduledTask;
    }
}
