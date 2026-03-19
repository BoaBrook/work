package cn.stylefeng.guns.modular.deviceRelation.controller;

import cn.stylefeng.guns.modular.deviceRelation.service.DeviceRelationService;
import cn.stylefeng.guns.modular.deviceRelation.entity.CurrentAssociationsDTO;
import cn.stylefeng.guns.modular.deviceRelation.entity.DeviceRelationSaveDTO;
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
import java.util.Map;

/**
 * 设备关联
 */
@RestController
@ApiResource(name = "设备关联管理（列表）", resBizType = ResBizTypeEnum.BUSINESS)
public class DeviceRelationController {

    @Resource
    private DeviceRelationService deviceRelationService;

    /**
     * 工业电视列表
     */
    @GetResource(name = "关联设备-工业电视列表", path = "/deviceRelation/industrialTvList")
    public ResponseData<PageResult<?>> industrialTvList(@RequestParam Map<String, Object> params) {
        return new SuccessResponseData<>(deviceRelationService.listIndustrialTvForRelation(params));
    }

    /**
     * 门禁设备列表
     */
    @GetResource(name = "关联设备-门禁列表", path = "/deviceRelation/accessControlList")
    public ResponseData<PageResult<?>> accessControlList(@RequestParam Map<String, Object> params) {
        return new SuccessResponseData<>(deviceRelationService.listAccessControlForRelation(params));
    }

    /**
     * 应急广播设备列表
     */
    @GetResource(name = "关联设备-应急广播列表", path = "/deviceRelation/emergencyBroadcastList")
    public ResponseData<PageResult<?>> emergencyBroadcastList(@RequestParam Map<String, Object> params) {
        return new SuccessResponseData<>(deviceRelationService.listEmergencyBroadcastForRelation(params));
    }

    /**
     * 保存设备关联关系
     */
    @PostResource(name = "保存设备关联关系", path = "/deviceRelation/saveRelations")
    public ResponseData<?> saveRelations(@RequestBody DeviceRelationSaveDTO dto) {
        return new SuccessResponseData<>(deviceRelationService.saveRelations(dto));
    }

    /**
     * 获取当前设备已关联设备
     */
    @GetResource(name = "获取当前设备已关联设备", path = "/deviceRelation/currentAssociations")
    public ResponseData<CurrentAssociationsDTO> currentAssociations(
            @RequestParam("subsystemType") String subsystemType,
            @RequestParam("relatedDeviceId") String relatedDeviceId) {
        return new SuccessResponseData<>(deviceRelationService.getCurrentAssociations(subsystemType, relatedDeviceId));
    }
}

