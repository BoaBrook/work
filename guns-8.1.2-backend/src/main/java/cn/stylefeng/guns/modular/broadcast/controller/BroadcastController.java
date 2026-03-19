package cn.stylefeng.guns.modular.broadcast.controller;

import cn.stylefeng.guns.core.dto.request.IdsRequest;
import cn.stylefeng.guns.database.entity.TEmergencyBroadcastHostBaseInfo;
import cn.stylefeng.guns.database.entity.TVoiceBroadcastMaterialBaseInfo;
import cn.stylefeng.guns.database.service.TVoiceBroadcastMaterialBaseInfoService;
import cn.stylefeng.guns.modular.broadcast.request.BroadcastHostRequest;
import cn.stylefeng.guns.modular.broadcast.request.PlayVoiceRequest;
import cn.stylefeng.guns.modular.broadcast.service.BroadcastService;
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

import java.io.IOException;

@RestController
@ApiResource(name = "应急广播", resBizType = ResBizTypeEnum.BUSINESS)
public class BroadcastController {

    @Autowired
    private BroadcastService broadcastService;

    @Autowired
    private TVoiceBroadcastMaterialBaseInfoService tVoiceBroadcastMaterialBaseInfoService;

    @GetResource(name = "大屏-应急广播", path = "/broadcast/screen/deviceList")
    public ResponseData<?> getScreenBroadCast(@RequestParam("stationId") String stationId){
        return new SuccessResponseData<>(broadcastService.getScreenBroadCast(stationId));
    }

    @GetResource(name = "大屏-音频列表", path = "/broadcast/screen/voiceList")
    public ResponseData<?> getScreenVoiceList(@RequestParam("stationId") String stationId){
        return new SuccessResponseData<>(tVoiceBroadcastMaterialBaseInfoService.lambdaQuery().eq(TVoiceBroadcastMaterialBaseInfo::getBelongStationId, stationId).list());
    }

    @PostResource(name = "大屏-播放音频", path = "/broadcast/screen/playVoice")
    public ResponseData<?> playVoice(@RequestBody PlayVoiceRequest request) throws IOException {
        return new SuccessResponseData<>(broadcastService.playVoice(request));
    }

    @GetResource(name = "大屏-在线统计", path = "/broadcast/screen/onlineStatistics")
    public ResponseData<?> getScreenOnlineStatistics(@RequestParam("stationId") String stationId){
        return new SuccessResponseData<>(broadcastService.getScreenOnlineStatistics(stationId));
    }

    @PostResource(name = "应急广播主机更新", path = "/broadcast/host/update")
    public ResponseData<?> updateBroadcastHost(@RequestBody TEmergencyBroadcastHostBaseInfo request){
        return new SuccessResponseData<>(broadcastService.updateBroadcastHost(request));
    }

    @GetResource(name = "应急广播主机查询", path = "/broadcast/host/list")
    public ResponseData<PageResult<TEmergencyBroadcastHostBaseInfo>> getBroadcastHostList(BroadcastHostRequest request){
        return new SuccessResponseData<>(broadcastService.getBroadcastHostList(request));
    }

    @PostResource(name = "应急广播主机批量删除", path = "/broadcast/host/batchDelete")
    public ResponseData<?> batchDeleteBroadcastHost(@RequestBody IdsRequest request){
        return new SuccessResponseData<>(broadcastService.batchDeleteBroadcastHost(request.getIdList()));
    }

    /**
     * 校验设备编码唯一性
     *
     * 用于新增和修改时，校验同一站场下的设备编码是否唯一
     *
     * @param belongStationId 站场ID
     * @param deviceCode 设备编码
     * @param deviceId 设备ID（编辑时传入，用于排除自身；新增时可不传）
     * @return true-唯一（可以使用），false-不唯一（已存在）
     */
    @GetResource(name = "校验设备编码唯一性", path = "/broadcast/host/checkDeviceCodeUnique")
    public ResponseData<Boolean> checkDeviceCodeUnique(
            @RequestParam("belongStationId") String belongStationId,
            @RequestParam(value = "deviceCode", required = false) String deviceCode,
            @RequestParam(value = "deviceIp", required = false) String deviceIp,
            @RequestParam(value = "deviceId", required = false) String deviceId) {
        return new SuccessResponseData<>(broadcastService.checkDeviceCodeUnique(belongStationId, deviceCode, deviceIp, deviceId));
    }

}
