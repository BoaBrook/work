package cn.stylefeng.guns.modular.industrialTV.controller;

import cn.stylefeng.guns.database.entity.TIndustrialTvRollPoling;
import cn.stylefeng.guns.modular.industrialTV.request.*;
import cn.stylefeng.guns.modular.industrialTV.service.IndustrialTVService;
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
@ApiResource(name = "工业电视", resBizType = ResBizTypeEnum.BUSINESS)
public class IndustrialTVController {

    @Autowired
    private IndustrialTVService industrialTVService;

    @GetResource(name = "大屏-实时监控", path = "/industrialTV/screen/realTime/monitor")
    public ResponseData<?> getRealTimeMonitor(@RequestParam("stationId") String stationId, @RequestParam(value = "deviceName",required = false) String deviceName){
        return new SuccessResponseData<>(industrialTVService.getRealTimeMonitor(stationId,deviceName));
    }

    @GetResource(name = "大屏-在线统计", path = "/industrialTV/screen/onlineStatistics")
    public ResponseData<?> getOnlineStatistics(@RequestParam("stationId") String stationId){
        return new SuccessResponseData<>(industrialTVService.getOnlineStatistics(stationId));
    }

    @GetResource(name = "轮询计划", path = "/industrialTV/pollingPlan")
    public ResponseData<?> getPollingPlan(RollPolingRequest request){
        return new SuccessResponseData<>(industrialTVService.getPollingPlan(request));
    }

    @PostResource(name = "编辑轮询计划", path = "/industrialTV/pollingPlan/update")
    public ResponseData<?> editPollingPlan(@RequestBody TIndustrialTvRollPoling request){
        return new SuccessResponseData<>(industrialTVService.editPollingPlan(request));
    }

    @PostResource(name = "删除轮询计划", path = "/industrialTV/pollingPlan/delete")
    public ResponseData<?> deletePollingPlan(@RequestBody TIndustrialTvRollPoling request){
        return new SuccessResponseData<>(industrialTVService.deletePollingPlan(request));
    }

    @PostResource(name = "轮询控制", path = "/industrialTV/pollingPlan/control")
    public ResponseData<?> controlPollingPlan(@RequestBody PollingPlanControlRequest request){
        return new SuccessResponseData<>(industrialTVService.controlPollingPlan(request));
    }

    @GetResource(name = "查询轮询状态", path = "/industrialTV/pollingPlan/getStatus")
    public ResponseData<?> pollingPlanStatus(@RequestParam("stationId") String stationId){
        return new SuccessResponseData<>(industrialTVService.pollingPlanStatus(stationId));
    }

    @GetResource(name = "查询重点区域监控", path = "/industrialTV/importantAreaMonitor")
    public ResponseData<?> getImportantAreaMonitor(@RequestParam("stationId") String stationId){
        return new SuccessResponseData<>(industrialTVService.getImportantAreaMonitor(stationId));
    }

    @GetResource(name = "查询设备历史录像", path = "/industrialTV/video/history")
    public ResponseData<?> getDeviceHistoryVideo(DeviceHistoryVideoRequest request){
        return new SuccessResponseData<>(industrialTVService.getDeviceHistoryVideo(request));
    }

    @PostResource(name = "工业电视云台控制", path = "/industrialTV/control")
    public ResponseData<?> industrialTVControl(@RequestBody ControlPtzRequest request){
        return new SuccessResponseData<>(industrialTVService.industrialTVControl(request));
    }

    @PostResource(name = "工业电视预置点控制", path = "/industrialTV/control/preset")
    public ResponseData<?> industrialTVControlPreset(@RequestBody ControlPresetRequest request){
        return new SuccessResponseData<>(industrialTVService.industrialTVControlPreset(request));
    }

    @GetResource(name = "按摄像头类型分组查询工业电视", path = "/industrialTV/groupByCameraType")
    public ResponseData<?> getIndustrialTVGroupByCameraType(){
        return new SuccessResponseData<>(industrialTVService.getIndustrialTVGroupByCameraType());
    }

    /**
     * 工业电视联动报警
     *
     * 当工业电视产生报警时，根据联动配置执行以下操作：
     * 1. 控制关联摄像头转到指定预设位
     * 2. 如果配置了抓图，则进行抓图
     * 3. 如果配置了播放音频，则播放指定音频
     *
     * @param request 联动报警请求（工业电视ID和报警类型编码）
     * @return 是否成功
     */
    @PostResource(name = "工业电视联动报警", path = "/industrialTV/linkageAlarm")
    public ResponseData<?> linkageAlarm(@RequestBody LinkageAlarmRequest request){
        return new SuccessResponseData<>(industrialTVService.linkageAlarm(request));
    }

    @PostResource(name = "上报省级平台设备清单", path = "/industrialTV/reportToProvince")
    public ResponseData<?> reportToProvince(){
        return new SuccessResponseData<>(industrialTVService.reportToProvince());
    }

}
