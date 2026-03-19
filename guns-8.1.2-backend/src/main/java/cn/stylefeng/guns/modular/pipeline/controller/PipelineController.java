package cn.stylefeng.guns.modular.pipeline.controller;

import cn.stylefeng.guns.database.entity.TPipelineBaseInfo;
import cn.stylefeng.guns.modular.pipeline.dto.PipelineWithStations;
import cn.stylefeng.guns.modular.pipeline.request.PipelineListRequest;
import cn.stylefeng.guns.modular.pipeline.service.PipelineService;
import cn.stylefeng.roses.kernel.rule.enums.ResBizTypeEnum;
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

@RestController
@ApiResource(name = "管线管理", resBizType = ResBizTypeEnum.BUSINESS)
public class PipelineController {

    @Resource
    private PipelineService pipelineService;

    /**
     * 管线列表查询（分页）
     * 支持根据管线名称和关联站场查询
     */
    @GetResource(name = "管线列表查询", path = "/pipeline/list")
    public ResponseData<?> getPipelineList(PipelineListRequest request) {
        return new SuccessResponseData<>(pipelineService.getPipelineList(request));
    }

    /**
     * 新增管线
     */
    @PostResource(name = "新增管线", path = "/pipeline/save")
    public ResponseData<?> savePipeline(@RequestBody TPipelineBaseInfo pipelineBaseInfo) {
        boolean saved = pipelineService.savePipeline(pipelineBaseInfo);
        return new SuccessResponseData<>(saved);
    }

    /**
     * 编辑管线
     */
    @PostResource(name = "编辑管线", path = "/pipeline/update")
    public ResponseData<?> updatePipeline(@RequestBody TPipelineBaseInfo pipelineBaseInfo) {
        boolean updated = pipelineService.updatePipeline(pipelineBaseInfo);
        return new SuccessResponseData<>(updated);
    }

    /**
     * 批量删除管线
     * @param pipelineIds 管线ID数组
     */
    @PostResource(name = "批量删除管线", path = "/pipeline/delete")
    public ResponseData<?> deletePipeline(@RequestBody List<String> pipelineIds) {
        boolean deleted = pipelineService.deletePipeline(pipelineIds);
        return new SuccessResponseData<>(deleted);
    }

    /**
     * 获取所有站场列表（用于查询时的下拉选择）
     * 返回站场ID和名称
     */
    @GetResource(name = "获取所有站场", path = "/pipeline/stations/all")
    public ResponseData<?> getAllStations() {
        return new SuccessResponseData<>(pipelineService.getAllStationsForOption());
    }

    /**
     * 获取管线详情
     * 返回包含站场信息的完整管线对象
     */
    @GetResource(name = "获取管线详情", path = "/pipeline/get")
    public ResponseData<?> getPipelineById(@RequestParam String pipelineId) {
        PipelineWithStations pipelineWithStations = pipelineService.getPipelineById(pipelineId);
        return new SuccessResponseData<>(pipelineWithStations);
    }

    /**
     * 获取所有管线列表
     */
    @GetResource(name = "获取所有管线", path = "/pipeline/all")
    public ResponseData<?> getAllPipelines() {
        return new SuccessResponseData<>(pipelineService.getAllPipelines());
    }


}
