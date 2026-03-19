package cn.stylefeng.guns.modular.industrialTV.controller;

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
import cn.stylefeng.roses.kernel.rule.pojo.response.ResponseData;
import cn.stylefeng.roses.kernel.rule.pojo.response.SuccessResponseData;
import cn.stylefeng.roses.kernel.scanner.api.annotation.ApiResource;
import cn.stylefeng.roses.kernel.scanner.api.annotation.GetResource;
import cn.stylefeng.roses.kernel.scanner.api.annotation.PostResource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 视频巡检任务管理控制器
 */
@RestController
@ApiResource(name = "视频巡检任务管理")
public class VideoInspectionManageController {

    @Resource
    private VideoInspectionManageService videoInspectionManageService;

    /**
     * 巡检任务分页查询
     */
    @GetResource(name = "巡检任务分页查询", path = "/videoInspection/task/pageList")
    public ResponseData<PageResult<VideoInspectionTaskListResponse>> pageListTask(VideoInspectionTaskRequest request) {
        return new SuccessResponseData<>(videoInspectionManageService.pageListTask(request));
    }

    /**
     * 巡检任务配置查询
     */
    @GetResource(name = "巡检任务配置查询", path = "/videoInspection/task/config")
    public ResponseData<List<VideoInspectionTaskConfigResponse>> getTaskConfig(@RequestParam("videoInspectionId") String videoInspectionId) {
        return new SuccessResponseData<>(videoInspectionManageService.getTaskConfig(videoInspectionId));
    }

    /**
     * 巡检任务更新（新增/修改）
     */
    @PostResource(name = "巡检任务更新", path = "/videoInspection/task/update")
    public ResponseData<Boolean> updateTask(@RequestBody VideoInspectionTaskUpdateRequest request) {
        return new SuccessResponseData<>(videoInspectionManageService.updateTask(request));
    }

    /**
     * 删除巡检任务
     */
    @PostResource(name = "删除巡检任务", path = "/videoInspection/task/delete")
    public ResponseData<Boolean> deleteTask(@RequestParam("videoInspectionId") String videoInspectionId) {
        return new SuccessResponseData<>(videoInspectionManageService.deleteTask(videoInspectionId));
    }

    /**
     * 巡检任务结果分页查询
     */
    @GetResource(name = "巡检任务结果分页查询", path = "/videoInspection/taskResult/pageList")
    public ResponseData<PageResult<VideoInspectionTaskResultResponse>> pageListTaskResult(VideoInspectionTaskResultRequest request) {
        return new SuccessResponseData<>(videoInspectionManageService.pageListTaskResult(request));
    }

    /**
     * 巡检任务结果执行记录分页查询
     */
    @GetResource(name = "巡检任务结果执行记录分页查询", path = "/videoInspection/taskResultRaw/pageList")
    public ResponseData<PageResult<VideoInspectionTaskResultRawResponse>> pageListTaskResultRaw(VideoInspectionTaskResultRawRequest request) {
        return new SuccessResponseData<>(videoInspectionManageService.pageListTaskResultRaw(request));
    }

}