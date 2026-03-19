package cn.stylefeng.guns.modular.index.controller;

import cn.stylefeng.guns.database.entity.TModelMapManagement;
import cn.stylefeng.guns.database.service.TAlarmResultRecordsService;
import cn.stylefeng.guns.database.service.TModelMapManagementService;
import cn.stylefeng.guns.modular.index.request.AlarmDisposeRequest;
import cn.stylefeng.guns.modular.index.request.AlarmInfoRequest;
import cn.stylefeng.guns.modular.index.request.TagInfoRequest;
import cn.stylefeng.guns.modular.index.service.IndexService;
import cn.stylefeng.roses.kernel.rule.enums.ResBizTypeEnum;
import cn.stylefeng.roses.kernel.rule.pojo.response.ResponseData;
import cn.stylefeng.roses.kernel.rule.pojo.response.SuccessResponseData;
import cn.stylefeng.roses.kernel.scanner.api.annotation.ApiResource;
import cn.stylefeng.roses.kernel.scanner.api.annotation.GetResource;
import cn.stylefeng.roses.kernel.scanner.api.annotation.PostResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ApiResource(name = "大屏首页", resBizType = ResBizTypeEnum.BUSINESS)
public class IndexController {

    @Autowired
    private IndexService indexService;

    @Autowired
    private TModelMapManagementService tModelMapManagementService;

    @Autowired
    private TAlarmResultRecordsService tAlarmResultRecordsService;

    @GetResource(name = "大屏-模型地图", path = "/screen/index/modelMap")
    public ResponseData<?> getModelMap(@RequestParam("stationId") String stationId) {
        return new SuccessResponseData<>(tModelMapManagementService.lambdaQuery().eq(TModelMapManagement::getBelongStationValveChamberId, stationId).one());
    }

    @GetResource(name = "大屏-标签列表", path = "/screen/index/tagList")
    public ResponseData<?> getTagList(@Validated TagInfoRequest request) {
        return new SuccessResponseData<>(indexService.getTagList(request));
    }

    @GetResource(name = "大屏-首页报警信息", path = "/screen/index/alarmInfo")
    public ResponseData<?> getAlarmInfoList(AlarmInfoRequest request) {
        return new SuccessResponseData<>(indexService.getAlarmInfo(request));
    }

    @GetResource(name = "大屏-首页设备列表", path = "/screen/index/deviceList")
    public ResponseData<?> getDeviceList(AlarmInfoRequest request) {
        return new SuccessResponseData<>(indexService.getDeviceList(request));
    }

    @GetResource(name = "大屏-报警详情", path = "/screen/index/alarmDetail")
    public ResponseData<?> getAlarmDetail(@RequestParam("alarmId") String alarmId) {
        return new SuccessResponseData<>(indexService.getAlarmDetail(alarmId));
    }

    @PostResource(name = "大屏-报警处置", path = "/screen/index/alarmDispose")
    public ResponseData<?> alarmDispose(@RequestBody AlarmDisposeRequest request) {
        return new SuccessResponseData<>(indexService.alarmDispose(request));
    }

    @GetResource(name = "大屏-报警总数", path = "/screen/index/alarmTotalNum")
    public ResponseData<?> alarmTotalNum(@RequestParam("stationId") String stationId) {
        return new SuccessResponseData<>(indexService.alarmTotalNum(stationId));
    }

    @PostResource(name = "大屏-报警响应", path = "/screen/index/alarmResponse")
    public ResponseData<?> alarmResponse(@RequestBody AlarmDisposeRequest request) {
        return new SuccessResponseData<>(indexService.alarmResponse(request));
    }

    @GetResource(name = "大屏-当月当日报警统计", path = "/screen/index/alarmNumberStatistics")
    public ResponseData<?> getAlarmStatistics(@RequestParam("systemType") String systemType) {
        return new SuccessResponseData<>(indexService.getAlarmStatistics(systemType));
    }

    @GetResource(name = "大屏-报警统计", path = "/screen/index/alarmSummary")
    public ResponseData<?> alarmStatistics(@RequestParam("stationId") String stationId) {
        return new SuccessResponseData<>(indexService.alarmStatistics(stationId));
    }

    @GetResource(name = "查询区域信息", path = "/screen/index/getAreaInfo")
    public ResponseData<?> getAreaInfo(@RequestParam("stationId") String stationId) {
        return new SuccessResponseData<>(indexService.getAreaInfo(stationId));
    }

}
