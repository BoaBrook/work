package cn.stylefeng.guns.modular.accesscontrol.controller;

import cn.stylefeng.guns.database.entity.TAccessControlBaseInfo;
import cn.stylefeng.guns.database.entity.TAccessControlEntryExitRecords;
import cn.stylefeng.guns.database.entity.TAccessControlPersonnelBaseInfo;
import cn.stylefeng.guns.database.entity.TDeviceRelationRecords;
import cn.stylefeng.guns.modular.accesscontrol.request.AccessLinkageAlarmRequest;
import cn.stylefeng.guns.modular.accesscontrol.service.AccessControlManageService;
import cn.stylefeng.roses.kernel.db.api.factory.PageResultFactory;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;
import cn.stylefeng.roses.kernel.rule.enums.ResBizTypeEnum;
import cn.stylefeng.roses.kernel.rule.pojo.response.ErrorResponseData;
import cn.stylefeng.roses.kernel.rule.pojo.response.ResponseData;
import cn.stylefeng.roses.kernel.rule.pojo.response.SuccessResponseData;
import cn.stylefeng.roses.kernel.scanner.api.annotation.ApiResource;
import cn.stylefeng.roses.kernel.scanner.api.annotation.GetResource;
import cn.stylefeng.roses.kernel.scanner.api.annotation.PostResource;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@ApiResource(name = "设备管理-门禁系统、门禁人员、门禁记录", resBizType = ResBizTypeEnum.BUSINESS)
public class AccessControlManageController {

    @Resource
    private AccessControlManageService accessControlManageService;

    @GetResource(name = "门禁系统-设备列表-分页查询", path = "/accessControl/manage/device/page")
    public ResponseData<?> getManageDevicePage(PageResult<?> page, TAccessControlBaseInfo query){
        Page<TAccessControlBaseInfo> pageResult = accessControlManageService.getManageDevicePage(page, query);
        if(pageResult == null){
            return new SuccessResponseData<>();
        }
        return new SuccessResponseData<>(PageResultFactory.createPageResult(pageResult));
    }

    @PostResource(name = "门禁系统-设备列表-新增", path = "/accessControl/manage/add")
    public ResponseData<?> addManageDevice(@RequestBody TAccessControlBaseInfo entity){
        accessControlManageService.addManageDevice(entity);
        return new SuccessResponseData<>();
    }

    @GetResource(name = "门禁系统-设备列表-新增验证", path = "/accessControl/manage/add/check")
    public ResponseData<?> addManageDeviceCheck(String belongStationId,String deviceCode,String ipAddress){
        return new SuccessResponseData<>(accessControlManageService.addManageDeviceCheck(belongStationId,deviceCode,ipAddress));
    }

    @PostResource(name = "门禁系统-设备列表-修改", path = "/accessControl/manage/update")
    public ResponseData<?> updateManageDevice(@RequestBody TAccessControlBaseInfo entity){
        accessControlManageService.updateManageDevice(entity);
        return new SuccessResponseData<>();
    }

    @PostResource(name = "门禁系统-设备列表-删除", path = "/accessControl/manage/delete")
    public ResponseData<?> deleteManageDevice(@RequestBody List<String> deviceIds){
        accessControlManageService.deleteManageDevice(deviceIds);
        return new SuccessResponseData<>();
    }

    @PostResource(name = "门禁系统-设备列表-关联设备", path = "/accessControl/manage/connect")
    public ResponseData<?> connectManageDevice(@RequestBody TDeviceRelationRecords entity){
        accessControlManageService.connectManageDevice(entity);
        return new SuccessResponseData<>();
    }

    @GetResource(name = "门禁系统-人员管理-分页查询", path = "/accessControl/manage/person/page")
    public ResponseData<?> getManagePersonPage(PageResult<?> page, TAccessControlPersonnelBaseInfo query){
        Page<TAccessControlPersonnelBaseInfo> pageResult = accessControlManageService.getManagePersonPage(page, query);
        if(pageResult == null){
            return new SuccessResponseData<>();
        }
        return new SuccessResponseData<>(PageResultFactory.createPageResult(pageResult));
    }

    @PostResource(name = "门禁系统-人员管理-新增", path = "/accessControl/person/add")
    public ResponseData<?> addManagePerson(@RequestBody TAccessControlPersonnelBaseInfo entity){
        accessControlManageService.addManagePerson(entity);
        return new SuccessResponseData<>();
    }

    @PostResource(name = "门禁系统-人员管理-修改", path = "/accessControl/person/update")
    public ResponseData<?> updateManagePerson(@RequestBody TAccessControlPersonnelBaseInfo entity){
        accessControlManageService.updateManagePerson(entity);
        return new SuccessResponseData<>();
    }

    @PostResource(name = "门禁系统-人员管理-删除", path = "/accessControl/person/delete")
    public ResponseData<?> deleteManagePerson(@RequestBody List<String> personIds){
        accessControlManageService.deleteManagePerson(personIds);
        return new SuccessResponseData<>();
    }

    @GetResource(name = "门禁系统-门禁记录-分页查询", path = "/accessControl/manage/record/page")
    public ResponseData<?> getManageRecordPage(PageResult<?> page, TAccessControlEntryExitRecords query){
        Page<TAccessControlEntryExitRecords> pageResult = accessControlManageService.getManageRecordPage(page, query);
        if(pageResult == null){
            return new SuccessResponseData<>();
        }
        return new SuccessResponseData<>(PageResultFactory.createPageResult(pageResult));
    }

    @PostResource(name = "门禁系统-门禁记录-联动报警", path = "/accessControl/manage/linkAlarm")
    public ResponseData<?> linkAlarm(@RequestBody AccessLinkageAlarmRequest request){
        return new SuccessResponseData<>(accessControlManageService.linkageAlarm(request));
    }
}
