package cn.stylefeng.guns.modular.hikvision.controller;

import cn.stylefeng.guns.modular.hikvision.request.CruiseRequest;
import cn.stylefeng.guns.modular.hikvision.request.PresetRequest;
import cn.stylefeng.guns.modular.hikvision.request.PtzControlRequest;
import cn.stylefeng.guns.modular.hikvision.service.HikVisionService;
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

/**
 * 海康威视SDK接口控制器
 */
@RestController
@ApiResource(name = "海康威视SDK接口", resBizType = ResBizTypeEnum.BUSINESS)
public class HikVisionController {

    @Autowired
    private HikVisionService hikVisionService;

    // ==================== 云台控制模块 ====================

    @PostResource(name = "云台控制", path = "/hikvision/ptz/control")
    public ResponseData<?> ptzControl(@RequestBody PtzControlRequest request) {
        return new SuccessResponseData<>(hikVisionService.ptzControl(request));
    }

    // ==================== 实时预览模块 ====================

//    @PostResource(name = "开始实时预览", path = "/hikvision/preview/start")
//    public ResponseData<?> startPreview(@RequestBody PreviewRequest request) {
//        return new SuccessResponseData<>(hikVisionService.startPreview(request));
//    }

    @PostResource(name = "停止实时预览", path = "/hikvision/preview/stop")
    public ResponseData<?> stopPreview(@RequestParam("previewHandle") Integer previewHandle) {
        return new SuccessResponseData<>(hikVisionService.stopPreview(previewHandle));
    }

    // ==================== 录像回放模块 ====================

//    @PostResource(name = "开始录像回放", path = "/hikvision/playback/start")
//    public ResponseData<?> startPlayback(@RequestBody PlaybackRequest request) {
//        return new SuccessResponseData<>(hikVisionService.startPlayback(request));
//    }

    @PostResource(name = "录像回放控制", path = "/hikvision/playback/control")
    public ResponseData<?> playbackControl(@RequestParam("playbackHandle") Integer playbackHandle,
                                           @RequestParam("command") String command,
                                           @RequestParam(value = "position", required = false) Integer position) {
        return new SuccessResponseData<>(hikVisionService.playbackControl(playbackHandle, command, position));
    }

    @PostResource(name = "停止录像回放", path = "/hikvision/playback/stop")
    public ResponseData<?> stopPlayback(@RequestParam("playbackHandle") Integer playbackHandle) {
        return new SuccessResponseData<>(hikVisionService.stopPlayback(playbackHandle));
    }

//    @PostResource(name = "查询录像文件列表", path = "/hikvision/playback/queryFiles")
//    public ResponseData<?> queryRecordFiles(@RequestBody PlaybackRequest request) {
//        return new SuccessResponseData<>(hikVisionService.queryRecordFiles(request));
//    }

    // ==================== 预置点控制模块 ====================

    @PostResource(name = "设置预置点", path = "/hikvision/preset/set")
    public ResponseData<?> setPreset(@RequestBody PresetRequest request) {
        return new SuccessResponseData<>(hikVisionService.setPreset(request));
    }

    @PostResource(name = "转到预置点", path = "/hikvision/preset/goto")
    public ResponseData<?> gotoPreset(@RequestBody PresetRequest request) {
        return new SuccessResponseData<>(hikVisionService.gotoPreset(request));
    }

    @PostResource(name = "删除预置点", path = "/hikvision/preset/remove")
    public ResponseData<?> removePreset(@RequestBody PresetRequest request) {
        return new SuccessResponseData<>(hikVisionService.removePreset(request));
    }

    @GetResource(name = "查询预置点列表", path = "/hikvision/preset/query")
    public ResponseData<?> queryPresets(@RequestParam("deviceId") String deviceId) {
        return new SuccessResponseData<>(hikVisionService.queryPresets(deviceId));
    }

    // ==================== 云台巡航模块 ====================

    @PostResource(name = "添加巡航点", path = "/hikvision/cruise/addPoint")
    public ResponseData<?> addCruisePoint(@RequestBody CruiseRequest request) {
        return new SuccessResponseData<>(hikVisionService.addCruisePoint(request));
    }

    @PostResource(name = "删除巡航点", path = "/hikvision/cruise/removePoint")
    public ResponseData<?> removeCruisePoint(@RequestBody CruiseRequest request) {
        return new SuccessResponseData<>(hikVisionService.removeCruisePoint(request));
    }

    @PostResource(name = "开始巡航", path = "/hikvision/cruise/start")
    public ResponseData<?> startCruise(@RequestBody CruiseRequest request) {
        return new SuccessResponseData<>(hikVisionService.startCruise(request));
    }

    @PostResource(name = "停止巡航", path = "/hikvision/cruise/stop")
    public ResponseData<?> stopCruise(@RequestBody CruiseRequest request) {
        return new SuccessResponseData<>(hikVisionService.stopCruise(request));
    }

    @PostResource(name = "查询巡航参数", path = "/hikvision/cruise/query")
    public ResponseData<?> queryCruise(@RequestBody CruiseRequest request) {
        return new SuccessResponseData<>(hikVisionService.queryCruise(request));
    }

    // ==================== 设备管理模块 ====================

    @PostResource(name = "设备登录", path = "/hikvision/device/login")
    public ResponseData<?> loginDevice(@RequestParam("deviceId") String deviceId) {
        return new SuccessResponseData<>(hikVisionService.loginDevice(deviceId));
    }

    @PostResource(name = "设备登出", path = "/hikvision/device/logout")
    public ResponseData<?> logoutDevice(@RequestParam("userId") Integer userId) {
        hikVisionService.logoutDevice(userId);
        return new SuccessResponseData<>();
    }

    @GetResource(name = "设备抓图", path = "/hikvision/device/snapshot")
    public ResponseData<?> snapshot(@RequestParam("deviceId") String deviceId) {
        return new SuccessResponseData<>(hikVisionService.snapshot(deviceId));
    }

}
