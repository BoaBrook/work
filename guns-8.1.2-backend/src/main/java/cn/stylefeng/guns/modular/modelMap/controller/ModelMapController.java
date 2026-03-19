package cn.stylefeng.guns.modular.modelMap.controller;

import cn.stylefeng.guns.core.dto.request.IdsRequest;
import cn.stylefeng.guns.database.entity.TModelMapManagement;
import cn.stylefeng.guns.modular.modelMap.request.ModelMapRequest;
import cn.stylefeng.guns.modular.modelMap.service.ModelMapService;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;
import cn.stylefeng.roses.kernel.file.api.pojo.response.SysFileInfoResponse;
import cn.stylefeng.roses.kernel.rule.enums.ResBizTypeEnum;
import cn.stylefeng.roses.kernel.rule.pojo.response.ResponseData;
import cn.stylefeng.roses.kernel.rule.pojo.response.SuccessResponseData;
import cn.stylefeng.roses.kernel.scanner.api.annotation.ApiResource;
import cn.stylefeng.roses.kernel.scanner.api.annotation.GetResource;
import cn.stylefeng.roses.kernel.scanner.api.annotation.PostResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 模型地图管理控制器
 */
@RestController
@ApiResource(name = "模型地图管理", resBizType = ResBizTypeEnum.BUSINESS)
public class ModelMapController {

    @Autowired
    private ModelMapService modelMapService;

    /**
     * 分页查询模型地图
     */
    @GetResource(name = "模型地图查询", path = "/modelMap/list")
    public ResponseData<PageResult<TModelMapManagement>> getModelMapList(ModelMapRequest request) {
        return new SuccessResponseData<>(modelMapService.getModelMapList(request));
    }

    /**
     * 上传模型文件
     */
    @PostResource(name = "上传模型文件", path = "/modelMap/upload")
    public ResponseData<SysFileInfoResponse> uploadModelFile(@RequestParam("file") MultipartFile file) {
        return new SuccessResponseData<>(modelMapService.uploadModelFile(file));
    }

    /**
     * 更新模型地图（新增/修改）
     */
    @PostResource(name = "更新模型地图", path = "/modelMap/update")
    public ResponseData<?> updateModelMap(@RequestBody TModelMapManagement request) {
        return new SuccessResponseData<>(modelMapService.updateModelMap(request));
    }

    /**
     * 批量删除模型地图
     */
    @PostResource(name = "批量删除模型地图", path = "/modelMap/batchDelete")
    public ResponseData<?> batchDeleteModelMap(@RequestBody IdsRequest request) {
        return new SuccessResponseData<>(modelMapService.batchDeleteModelMap(request));
    }

}
