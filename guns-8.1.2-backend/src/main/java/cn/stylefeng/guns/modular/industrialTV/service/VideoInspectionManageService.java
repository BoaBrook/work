package cn.stylefeng.guns.modular.industrialTV.service;

import cn.stylefeng.guns.modular.industrialTV.request.VideoInspectionTaskRequest;
import cn.stylefeng.guns.modular.industrialTV.request.VideoInspectionTaskResultRawRequest;
import cn.stylefeng.guns.modular.industrialTV.request.VideoInspectionTaskResultRequest;
import cn.stylefeng.guns.modular.industrialTV.request.VideoInspectionTaskUpdateRequest;
import cn.stylefeng.guns.modular.industrialTV.response.VideoInspectionTaskConfigResponse;
import cn.stylefeng.guns.modular.industrialTV.response.VideoInspectionTaskListResponse;
import cn.stylefeng.guns.modular.industrialTV.response.VideoInspectionTaskResultRawResponse;
import cn.stylefeng.guns.modular.industrialTV.response.VideoInspectionTaskResultResponse;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;

import java.util.List;

/**
 * 视频巡检任务管理服务接口
 */
public interface VideoInspectionManageService {

    /**
     * 巡检任务分页查询
     *
     * @param request 查询请求参数
     * @return 分页结果
     */
    PageResult<VideoInspectionTaskListResponse> pageListTask(VideoInspectionTaskRequest request);

    /**
     * 巡检任务配置查询
     *
     * @param videoInspectionId 视频巡检ID
     * @return 配置列表（按工业电视分组）
     */
    List<VideoInspectionTaskConfigResponse> getTaskConfig(String videoInspectionId);

    /**
     * 巡检任务更新（新增/修改）
     *
     * @param request 更新请求参数
     * @return 是否成功
     */
    Boolean updateTask(VideoInspectionTaskUpdateRequest request);

    /**
     * 巡检任务结果查询
     *
     * @param request 查询请求参数
     * @return 分页结果
     */
    PageResult<VideoInspectionTaskResultResponse> pageListTaskResult(VideoInspectionTaskResultRequest request);

    /**
     * 巡检任务结果执行记录查询
     *
     * @param request 查询请求参数
     * @return 分页结果
     */
    PageResult<VideoInspectionTaskResultRawResponse> pageListTaskResultRaw(VideoInspectionTaskResultRawRequest request);

    /**
     * 删除巡检任务
     *
     * @param videoInspectionId 视频巡检ID
     * @return 是否成功
     */
    Boolean deleteTask(String videoInspectionId);

}
