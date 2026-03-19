package cn.stylefeng.guns.modular.accesscontrol.controller;

import cn.stylefeng.guns.database.entity.TAccessControlBaseInfo;
import cn.stylefeng.guns.database.entity.TAccessControlPersonnelBaseInfo;
import cn.stylefeng.guns.modular.accesscontrol.request.AccessControlGatewayRequest;
import cn.stylefeng.guns.modular.accesscontrol.request.PersonalManageRequest;
import cn.stylefeng.guns.modular.accesscontrol.schedule.AccessControlSchedule;
import cn.stylefeng.guns.modular.accesscontrol.service.AccessControlGatewayService;
import cn.stylefeng.guns.modular.accesscontrol.service.AccessControlService;
import cn.stylefeng.roses.kernel.db.api.factory.PageResultFactory;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;
import cn.stylefeng.roses.kernel.rule.enums.ResBizTypeEnum;
import cn.stylefeng.roses.kernel.rule.pojo.response.ResponseData;
import cn.stylefeng.roses.kernel.rule.pojo.response.SuccessResponseData;
import cn.stylefeng.roses.kernel.scanner.api.annotation.ApiResource;
import cn.stylefeng.roses.kernel.scanner.api.annotation.GetResource;
import cn.stylefeng.roses.kernel.scanner.api.annotation.PostResource;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@ApiResource(name = "门禁系统", resBizType = ResBizTypeEnum.BUSINESS)
public class AccessControlController {

    @Resource
    private AccessControlService  accessControlService;
    @Resource
    private AccessControlSchedule accessControlSchedule;
    @Resource
    private AccessControlGatewayService accessControlGatewayService;

    @GetResource(name = "大屏-门禁-在线数统计", path = "/accessControl/screen/deviceState")
    public ResponseData<?> getScreenAccessControl(String belongStationId){
        return new SuccessResponseData<>(accessControlService.getScreenAccessControl(belongStationId));
    }

    @GetResource(name = "大屏-门禁-进出记录", path = "/accessControl/screen/assessRecords")
    public ResponseData<?> getRecordsOfAccess(String belongStationId){
        return new SuccessResponseData<>(accessControlService.getRecordsOfAccess(belongStationId));
    }

    @GetResource(name = "大屏-门禁-人员统计", path = "/accessControl/screen/staffCount")
    public ResponseData<?> getScreenStaffCount(String belongStationId){
        return new SuccessResponseData<>(accessControlService.getScreenStaffCount(belongStationId));
    }

    @GetResource(name = "大屏-门禁-设备列表", path = "/accessControl/screen/device/page")
    public ResponseData<?> getScreenDevicePage(String belongStationId, PageResult<TAccessControlBaseInfo> page){
        Page<TAccessControlBaseInfo> pageResult = accessControlService.getScreenDevicePage(belongStationId, page);
        if(pageResult == null){
            return new SuccessResponseData<>();
        }
        return new SuccessResponseData<>(PageResultFactory.createPageResult(pageResult));
    }

    @GetResource(name = "门禁系统-在站人员统计", path = "/accessControl/inStation/staffCount")
    public ResponseData<?> getStationStaffCount(String belongStationId){
        return new SuccessResponseData<>(accessControlService.getStationStaffCount(belongStationId));
    }

    @GetResource(name = "门禁系统-人员管理", path = "/accessControl/manage/personal/page")
    public ResponseData<?> getPersonnelPage(String belongStationId, String name, String personnelType,
                                                PageResult<TAccessControlPersonnelBaseInfo> page){
        Page<TAccessControlPersonnelBaseInfo> pageResult = accessControlService.getPersonnelPage(belongStationId, name, personnelType, page);
        if(pageResult == null){
            return new SuccessResponseData<>();
        }
        return new SuccessResponseData<>(PageResultFactory.createPageResult(pageResult));
    }

    @PostResource(name = "门禁系统-人员管理-设置在站/离站", path = "/accessControl/manage/personal/update")
    public ResponseData<?> updatePersonnelPage(@RequestBody PersonalManageRequest request){
        return accessControlService.updatePersonnelPage(request);
    }

    @GetResource(name = "门禁系统-在站人员同步", path = "/accessControl/inStation/sync")
    public void getStationStaffSync(){
        accessControlSchedule.syncPersonalData();
    }

    @GetResource(name = "门禁系统-门禁记录同步", path = "/accessControl/inStation/records/sync")
    public void getStationStaffSyncRecords(){
        accessControlSchedule.syncAccessRecordsData();
    }

    @PostResource(name = "门禁系统-设置门状态", path = "/accessControl/gateway")
    public ResponseData<?> remoteControlGate(@RequestBody AccessControlGatewayRequest request){
        accessControlGatewayService.remoteControlGate(request);
        return new SuccessResponseData<>();
    }
}
