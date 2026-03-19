package cn.stylefeng.guns.modular.hikvision.service.impl;

import cn.stylefeng.guns.database.entity.TIndustrialTvBaseInfo;
import cn.stylefeng.guns.database.service.TIndustrialTvBaseInfoService;
import cn.stylefeng.guns.modular.accesscontrol.hikClient.HikSdkManager;
import cn.stylefeng.guns.modular.hikvision.NetSDKDemo.HCNetSDK;
import cn.stylefeng.guns.modular.hikvision.request.CruiseRequest;
import cn.stylefeng.guns.modular.hikvision.request.PresetRequest;
import cn.stylefeng.guns.modular.hikvision.request.PtzControlRequest;
import cn.stylefeng.guns.modular.hikvision.response.CruiseResponse;
import cn.stylefeng.guns.modular.hikvision.response.PresetResponse;
import cn.stylefeng.guns.modular.hikvision.service.HikVisionService;
import cn.stylefeng.guns.modular.hikvision.utils.RtspUrlParser;
import cn.stylefeng.roses.kernel.rule.exception.base.ServiceException;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 海康威视SDK服务实现类
 */
@Slf4j
@Service
public class HikVisionServiceImpl implements HikVisionService {

    private HCNetSDK hCNetSDK;

    @Autowired
    private HikSdkManager hikSdkManager;

    @Autowired
    private TIndustrialTvBaseInfoService tIndustrialTvBaseInfoService;

    /**
     * 设备登录句柄缓存 (deviceId -> userId)
     */
    private final Map<String, Integer> deviceLoginMap = new ConcurrentHashMap<>();

    /**
     * 预览句柄缓存 (previewHandle -> deviceId)
     */
    private final Map<Integer, String> previewHandleMap = new ConcurrentHashMap<>();

    /**
     * 回放句柄缓存 (playbackHandle -> deviceId)
     */
    private final Map<Integer, String> playbackHandleMap = new ConcurrentHashMap<>();

    /**
     * 设备云台控制命令缓存 (deviceId -> lastCommand)
     * 用于记录每个设备最后一次执行的云台控制命令
     */
    private final Map<String, String> devicePtzCommandMap = new ConcurrentHashMap<>();

    private static final String WIN_DLL_PATH = System.getProperty("user.dir") + "\\guns-8.1.2-backend\\lib\\win\\HCNetSDK.dll";
    private static final String LINUX_SO_PATH = "/service/app/lib/linux/libhcnetsdk.so";

    @Data
    @AllArgsConstructor
    public static class LoginInfo {
        int channelNo;
        int userId;
    }

    @PostConstruct
    public void init() {
        if (!hikSdkManager.initSdk()) {
            log.error("SDK初始化失败，终止出入记录同步");
            return;
        }
        hCNetSDK = hikSdkManager.getHCNetSDK();
    }

    @Override
    public LoginInfo loginDevice(String deviceId) {
        TIndustrialTvBaseInfo device = tIndustrialTvBaseInfoService.getById(deviceId);
        if (device == null) {
            throw new ServiceException("工业电视", "500", "设备不存在");
        }

        if (StringUtils.isBlank(device.getCameraIp())) {
            throw new ServiceException("工业电视", "500", "设备IP未配置");
        }

        RtspUrlParser.RtspInfo rtspInfo = RtspUrlParser.parseHikvisionRtspUrl(device.getStreamAddress());

        int userId = hikSdkManager.loginDevice(rtspInfo.getNvrIp(), (short) 8000, rtspInfo.getUsername(), rtspInfo.getPassword());

        if (userId == -1) {
            int errorCode = hCNetSDK.NET_DVR_GetLastError();
            log.error("设备[{}]登录失败，错误码：{}", device.getCameraIp(), errorCode);
            throw new ServiceException("工业电视", "500", "设备登录失败，错误码：" + errorCode);
        }

        log.info("设备[{}]登录成功，userId={}", device.getCameraIp(), userId);
        deviceLoginMap.put(deviceId, userId);
        return new LoginInfo(rtspInfo.getChannelNo(), userId);
    }

    @Override
    public void logoutDevice(int userId) {
        if (userId >= 0 && hCNetSDK != null) {
            hCNetSDK.NET_DVR_Logout(userId);
            // 从缓存中移除
            deviceLoginMap.values().removeIf(v -> v == userId);
            log.info("设备登出成功，userId={}", userId);
        }
    }

    @Override
    public byte[] snapshot(String deviceId) {
        LoginInfo loginInfo = loginDevice(deviceId);
        // 1. 设置截图参数
        HCNetSDK.NET_DVR_JPEGPARA jpegPara = new HCNetSDK.NET_DVR_JPEGPARA();
        jpegPara.wPicSize = 0;      // 图片分辨率
        jpegPara.wPicQuality = 0;    // 图片质量
        // 2. 分配内存缓冲区（1MB足够存放JPEG图片）
        int bufferSize = 1024 * 1024;
        Pointer jpegBuffer = new Memory(bufferSize);
        IntByReference sizeReturned = new IntByReference(0);
        boolean isSuccess = hCNetSDK.NET_DVR_CaptureJPEGPicture_NEW(loginInfo.getUserId(), loginInfo.getChannelNo() + 32, jpegPara, jpegBuffer, bufferSize, sizeReturned);
        if (!isSuccess) {
            int errorCode = hCNetSDK.NET_DVR_GetLastError();
            log.error("截图失败，错误码：{}", errorCode);
            return null;
        }
        int imageSize = sizeReturned.getValue();
        log.info("截图成功，图片大小：{}字节", imageSize);

        // 4. 从Pointer中读取图片数据
        byte[] imageData = new byte[imageSize];
        jpegBuffer.read(0, imageData, 0, imageSize);

        hikSdkManager.logoutDevice(loginInfo.getUserId());
        return imageData;
    }

    @Override
    public Boolean ptzControl(PtzControlRequest request) {
        LoginInfo loginInfo = loginDevice(request.getDeviceId());
    
        // 处理云台控制命令的逻辑
        String currentCommand = request.getCommand();
            
        // 如果当前命令是 stop，则查询上次的控制命令并恢复
        if ("stop".equalsIgnoreCase(currentCommand)) {
            String lastCommand = devicePtzCommandMap.get(request.getDeviceId());
            if (lastCommand != null && !"stop".equalsIgnoreCase(lastCommand)) {
                log.info("设备 [{}] 收到 stop 命令，恢复上次执行的控制命令：{}", request.getDeviceId(), lastCommand);
                currentCommand = lastCommand;
            } else {
                log.warn("设备 [{}] 没有可恢复的控制命令", request.getDeviceId());
                hikSdkManager.logoutDevice(loginInfo.getUserId());
                return false;
            }
        } else {
            // 记录当前控制命令（非 stop 命令才记录）
            devicePtzCommandMap.put(request.getDeviceId(), currentCommand);
            log.debug("设备 [{}] 记录控制命令：{}", request.getDeviceId(), currentCommand);
        }
    
        // 解析云台控制命令
        int ptzCommand = parsePtzCommand(currentCommand);
        if (ptzCommand == -1) {
            throw new ServiceException("工业电视", "500", "无效的云台控制命令");
        }
    
        int stop = request.getStop() != null ? request.getStop() : 0;
        int speed = request.getSpeed() != null ? request.getSpeed() : 4;
    
        // 使用带速度的云台控制
        boolean result = hCNetSDK.NET_DVR_PTZControlWithSpeed_Other(loginInfo.getUserId(), loginInfo.getChannelNo() + 32, ptzCommand, stop, speed);
        if (!result) {
            int errorCode = hCNetSDK.NET_DVR_GetLastError();
            log.error("云台控制失败，错误码：{}", errorCode);
            throw new ServiceException("工业电视", "500", "云台控制失败，错误码：" + errorCode);
        }
    
        log.info("云台控制成功，设备 ID={}，命令={}，速度={}", request.getDeviceId(), currentCommand, speed);
    
        hikSdkManager.logoutDevice(loginInfo.getUserId());
        return true;
    }

//    @Override
//    public PreviewResponse startPreview(PreviewRequest request) {
//        int userId = loginDevice(request.getDeviceId());
//
//        HCNetSDK.NET_DVR_PREVIEWINFO previewInfo = new HCNetSDK.NET_DVR_PREVIEWINFO();
//        previewInfo.hPlayWnd = null; // 无窗口预览，通过回调获取流
//        previewInfo.lChannel = request.getChannel() != null ? request.getChannel() : 1;
//        previewInfo.dwStreamType = request.getStreamType() != null ? request.getStreamType() : 0;
//        previewInfo.dwLinkMode = request.getProtocolType() != null ? request.getProtocolType() : 0;
//        previewInfo.bBlocked = false;
//
//        int previewHandle = hCNetSDK.NET_DVR_RealPlay_V40(userId, previewInfo, null, null);
//        if (previewHandle == -1) {
//            int errorCode = hCNetSDK.NET_DVR_GetLastError();
//            log.error("开始预览失败，错误码：{}", errorCode);
//            PreviewResponse response = new PreviewResponse();
//            response.setStatus(0);
//            response.setErrorMsg("预览失败，错误码：" + errorCode);
//            return response;
//        }
//
//        // 缓存预览句柄
//        previewHandleMap.put(previewHandle, request.getDeviceId());
//
//        PreviewResponse response = new PreviewResponse();
//        response.setPreviewHandle(previewHandle);
//        response.setDeviceId(request.getDeviceId());
//        response.setStatus(1);
//
//        log.info("开始预览成功，设备ID={}，预览句柄={}", request.getDeviceId(), previewHandle);
//        return response;
//    }

    @Override
    public Boolean stopPreview(Integer previewHandle) {
        if (previewHandle == null || previewHandle == -1) {
            return false;
        }

        boolean result = hCNetSDK.NET_DVR_StopRealPlay(previewHandle);
        if (!result) {
            int errorCode = hCNetSDK.NET_DVR_GetLastError();
            log.error("停止预览失败，错误码：{}", errorCode);
            return false;
        }

        previewHandleMap.remove(previewHandle);
        log.info("停止预览成功，预览句柄={}", previewHandle);
        return true;
    }

//    @Override
//    public PlaybackResponse startPlayback(PlaybackRequest request) {
//        int userId = loginDevice(request.getDeviceId());
//
//        // 构建回放参数
//        HCNetSDK.NET_DVR_VOD_PARA vodPara = new HCNetSDK.NET_DVR_VOD_PARA();
//        vodPara.dwSize = vodPara.size();
//        vodPara.struIDInfo.dwSize = vodPara.struIDInfo.size();
//        vodPara.struIDInfo.lChannel = request.getChannel() != null ? request.getChannel() : 1;
//
//        // 设置开始时间
//        if (request.getStartTime() != null) {
//            Calendar cal = Calendar.getInstance();
//            cal.setTime(request.getStartTime());
//            vodPara.struBeginTime.dwYear = cal.get(Calendar.YEAR);
//            vodPara.struBeginTime.dwMonth = cal.get(Calendar.MONTH) + 1;
//            vodPara.struBeginTime.dwDay = cal.get(Calendar.DAY_OF_MONTH);
//            vodPara.struBeginTime.dwHour = cal.get(Calendar.HOUR_OF_DAY);
//            vodPara.struBeginTime.dwMinute = cal.get(Calendar.MINUTE);
//            vodPara.struBeginTime.dwSecond = cal.get(Calendar.SECOND);
//        }
//
//        // 设置结束时间
//        if (request.getEndTime() != null) {
//            Calendar cal = Calendar.getInstance();
//            cal.setTime(request.getEndTime());
//            vodPara.struEndTime.dwYear = cal.get(Calendar.YEAR);
//            vodPara.struEndTime.dwMonth = cal.get(Calendar.MONTH) + 1;
//            vodPara.struEndTime.dwDay = cal.get(Calendar.DAY_OF_MONTH);
//            vodPara.struEndTime.dwHour = cal.get(Calendar.HOUR_OF_DAY);
//            vodPara.struEndTime.dwMinute = cal.get(Calendar.MINUTE);
//            vodPara.struEndTime.dwSecond = cal.get(Calendar.SECOND);
//        }
//
//        vodPara.hWnd = null; // 无窗口回放
//        vodPara.write();
//
//        int playbackHandle = hCNetSDK.NET_DVR_PlayBackByTime_V40(userId, vodPara);
//        if (playbackHandle == -1) {
//            int errorCode = hCNetSDK.NET_DVR_GetLastError();
//            log.error("开始回放失败，错误码：{}", errorCode);
//            PlaybackResponse response = new PlaybackResponse();
//            response.setStatus(0);
//            response.setErrorMsg("回放失败，错误码：" + errorCode);
//            return response;
//        }
//
//        // 开始播放
//        hCNetSDK.NET_DVR_PlayBackControl(playbackHandle, HCNetSDK.NET_DVR_PLAYSTART, 0, null);
//
//        // 缓存回放句柄
//        playbackHandleMap.put(playbackHandle, request.getDeviceId());
//
//        PlaybackResponse response = new PlaybackResponse();
//        response.setPlaybackHandle(playbackHandle);
//        response.setDeviceId(request.getDeviceId());
//        response.setStatus(1);
//
//        log.info("开始回放成功，设备ID={}，回放句柄={}", request.getDeviceId(), playbackHandle);
//        return response;
//    }

    @Override
    public Boolean playbackControl(Integer playbackHandle, String command, Integer position) {
        if (playbackHandle == null || playbackHandle == -1) {
            return false;
        }

        int controlCode = parsePlaybackCommand(command);
        if (controlCode == -1) {
            throw new ServiceException("工业电视", "500", "无效的回放控制命令");
        }

        int inValue = position != null ? position : 0;
        boolean result = hCNetSDK.NET_DVR_PlayBackControl(playbackHandle, controlCode, inValue, null);
        if (!result) {
            int errorCode = hCNetSDK.NET_DVR_GetLastError();
            log.error("回放控制失败，错误码：{}", errorCode);
            return false;
        }

        log.info("回放控制成功，句柄={}，命令={}", playbackHandle, command);
        return true;
    }

    @Override
    public Boolean stopPlayback(Integer playbackHandle) {
        if (playbackHandle == null || playbackHandle == -1) {
            return false;
        }

        // 先停止播放
        hCNetSDK.NET_DVR_PlayBackControl(playbackHandle, HCNetSDK.NET_DVR_PLAYSTOP, 0, null);

        playbackHandleMap.remove(playbackHandle);
        log.info("停止回放成功，回放句柄={}", playbackHandle);
        return true;
    }

//    @Override
//    public List<RecordFileResponse> queryRecordFiles(PlaybackRequest request) {
//        int userId = loginDevice(request.getDeviceId());
//
//        List<RecordFileResponse> fileList = new ArrayList<>();
//
//        // 构建查找条件
//        HCNetSDK.NET_DVR_FILECOND_V40 fileCond = new HCNetSDK.NET_DVR_FILECOND_V40();
//        fileCond.dwSize = fileCond.size();
//        fileCond.struIDInfo.dwSize = fileCond.struIDInfo.size();
//        fileCond.struIDInfo.lChannel = request.getChannel() != null ? request.getChannel() : 1;
//
//        // 设置开始时间
//        if (request.getStartTime() != null) {
//            Calendar cal = Calendar.getInstance();
//            cal.setTime(request.getStartTime());
//            fileCond.struStartTime.dwYear = cal.get(Calendar.YEAR);
//            fileCond.struStartTime.dwMonth = cal.get(Calendar.MONTH) + 1;
//            fileCond.struStartTime.dwDay = cal.get(Calendar.DAY_OF_MONTH);
//            fileCond.struStartTime.dwHour = cal.get(Calendar.HOUR_OF_DAY);
//            fileCond.struStartTime.dwMinute = cal.get(Calendar.MINUTE);
//            fileCond.struStartTime.dwSecond = cal.get(Calendar.SECOND);
//        }
//
//        // 设置结束时间
//        if (request.getEndTime() != null) {
//            Calendar cal = Calendar.getInstance();
//            cal.setTime(request.getEndTime());
//            fileCond.struStopTime.dwYear = cal.get(Calendar.YEAR);
//            fileCond.struStopTime.dwMonth = cal.get(Calendar.MONTH) + 1;
//            fileCond.struStopTime.dwDay = cal.get(Calendar.DAY_OF_MONTH);
//            fileCond.struStopTime.dwHour = cal.get(Calendar.HOUR_OF_DAY);
//            fileCond.struStopTime.dwMinute = cal.get(Calendar.MINUTE);
//            fileCond.struStopTime.dwSecond = cal.get(Calendar.SECOND);
//        }
//
//        fileCond.byDrawFunction = 0;
//        fileCond.byEnableAutoNext = 1;
//        fileCond.byLockedFileOnly = 0;
//        fileCond.byNeedAccurate = 0;
//        fileCond.byIsCardRecord = 0;
//        fileCond.dwUseRetGroup = 0;
//        fileCond.nIndextype = 0;
//        fileCond.write();
//
//        int findHandle = hCNetSDK.NET_DVR_FindFile_V40(userId, fileCond);
//        if (findHandle == -1) {
//            int errorCode = hCNetSDK.NET_DVR_GetLastError();
//            log.error("查找录像文件失败，错误码：{}", errorCode);
//            return fileList;
//        }
//
//        HCNetSDK.NET_DVR_FINDDATA_V40 findData = new HCNetSDK.NET_DVR_FINDDATA_V40();
//        IntByReference dwCount = new IntByReference(0);
//
//        while (true) {
//            int result = hCNetSDK.NET_DVR_FindNextFile_V40(findHandle, findData);
//            if (result == HCNetSDK.NET_DVR_FILE_SUCCESS) {
//                RecordFileResponse fileResponse = new RecordFileResponse();
//                fileResponse.setFileName(new String(findData.sFileName, StandardCharsets.UTF_8).trim());
//                fileResponse.setChannel(findData.struIDInfo.lChannel);
//                fileResponse.setFileSize((long) findData.dwFileSize);
//
//                // 转换时间
//                Calendar startCal = Calendar.getInstance();
//                startCal.set(findData.struStartTime.dwYear, findData.struStartTime.dwMonth - 1,
//                        findData.struStartTime.dwDay, findData.struStartTime.dwHour,
//                        findData.struStartTime.dwMinute, findData.struStartTime.dwSecond);
//                fileResponse.setStartTime(startCal.getTime());
//
//                Calendar endCal = Calendar.getInstance();
//                endCal.set(findData.struStopTime.dwYear, findData.struStopTime.dwMonth - 1,
//                        findData.struStopTime.dwDay, findData.struStopTime.dwHour,
//                        findData.struStopTime.dwMinute, findData.struStopTime.dwSecond);
//                fileResponse.setEndTime(endCal.getTime());
//
//                fileList.add(fileResponse);
//            } else if (result == HCNetSDK.NET_DVR_FILE_NOFIND || result == HCNetSDK.NET_DVR_NOMOREFILE) {
//                break;
//            } else {
//                log.error("查找录像文件异常，错误码：{}", hCNetSDK.NET_DVR_GetLastError());
//                break;
//            }
//        }
//
//        hCNetSDK.NET_DVR_FindClose_V30(findHandle);
//        log.info("查询录像文件完成，共{}个文件", fileList.size());
//        return fileList;
//    }

    @Override
    public Boolean setPreset(PresetRequest request) {
        LoginInfo loginInfo = loginDevice(request.getDeviceId());

        boolean result = hCNetSDK.NET_DVR_PTZPreset_Other(loginInfo.getUserId(), loginInfo.getChannelNo() + 32, HCNetSDK.SET_PRESET, request.getPresetIndex());
        if (!result) {
            int errorCode = hCNetSDK.NET_DVR_GetLastError();
            log.error("设置预置点失败，错误码：{}", errorCode);
            throw new ServiceException("工业电视", "500", "设置预置点失败，错误码：" + errorCode);
        }

        log.info("设置预置点成功，设备ID={}，预置点编号={}", request.getDeviceId(), request.getPresetIndex());

        hikSdkManager.logoutDevice(loginInfo.getUserId());
        return true;
    }

    @Override
    public Boolean gotoPreset(PresetRequest request) {
        LoginInfo loginInfo = loginDevice(request.getDeviceId());

        boolean result = hCNetSDK.NET_DVR_PTZPreset_Other(loginInfo.getUserId(), loginInfo.getChannelNo() + 32, HCNetSDK.GOTO_PRESET, request.getPresetIndex());
        if (!result) {
            int errorCode = hCNetSDK.NET_DVR_GetLastError();
            log.error("转到预置点失败，错误码：{}", errorCode);
            throw new ServiceException("工业电视", "500", "转到预置点失败，错误码：" + errorCode);
        }

        log.info("转到预置点成功，设备ID={}，预置点编号={}", request.getDeviceId(), request.getPresetIndex());
        hikSdkManager.logoutDevice(loginInfo.getUserId());
        return true;
    }

    @Override
    public Boolean removePreset(PresetRequest request) {
        LoginInfo loginInfo = loginDevice(request.getDeviceId());

        boolean result = hCNetSDK.NET_DVR_PTZPreset_Other(loginInfo.getUserId(), loginInfo.getChannelNo() + 32, HCNetSDK.CLE_PRESET, request.getPresetIndex());
        if (!result) {
            int errorCode = hCNetSDK.NET_DVR_GetLastError();
            log.error("删除预置点失败，错误码：{}", errorCode);
            throw new ServiceException("工业电视", "500", "删除预置点失败，错误码：" + errorCode);
        }

        log.info("删除预置点成功，设备ID={}，预置点编号={}", request.getDeviceId(), request.getPresetIndex());
        hikSdkManager.logoutDevice(loginInfo.getUserId());
        return true;
    }

    @Override
    public List<PresetResponse> queryPresets(String deviceId) {
        // 预置点查询需要通过配置接口获取，这里返回空列表
        // 实际使用时需要通过NET_DVR_GET_PRESET_NAME等接口获取
        return new ArrayList<>();
    }

    @Override
    public Boolean addCruisePoint(CruiseRequest request) {
        LoginInfo loginInfo = loginDevice(request.getDeviceId());

        // 添加巡航点命令
        boolean result = hCNetSDK.NET_DVR_PTZCruise_Other(loginInfo.getUserId(), loginInfo.getChannelNo() + 32,
                HCNetSDK.FILL_PRE_SEQ,
                request.getCruiseRoute().byteValue(),
                request.getPresetIndex().byteValue(),
                request.getSpeed().shortValue());

        if (!result) {
            int errorCode = hCNetSDK.NET_DVR_GetLastError();
            log.error("添加巡航点失败，错误码：{}", errorCode);
            throw new ServiceException("工业电视", "500", "添加巡航点失败，错误码：" + errorCode);
        }

        log.info("添加巡航点成功，设备ID={}，巡航路径={}，预置点={}", request.getDeviceId(),
                request.getCruiseRoute(), request.getPresetIndex());
        return true;
    }

    @Override
    public Boolean removeCruisePoint(CruiseRequest request) {
        LoginInfo loginInfo = loginDevice(request.getDeviceId());

        boolean result = hCNetSDK.NET_DVR_PTZCruise_Other(loginInfo.getUserId(), loginInfo.getChannelNo() + 32,
                HCNetSDK.CLE_PRE_SEQ,
                request.getCruiseRoute().byteValue(),
                request.getCruisePoint().byteValue(),
                (short) 0);

        if (!result) {
            int errorCode = hCNetSDK.NET_DVR_GetLastError();
            log.error("删除巡航点失败，错误码：{}", errorCode);
            throw new ServiceException("工业电视", "500", "删除巡航点失败，错误码：" + errorCode);
        }

        log.info("删除巡航点成功，设备ID={}，巡航路径={}，巡航点={}", request.getDeviceId(),
                request.getCruiseRoute(), request.getCruisePoint());
        return true;
    }

    @Override
    public Boolean startCruise(CruiseRequest request) {
        LoginInfo loginInfo = loginDevice(request.getDeviceId());

        boolean result = hCNetSDK.NET_DVR_PTZCruise_Other(loginInfo.getUserId(), loginInfo.getChannelNo() + 32,
                HCNetSDK.RUN_CRUISE,
                request.getCruiseRoute().byteValue(),
                (byte) 0,
                (short) 0);

        if (!result) {
            int errorCode = hCNetSDK.NET_DVR_GetLastError();
            log.error("开始巡航失败，错误码：{}", errorCode);
            throw new ServiceException("工业电视", "500", "开始巡航失败，错误码：" + errorCode);
        }

        log.info("开始巡航成功，设备ID={}，巡航路径={}", request.getDeviceId(), request.getCruiseRoute());
        return true;
    }

    @Override
    public Boolean stopCruise(CruiseRequest request) {
        LoginInfo loginInfo = loginDevice(request.getDeviceId());

        boolean result = hCNetSDK.NET_DVR_PTZCruise_Other(loginInfo.getUserId(), loginInfo.getChannelNo() + 32,
                HCNetSDK.STOP_SEQ,
                request.getCruiseRoute().byteValue(),
                (byte) 0,
                (short) 0);

        if (!result) {
            int errorCode = hCNetSDK.NET_DVR_GetLastError();
            log.error("停止巡航失败，错误码：{}", errorCode);
            throw new ServiceException("工业电视", "500", "停止巡航失败，错误码：" + errorCode);
        }

        log.info("停止巡航成功，设备ID={}，巡航路径={}", request.getDeviceId(), request.getCruiseRoute());
        return true;
    }

    @Override
    public CruiseResponse queryCruise(CruiseRequest request) {
        LoginInfo loginInfo = loginDevice(request.getDeviceId());

        HCNetSDK.NET_DVR_CRUISE_RET cruiseRet = new HCNetSDK.NET_DVR_CRUISE_RET();
        boolean result = hCNetSDK.NET_DVR_GetPTZCruise(loginInfo.getUserId(), loginInfo.getChannelNo() + 32,
                request.getCruiseRoute(), cruiseRet);

        if (!result) {
            int errorCode = hCNetSDK.NET_DVR_GetLastError();
            log.error("查询巡航参数失败，错误码：{}", errorCode);
            throw new ServiceException("工业电视", "500", "查询巡航参数失败，错误码：" + errorCode);
        }

        CruiseResponse response = new CruiseResponse();
        response.setCruiseRoute(request.getCruiseRoute());
        response.setIsRunning(false);

        List<CruiseResponse.CruisePoint> cruisePoints = new ArrayList<>();
        for (int i = 0; i < cruiseRet.struCruisePoint.length; i++) {
            HCNetSDK.NET_DVR_CRUISE_POINT point = cruiseRet.struCruisePoint[i];
            if (point.PresetNum > 0) {
                CruiseResponse.CruisePoint cp = new CruiseResponse.CruisePoint();
                cp.setCruisePoint(i + 1);
                cp.setPresetIndex((int) point.PresetNum);
                cp.setSpeed((int) point.Speed);
                cp.setDwellTime((int) point.Dwell);
                cruisePoints.add(cp);
            }
        }
        response.setCruisePoints(cruisePoints);

        log.info("查询巡航参数成功，设备ID={}，巡航路径={}，巡航点数量={}", request.getDeviceId(),
                request.getCruiseRoute(), cruisePoints.size());
        return response;
    }

    /**
     * 解析云台控制命令
     */
    private int parsePtzCommand(String command) {
        if (command == null) {
            return -1;
        }
        switch (command.toLowerCase()) {
            case "up":
                return HCNetSDK.TILT_UP;
            case "down":
                return HCNetSDK.TILT_DOWN;
            case "left":
                return HCNetSDK.PAN_LEFT;
            case "right":
                return HCNetSDK.PAN_RIGHT;
            case "upleft":
                return HCNetSDK.UP_LEFT;
            case "upright":
                return HCNetSDK.UP_RIGHT;
            case "downleft":
                return HCNetSDK.DOWN_LEFT;
            case "downright":
                return HCNetSDK.DOWN_RIGHT;
            case "zoomin":
                return HCNetSDK.ZOOM_IN;
            case "zoomout":
                return HCNetSDK.ZOOM_OUT;
            case "focusnear":
                return HCNetSDK.FOCUS_NEAR;
            case "focusfar":
                return HCNetSDK.FOCUS_FAR;
            case "irisopen":
                return HCNetSDK.IRIS_OPEN;
            case "irisclose":
                return HCNetSDK.IRIS_CLOSE;
            case "stop":
                return 0;
            default:
                return -1;
        }
    }

    /**
     * 解析回放控制命令
     */
    private int parsePlaybackCommand(String command) {
        if (command == null) {
            return -1;
        }
        switch (command.toLowerCase()) {
            case "play":
                return HCNetSDK.NET_DVR_PLAYSTART;
            case "pause":
                return HCNetSDK.NET_DVR_PLAYPAUSE;
            case "resume":
                return HCNetSDK.NET_DVR_PLAYRESTART;
            case "stop":
                return HCNetSDK.NET_DVR_PLAYSTOP;
            case "slow":
                return HCNetSDK.NET_DVR_PLAYSLOW;
            case "fast":
                return HCNetSDK.NET_DVR_PLAYFAST;
            case "normal":
                return HCNetSDK.NET_DVR_PLAYNORMAL;
            case "frame":
                return HCNetSDK.NET_DVR_PLAYFRAME;
            case "position":
                return HCNetSDK.NET_DVR_PLAYSETPOS;
            default:
                return -1;
        }
    }

    /**
     * 获取设备通道号
     */
    private int getChannel(String deviceId) {
        // 默认返回1，实际可从设备配置中获取
        return 1;
    }

}
