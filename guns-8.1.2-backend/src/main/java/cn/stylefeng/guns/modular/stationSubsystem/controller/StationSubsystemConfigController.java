package cn.stylefeng.guns.modular.stationSubsystem.controller;

import cn.stylefeng.guns.modular.stationSubsystem.dto.StationSubsystemConfigListRequest;
import cn.stylefeng.guns.modular.stationSubsystem.dto.StationSubsystemConfigResponse;
import cn.stylefeng.guns.modular.stationSubsystem.dto.StationSubsystemConfigSaveRequest;
import cn.stylefeng.guns.modular.stationSubsystem.service.StationSubsystemConfigService;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;
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

/**
 * 站场子系统配置管理
 */
@RestController
@ApiResource(name = "站场子系统配置", resBizType = ResBizTypeEnum.BUSINESS)
public class StationSubsystemConfigController {

    @Resource
    private StationSubsystemConfigService stationSubsystemConfigService;

    /**
     * 分页获取站场及其子系统配置列表
     */
    @GetResource(name = "站场子系统配置-站场列表", path = "/stationSubsystemConfig/list")
    public ResponseData<PageResult<StationSubsystemConfigResponse>> list(StationSubsystemConfigListRequest request) {
        return new SuccessResponseData<>(stationSubsystemConfigService.pageList(request));
    }

    /**
     * 获取站场当前已配置的子系统类型详情
     */
    @GetResource(name = "站场子系统配置-获取配置", path = "/stationSubsystemConfig/getConfig")
    public ResponseData<List<String>> getConfig(@RequestParam("stationId") String stationId) {
        return new SuccessResponseData<>(stationSubsystemConfigService.getConfig(stationId));
    }

    /**
     * 覆保存站场的子系统配置：
     */
    @PostResource(name = "站场子系统配置-保存", path = "/stationSubsystemConfig/save")
    public ResponseData<Boolean> save(@RequestBody StationSubsystemConfigSaveRequest request) {
        return new SuccessResponseData<>(stationSubsystemConfigService.save(request));
    }
}

