package cn.stylefeng.guns.modular.industrialTV.controller;

import cn.stylefeng.guns.core.dto.request.IdsRequest;
import cn.stylefeng.guns.modular.industrialTV.request.PresetUpdateRequest;
import cn.stylefeng.guns.modular.industrialTV.service.PresetService;
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

@RestController
@ApiResource(name = "预设位管理", resBizType = ResBizTypeEnum.BUSINESS)
public class PresetController {

    @Autowired
    private PresetService presetService;

    @GetResource(name = "预设位查询", path = "/preset/query")
    public ResponseData<?> presetQuery(@RequestParam("deviceId") String deviceId){
        return new SuccessResponseData<>(presetService.presetQuery(deviceId));
    }

    @PostResource(name = "预设位更新", path = "/preset/update")
    public ResponseData<?> presetUpdate(@RequestBody PresetUpdateRequest request){
        return new SuccessResponseData<>(presetService.presetUpdate(request));
    }

    @PostResource(name = "预设位删除", path = "/preset/batchDelete")
    public ResponseData<?> presetBatchDelete(@RequestBody IdsRequest request){
        return new SuccessResponseData<>(presetService.presetBatchDelete(request.getIdList()));
    }

}
