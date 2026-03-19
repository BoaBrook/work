package cn.stylefeng.guns.modular.industrialTV.controller;

import cn.stylefeng.guns.database.entity.TVideoInspectionTaskResult;
import cn.stylefeng.guns.database.entity.TVideoInspectionTaskResultRaw;
import cn.stylefeng.guns.database.entity.TVideoInspectionTasks;
import cn.stylefeng.guns.modular.industrialTV.request.AlarmRealTimeHandleRequest;
import cn.stylefeng.guns.modular.industrialTV.request.AlarmRealTimePushRequest;
import cn.stylefeng.guns.modular.industrialTV.request.TaskResultDetailsRequest;
import cn.stylefeng.guns.modular.industrialTV.service.VideoInspectionService;
import cn.stylefeng.roses.kernel.rule.enums.ResBizTypeEnum;
import cn.stylefeng.roses.kernel.rule.pojo.request.BaseRequest;
import cn.stylefeng.roses.kernel.rule.pojo.response.ResponseData;
import cn.stylefeng.roses.kernel.rule.pojo.response.SuccessResponseData;
import cn.stylefeng.roses.kernel.scanner.api.annotation.ApiResource;
import cn.stylefeng.roses.kernel.scanner.api.annotation.GetResource;
import cn.stylefeng.roses.kernel.scanner.api.annotation.PostResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ApiResource(name = "视频巡检", resBizType = ResBizTypeEnum.BUSINESS)
public class VideoInspectionController {

    @Autowired
    private VideoInspectionService videoInspectionService;

    @GetResource(name = "告警统计", path = "/video/inspection/alarm/statistics")
    public ResponseData<?> getAlarmStatistics(@RequestParam("stationId") String stationId){
        return new SuccessResponseData<>(videoInspectionService.getAlarmStatistics(stationId));
    }

    @GetResource(name = "巡检计划", path = "/video/inspection/plan")
    public ResponseData<?> getInspectionPlan(@RequestParam("stationId") String stationId){
        return new SuccessResponseData<>(videoInspectionService.    getInspectionPlan(stationId));
    }

    @GetResource(name = "巡检报警趋势统计", path = "/video/inspection/alarmTrend")
    public ResponseData<?> getAlarmTrend(@RequestParam("stationId") String stationId){
        return new SuccessResponseData<>(videoInspectionService.getAlarmTrend(stationId));
    }

    @PostResource(name = "巡检告警实时推送", path = "/video/inspection/realTime/push")
    public ResponseData<?> inspectionRealTimePush(@RequestBody AlarmRealTimePushRequest request){
        return new SuccessResponseData<>(videoInspectionService.inspectionRealTimePush(request));
    }

    @PostResource(name = "巡检告警处理实时推送", path = "/video/inspection/realTime/handle")
    public ResponseData<?> inspectionRealTimeHandle(@RequestBody AlarmRealTimeHandleRequest request){
        return new SuccessResponseData<>(videoInspectionService.inspectionRealTimeHandle(request));
    }

    @GetResource(name = "巡检任务", path="/video/inspection/tasks")
    public ResponseData<?> getInspectionTasks(TVideoInspectionTasks query, BaseRequest request){
        return new SuccessResponseData<>(videoInspectionService.getInspectionTasks(query, request));
    }

    @GetResource(name ="获取巡检任务配置", path = "/video/inspection/task/getInfo")
    public ResponseData<?> getTaskInfo(@RequestParam("videoInspectionId") String videoInspectionId){
        return new SuccessResponseData<>(videoInspectionService.getTaskInfo(videoInspectionId));
    }

    @PostResource(name = "修改巡检任务配置", path = "/video/inspection/task/updateInfo")
    public ResponseData<?> updateTaskInfo(@RequestBody TVideoInspectionTasks task){
        return new SuccessResponseData<>(videoInspectionService.updateTaskInfo(task));
    }

    @GetResource(name = "巡检执行结果", path = "/video/inspection/taskResults")
    public ResponseData<?> getInspectionTaskResults(TVideoInspectionTaskResult query, BaseRequest request){
        return new SuccessResponseData<>(videoInspectionService.getInspectionTaskResult(query, request));
    }

    @GetResource(name = "巡检执行预置点结果", path = "/video/inspection/taskResultRaws")
    public ResponseData<?> getInspectionTaskResultRaws(TVideoInspectionTaskResultRaw query, BaseRequest request){
        return new SuccessResponseData<>(videoInspectionService.getInspectionTaskResultRaws(query, request));
    }

    @GetResource(name = "巡检统计", path = "/video/inspection/statistics")
    public ResponseData<?> getInspectionStatistics(@RequestParam("stationId") String stationId){
        return new SuccessResponseData<>(videoInspectionService.getInspectionStatistics(stationId));
    }

    @GetResource(name = "巡检记录详情", path = "/video/inspection/taskResultDetails")
    public ResponseData<?> getInspectionTaskResultDetails(TaskResultDetailsRequest request){
        return new SuccessResponseData<>(videoInspectionService.getInspectionTaskResultDetails(request));
    }

    @GetResource(name = "查询巡检播放任务", path = "/video/inspection/taskPlayDetails")
    public ResponseData<?> getTaskPlayDetails(@RequestParam("stationId") String stationId){
        return new SuccessResponseData<>(videoInspectionService.getTaskPlayDetails(stationId));
    }

    @GetResource(name = "获取巡检任务当前的摄像头播放的记录", path = "/video/inspection/playStatus")
    public ResponseData<?> getPlayStatus(@RequestParam("videoInspectionId") String videoInspectionId){
        return new SuccessResponseData<>(videoInspectionService.getPlayStatus(videoInspectionId));
    }

}
