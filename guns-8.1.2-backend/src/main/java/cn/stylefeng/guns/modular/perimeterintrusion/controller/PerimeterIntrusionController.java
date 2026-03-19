package cn.stylefeng.guns.modular.perimeterintrusion.controller;

import cn.stylefeng.guns.database.entity.TPerimeterIntrusionHostBaseInfo;
import cn.stylefeng.guns.database.entity.TPerimeterIntrusionZoneBaseInfo;
import cn.stylefeng.guns.modular.linkagealarm.request.LinkageAlarmRequest;
import cn.stylefeng.guns.modular.perimeterintrusion.dto.*;
import cn.stylefeng.guns.modular.perimeterintrusion.service.PerimeterIntrusionService;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;
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
@ApiResource(name = "周界入侵", resBizType = ResBizTypeEnum.BUSINESS)
public class PerimeterIntrusionController {

    @Autowired
    private PerimeterIntrusionService perimeterIntrusionService;

    /**
     * 后台管理 - 周界入侵主机设备基础信息查询（分页）
     */
    @GetResource(name = "周界入侵主机设备基础信息查询", path = "/perimeter/host/list")
    public ResponseData<PageResult<TPerimeterIntrusionHostBaseInfo>> getHostBaseInfoList(HostBaseInfoRequest request) {
        return new SuccessResponseData<>(perimeterIntrusionService.getHostBaseInfoList(request));
    }

    /**
     * 后台管理 - 新增周界入侵主机
     */
    @PostResource(name = "新增周界入侵主机", path = "/perimeter/host/add")
    public ResponseData<?> addHost(@RequestBody HostAddRequest request) {
        return new SuccessResponseData<>(perimeterIntrusionService.addHost(request));
    }

    /**
     * 后台管理 - 修改周界入侵主机
     */
    @PostResource(name = "修改周界入侵主机", path = "/perimeter/host/update")
    public ResponseData<?> updateHost(@RequestBody HostUpdateRequest request) {
        return new SuccessResponseData<>(perimeterIntrusionService.updateHost(request));
    }

    /**
     * 后台管理 - 删除周界入侵主机
     */
    @PostResource(name = "删除周界入侵主机", path = "/perimeter/host/delete")
    public ResponseData<?> deleteHost(@RequestParam("deviceId") String deviceId) {
        return new SuccessResponseData<>(perimeterIntrusionService.deleteHost(deviceId));
    }

    /**
     * 后台管理 - 周界入侵防区基础信息查询（分页）
     */
    @GetResource(name = "周界入侵防区基础信息查询", path = "/perimeter/zone/list")
    public ResponseData<PageResult<TPerimeterIntrusionZoneBaseInfo>> getZoneBaseInfoList(ZoneBaseInfoRequest request) {
        return new SuccessResponseData<>(perimeterIntrusionService.getZoneBaseInfoList(request));
    }

    /**
     * 后台管理 - 新增周界入侵防区
     */
    @PostResource(name = "新增周界入侵防区", path = "/perimeter/zone/add")
    public ResponseData<?> addZone(@RequestBody ZoneAddRequest request) {
        return new SuccessResponseData<>(perimeterIntrusionService.addZone(request));
    }

    /**
     * 后台管理 - 修改周界入侵防区
     */
    @PostResource(name = "修改周界入侵防区", path = "/perimeter/zone/update")
    public ResponseData<?> updateZone(@RequestBody ZoneUpdateRequest request) {
        return new SuccessResponseData<>(perimeterIntrusionService.updateZone(request));
    }

    /**
     * 后台管理 - 删除周界入侵防区
     */
    @PostResource(name = "删除周界入侵防区", path = "/perimeter/zone/delete")
    public ResponseData<?> deleteZone(@RequestParam("zoneId") String zoneId) {
        return new SuccessResponseData<>(perimeterIntrusionService.deleteZone(zoneId));
    }

    /**
     * 大屏 - 防区布防/撤防
     */
    @PostResource(name = "防区布防/撤防", path = "/perimeter/zone/arm")
    public ResponseData<?> armZone(@RequestBody ZoneArmedRequest request) {
        return new SuccessResponseData<>(perimeterIntrusionService.armZone(request));
    }

    /**
     * 大屏-周界主机防区查询（分页）
     */
    @GetResource(name = "大屏-周界主机防区查询", path = "/perimeter/hostZone/list")
    public ResponseData<PageResult<HostZoneScreenResponse>> getHostZoneList(HostZoneQueryRequest request) {
        return new SuccessResponseData<>(perimeterIntrusionService.getHostZoneList(request));
    }

    /**
     * 大屏-周界主机详情查询
     */
    @GetResource(name = "大屏-周界主机详情", path = "/perimeter/host/detail")
    public ResponseData<HostDetailScreenResponse> getHostDetail(HostDetailQueryRequest request) {
        return new SuccessResponseData<>(perimeterIntrusionService.getHostDetail(request));
    }

    /**
     * 大屏-防区详情查询
     */
    @GetResource(name = "大屏-防区详情", path = "/perimeter/zone/detail")
    public ResponseData<ZoneDetailScreenResponse> getZoneDetail(ZoneDetailQueryRequest request) {
        return new SuccessResponseData<>(perimeterIntrusionService.getZoneDetail(request));
    }

    /**
     * 大屏 - 周界主机在线状态统计
     */
    @GetResource(name = "大屏 - 周界主机在线状态统计", path = "/perimeter/host/online")
    public ResponseData<HostStatusResponse> getHostOnlineStatus(String stationId) {
        return new SuccessResponseData<>(perimeterIntrusionService.getHostOnlineStatus(stationId));
    }

    /**
     * 校验设备编码唯一性
     *
     * 用于新增和修改时，校验同一站场下的设备编码是否唯一
     *
     * @param belongStationId 站场ID
     * @param deviceCode 设备编码
     * @param deviceId 设备ID（编辑时传入，用于排除自身；新增时可不传）
     * @param deviceType 设备类型
     * @return true-唯一（可以使用），false-不唯一（已存在）
     */
    @GetResource(name = "校验设备编码唯一性", path = "/perimeter/host/checkDeviceCodeUnique")
    public ResponseData<Boolean> checkDeviceCodeUnique(
            @RequestParam("belongStationId") String belongStationId,
            @RequestParam(value = "deviceCode", required = false) String deviceCode,
            @RequestParam(value = "deviceIp", required = false) String deviceIp,
            @RequestParam("deviceType") String  deviceType,
            @RequestParam(value = "deviceId", required = false) String deviceId) {
        return new SuccessResponseData<>(perimeterIntrusionService.checkDeviceCodeUnique(belongStationId, deviceCode, deviceIp, deviceType, deviceId));
    }

    /**
     * 周界入侵联动报警
     *
     * 当周界入侵设备产生报警时，根据联动配置执行以下操作：
     * 1. 控制关联摄像头转到指定预设位
     * 2. 如果配置了抓图，则进行抓图
     * 3. 如果配置了播放音频，则播放指定音频
     *
     * @param request 联动报警请求（周界入侵主机ID和报警类型编码）
     * @return 是否成功
     */
    @PostResource(name = "周界入侵联动报警", path = "/perimeter/linkageAlarm")
    public ResponseData<?> linkageAlarm(@RequestBody LinkageAlarmRequest request){
        return new SuccessResponseData<>(perimeterIntrusionService.linkageAlarm(request));
    }

}
