package cn.stylefeng.guns.modular.tagmanagement.controller;

import cn.stylefeng.guns.database.entity.TTagManagement;
import cn.stylefeng.guns.modular.tagmanagement.request.TagManagementListRequest;
import cn.stylefeng.guns.modular.tagmanagement.service.TagManagementQueryService;
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

@RestController
@ApiResource(name = "标签管理", resBizType = ResBizTypeEnum.BUSINESS)
public class TagManagementController {

    @Resource
    private TagManagementQueryService tagManagementQueryService;

    @GetResource(name = "标签管理-列表查询", path = "/tagManagement/list")
    public ResponseData<?> getTagList(TagManagementListRequest request) {
        return new SuccessResponseData<>(tagManagementQueryService.getTagList(request));
    }

    @GetResource(name = "标签管理-子系统类型下拉", path = "/tagManagement/subsystemOptions")
    public ResponseData<?> subsystemOptions() {
        return new SuccessResponseData<>(tagManagementQueryService.subsystemOptions());
    }

    @GetResource(name = "标签管理-设备下拉", path = "/tagManagement/deviceOptions")
    public ResponseData<?> deviceOptions(@RequestParam(value = "belongStationId", required = false) String belongStationId,
                                         @RequestParam(value = "subsystemType", required = false) String subsystemType) {
        return new SuccessResponseData<>(tagManagementQueryService.deviceOptions(belongStationId, subsystemType));
    }

    @GetResource(name = "标签管理-模型下拉", path = "/modelOptions")
    public ResponseData<?> modelOptions(@RequestParam(value = "belongStationId", required = false) String belongStationId) {
        return new SuccessResponseData<>(tagManagementQueryService.modelOptions(belongStationId));
    }

    @PostResource(name = "标签管理-新增", path = "/tagManagement/add")
    public ResponseData<?> add(@RequestBody TTagManagement request) {
        return new SuccessResponseData<>(tagManagementQueryService.addTag(request));
    }

    @PostResource(name = "标签管理-编辑", path = "/tagManagement/update")
    public ResponseData<?> update(@RequestBody TTagManagement request) {
        return new SuccessResponseData<>(tagManagementQueryService.updateTag(request));
    }

    @PostResource(name = "标签管理-删除", path = "/tagManagement/delete")
    public ResponseData<?> delete(@RequestParam("tagId") String tagId) {
        return new SuccessResponseData<>(tagManagementQueryService.deleteTag(tagId));
    }
}
