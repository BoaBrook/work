package cn.stylefeng.guns.modular.hikvision.service;

import cn.stylefeng.guns.modular.hikvision.request.*;
import cn.stylefeng.guns.modular.hikvision.response.*;
import cn.stylefeng.guns.modular.hikvision.service.impl.HikVisionServiceImpl;

import java.util.List;

/**
 * 海康威视SDK服务接口
 */
public interface HikVisionService {

    /**
     * 云台控制
     *
     * @param request 云台控制请求
     * @return 是否成功
     */
    Boolean ptzControl(PtzControlRequest request);

//    /**
//     * 开始实时预览
//     *
//     * @param request 预览请求
//     * @return 预览响应
//     */
//    PreviewResponse startPreview(PreviewRequest request);

    /**
     * 停止实时预览
     *
     * @param previewHandle 预览句柄
     * @return 是否成功
     */
    Boolean stopPreview(Integer previewHandle);

//    /**
//     * 按时间回放录像
//     *
//     * @param request 回放请求
//     * @return 回放响应
//     */
//    PlaybackResponse startPlayback(PlaybackRequest request);

    /**
     * 回放控制
     *
     * @param playbackHandle 回放句柄
     * @param command        控制命令
     * @param position       播放进度(可选)
     * @return 是否成功
     */
    Boolean playbackControl(Integer playbackHandle, String command, Integer position);

    /**
     * 停止回放
     *
     * @param playbackHandle 回放句柄
     * @return 是否成功
     */
    Boolean stopPlayback(Integer playbackHandle);

//    /**
//     * 查询录像文件列表
//     *
//     * @param request 回放请求
//     * @return 录像文件列表
//     */
//    List<RecordFileResponse> queryRecordFiles(PlaybackRequest request);

    /**
     * 设置预置点
     *
     * @param request 预置点请求
     * @return 是否成功
     */
    Boolean setPreset(PresetRequest request);

    /**
     * 转到预置点
     *
     * @param request 预置点请求
     * @return 是否成功
     */
    Boolean gotoPreset(PresetRequest request);

    /**
     * 删除预置点
     *
     * @param request 预置点请求
     * @return 是否成功
     */
    Boolean removePreset(PresetRequest request);

    /**
     * 查询预置点列表
     *
     * @param deviceId 设备ID
     * @return 预置点列表
     */
    List<PresetResponse> queryPresets(String deviceId);

    /**
     * 添加巡航点
     *
     * @param request 巡航请求
     * @return 是否成功
     */
    Boolean addCruisePoint(CruiseRequest request);

    /**
     * 删除巡航点
     *
     * @param request 巡航请求
     * @return 是否成功
     */
    Boolean removeCruisePoint(CruiseRequest request);

    /**
     * 开始巡航
     *
     * @param request 巡航请求
     * @return 是否成功
     */
    Boolean startCruise(CruiseRequest request);

    /**
     * 停止巡航
     *
     * @param request 巡航请求
     * @return 是否成功
     */
    Boolean stopCruise(CruiseRequest request);

    /**
     * 查询巡航参数
     *
     * @param request 巡航请求
     * @return 巡航信息
     */
    CruiseResponse queryCruise(CruiseRequest request);

    /**
     * 设备登录
     *
     * @param deviceId 设备ID
     * @return 登录句柄
     */
    HikVisionServiceImpl.LoginInfo loginDevice(String deviceId);

    /**
     * 设备登出
     *
     * @param userId 用户ID
     */
    void logoutDevice(int userId);

    byte[] snapshot(String deviceId);

}
