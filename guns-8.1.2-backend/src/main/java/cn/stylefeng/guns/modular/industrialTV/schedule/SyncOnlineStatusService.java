package cn.stylefeng.guns.modular.industrialTV.schedule;

import cn.stylefeng.guns.core.utils.StringUtils;
import cn.stylefeng.guns.database.entity.TIndustrialTvBaseInfo;
import cn.stylefeng.guns.database.entity.TStationBaseInfo;
import cn.stylefeng.guns.database.service.TIndustrialTvBaseInfoService;
import cn.stylefeng.guns.database.service.TStationBaseInfoService;
import cn.stylefeng.guns.liveGBS.LiveGBSService;
import cn.stylefeng.guns.liveGBS.dto.BaseRequestDTO;
import cn.stylefeng.guns.liveGBS.dto.ChannelinfoResponseDTO;
import cn.stylefeng.guns.modular.industrialTV.remoteClient.client.SmartSecurityClient;
import cn.stylefeng.guns.modular.industrialTV.remoteClient.dto.CameraLineStatus;
import cn.stylefeng.guns.modular.industrialTV.remoteClient.dto.SmartSecurityResponse;
import cn.stylefeng.guns.modular.industrialTV.remoteClient.factory.SmartSecurityClientFactory;
import cn.stylefeng.guns.modular.industrialTV.remoteClient.service.SmartSecurityClientService;
import cn.stylefeng.guns.modular.nodeSystem.dto.DeviceStatusDTO;
import cn.stylefeng.guns.modular.nodeSystem.service.NodeSystemService;
import cn.stylefeng.guns.zlmediakit.ZlMediaKitService;
import cn.stylefeng.guns.zlmediakit.dto.ZlMediaCacheDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 工业电视在线状态同步服务类
 * 定时同步摄像头的在线状态
 */
@Service
@Slf4j
public class SyncOnlineStatusService {

    @Autowired
    private TIndustrialTvBaseInfoService tIndustrialTvBaseInfoService;

    @Autowired
    private SmartSecurityClientFactory smartSecurityClientFactory;

    @Autowired
    private SmartSecurityClientService smartSecurityClientService;

    @Autowired
    private NodeSystemService nodeSystemService;

    @Autowired
    private TStationBaseInfoService tStationBaseInfoService;

    @Autowired
    private ZlMediaKitService zlMediaKitService;

    @Autowired
    private LiveGBSService liveGBSService;

    private final ReentrantLock lock = new ReentrantLock();

    /**
     * 定时同步摄像头在线状态，每次执行完后等待30秒再执行下次
     */
    @Scheduled(initialDelay = 5000, fixedDelay = 30000) // 初始延迟5秒，执行完后等待30秒再执行
    public void syncCameraOnlineStatus() {
        if (!lock.tryLock()) {
            log.info("摄像头在线状态同步已在执行中，跳过本次执行");
            return;
        }

        try {
            log.info("开始同步摄像头在线状态...");

            // 查询所有摄像头设备数据
            List<TIndustrialTvBaseInfo> cameras = tIndustrialTvBaseInfoService.list();

            if (cameras.isEmpty()) {
                log.info("未找到任何摄像头设备，跳过同步");
                return;
            }

            // 遍历每个摄像头，获取在线状态并更新数据库
            for (TIndustrialTvBaseInfo camera : cameras) {
                try {
                    // 获取摄像头的IP、端口、用户名和密码
                    String cameraIp = camera.getCameraIp();
                    Integer cameraPort = camera.getCameraPort();
                    String cameraUsername = camera.getCameraUsername();
                    String cameraPassword = camera.getCameraPassword();

                    // 检查必要的连接信息是否完整
                    if (cameraIp == null || cameraIp.isEmpty() ||
                        cameraPort == null ||
                        cameraUsername == null || cameraUsername.isEmpty() ||
                        cameraPassword == null || cameraPassword.isEmpty()) {
                        log.warn("摄像头 {} (ID: {}) 连接信息不完整，跳过状态同步", camera.getDeviceName(), camera.getDeviceId());
                        continue;
                    }

                    // 获取摄像头在线状态
                    Boolean mediaOnlie = zlMediaKitService.isMediaOnlie(camera.getStreamAddress());
                    if (!mediaOnlie) {
                        try{
                            ZlMediaCacheDTO playUrl = zlMediaKitService.getPlayUrl(camera.getStreamAddress());
                            mediaOnlie = !StringUtils.isEmpty(playUrl);
                        }catch (Exception e){
                            BaseRequestDTO requestDto = new BaseRequestDTO();
                            requestDto.setCode(camera.getStreamChannel());
                            requestDto.setSerial(camera.getGbCode());
                            ChannelinfoResponseDTO channelinfo = liveGBSService.channelinfo(requestDto);
                            mediaOnlie = channelinfo != null && !StringUtils.isEmpty(channelinfo.getStatus()) && "ON".equals(channelinfo.getStatus());
                        }
                    }

                    String lineStatus;
                    if (mediaOnlie) {
                        lineStatus = "normal";

                        // 根据在线状态更新数据库
                        // lineStatus: normal-在线, offline-离线
                        // onlineStatus: 0-离线, 1-在线, 2-占用

                        if (!camera.getOnlineStatus().equals("1")) {
                            sendDeviceStatus(camera, "1");
                        }
                        camera.setOnlineStatus("1"); // 在线
                        // 更新数据库记录
                        tIndustrialTvBaseInfoService.updateById(camera);

                        log.debug("摄像头 {} (ID: {}) 状态更新成功: {}", camera.getDeviceName(), camera.getDeviceId(), lineStatus);
                    } else {
                        log.warn("无法获取摄像头 {} (ID: {}) 的状态信息", camera.getDeviceName(), camera.getDeviceId());
                        if(!camera.getOnlineStatus().equals("0")){
                            sendDeviceStatus(camera, "0");
                        }
                        // 如果无法获取状态，默认设置为离线
                        camera.setOnlineStatus("0");
                        tIndustrialTvBaseInfoService.updateById(camera);
                    }
                } catch (Exception e) {
                    log.error("更新摄像头 {} (ID: {}) 状态时出错: {}", camera.getDeviceName(), camera.getDeviceId(), e.getMessage(), e);
                    if(!camera.getOnlineStatus().equals("0")){
                        sendDeviceStatus(camera, "0");
                    }
                    // 出错时设置为离线状态
                    camera.setOnlineStatus("0");
                    tIndustrialTvBaseInfoService.updateById(camera);
                }
            }

            log.info("摄像头在线状态同步完成，共处理 {} 个摄像头", cameras.size());

        } catch (Exception e) {
            log.error("同步摄像头在线状态时发生异常", e);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取摄像头状态
     * 优先使用流媒体国标通道ID查询，如果不存在则使用设备编码查询
     */
    private List<CameraLineStatus> getCameraStatus(SmartSecurityClient client, TIndustrialTvBaseInfo camera) {
        try {
            List<String> streamChannelSerials = new ArrayList<>();

            // 优先使用流媒体国标通道ID
            if (camera.getStreamChannel() != null && !StringUtils.isEmpty(camera.getStreamChannel())) {
                streamChannelSerials.add(camera.getStreamChannel());
            }

            // 如果没有流媒体通道，尝试使用设备编码
            if (streamChannelSerials.isEmpty() && camera.getDeviceCode() != null && !camera.getDeviceCode().isEmpty()) {
                streamChannelSerials.add(camera.getDeviceCode());
            }

            // 如果都没有，返回空列表
            if (streamChannelSerials.isEmpty()) {
                log.warn("摄像头 {} (ID: {}) 没有可用的流媒体通道ID或设备编码",
                        camera.getDeviceName(), camera.getDeviceId());
                return Collections.emptyList();
            }

            // 调用接口获取摄像头状态
            SmartSecurityResponse<List<CameraLineStatus>> response = client.getCamerasLineStatus(streamChannelSerials);

            if (response != null && response.isSuccess() && response.getData() != null) {
                return response.getData();
            }

            return Collections.emptyList();
        } catch (Exception e) {
            log.error("获取摄像头 {} (ID: {}) 状态时发生异常",
                    camera.getDeviceName(), camera.getDeviceId(), e);
            return Collections.emptyList();
        }
    }

    /**
     * 手动触发同步摄像头状态
     */
    public void manualSyncCameraStatus() {
        if (!lock.tryLock()) {
            log.info("摄像头在线状态同步已在执行中，跳过本次执行");
            return;
        }

        try {
            syncCameraOnlineStatus();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 同步指定摄像头的在线状态
     * @param deviceId 摄像头设备ID
     */
    public void syncSingleCameraStatus(String deviceId) {
        if (!lock.tryLock()) {
            log.info("摄像头在线状态同步已在执行中，跳过本次执行");
            return;
        }

        try {
            TIndustrialTvBaseInfo camera = tIndustrialTvBaseInfoService.getById(deviceId);
            if (camera == null) {
                log.warn("未找到设备ID为 {} 的摄像头", deviceId);
                return;
            }

            log.info("开始同步摄像头 {} (ID: {}) 的在线状态...", camera.getDeviceName(), camera.getDeviceId());

            // 获取摄像头的IP、端口、用户名和密码
            String cameraIp = camera.getCameraIp();
            Integer cameraPort = camera.getCameraPort();
            String cameraUsername = camera.getCameraUsername();
            String cameraPassword = camera.getCameraPassword();

            // 检查必要的连接信息是否完整
            if (cameraIp == null || cameraIp.isEmpty() ||
                cameraPort == null ||
                cameraUsername == null || cameraUsername.isEmpty() ||
                cameraPassword == null || cameraPassword.isEmpty()) {
                log.warn("摄像头 {} (ID: {}) 连接信息不完整，跳过状态同步",
                        camera.getDeviceName(), camera.getDeviceId());
                return;
            }

            // 创建视频智能分析系统客户端
            SmartSecurityClient smartSecurityClient = smartSecurityClientFactory.createClient(
                    cameraIp,
                    cameraPort,
                    cameraUsername,
                    cameraPassword
            );

            // 获取摄像头在线状态
            List<CameraLineStatus> cameraStatusList = getCameraStatus(smartSecurityClient, camera);

            if (cameraStatusList != null && !cameraStatusList.isEmpty()) {
                CameraLineStatus cameraStatus = cameraStatusList.get(0);

                // 根据在线状态更新数据库
                String lineStatus = cameraStatus.getLineStatus();

                if ("normal".equalsIgnoreCase(lineStatus)) {
                    camera.setOnlineStatus("1"); // 在线
                } else if ("offline".equalsIgnoreCase(lineStatus)) {
                    camera.setOnlineStatus("0"); // 离线
                } else {
                    camera.setOnlineStatus("0"); // 未知状态，默认设置为离线
                }

                // 更新数据库记录
                tIndustrialTvBaseInfoService.updateById(camera);

                log.info("摄像头 {} (ID: {}) 状态更新成功: {}",
                        camera.getDeviceName(), camera.getDeviceId(), lineStatus);
            } else {
                log.warn("无法获取摄像头 {} (ID: {}) 的状态信息",
                        camera.getDeviceName(), camera.getDeviceId());

                // 如果无法获取状态，默认设置为离线
                camera.setOnlineStatus("0");
                tIndustrialTvBaseInfoService.updateById(camera);
            }

        } catch (Exception e) {
            log.error("同步摄像头 {} 状态时出错: {}", deviceId, e.getMessage(), e);
        } finally {
            lock.unlock();
        }
    }

    private void sendDeviceStatus(TIndustrialTvBaseInfo camera, String status) {
        try{
            DeviceStatusDTO deviceStatusDTO = new DeviceStatusDTO();
            deviceStatusDTO.setDeviceCode(camera.getDeviceCode());
            deviceStatusDTO.setDeviceName(camera.getDeviceName());
            deviceStatusDTO.setType(Integer.valueOf(status));
            deviceStatusDTO.setNodeCode(nodeSystemService.getNodeCode());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            deviceStatusDTO.setTriggerTime(sdf.format(new Date()));
            TStationBaseInfo station = tStationBaseInfoService.getById(camera.getBelongStationId());
            deviceStatusDTO.setStationCode(station.getStationCode());
            deviceStatusDTO.setWorkAreaCode(tStationBaseInfoService.getBelongOperationAreaCode(station));
            deviceStatusDTO.setPipelineCode(tStationBaseInfoService.getBelongPipelineCode(station));
            if(!nodeSystemService.sendDeviceStatus(deviceStatusDTO)){
                log.info("工业电视 {} 向省级平台同步状态失败", camera.getDeviceName());
            }
        }catch (Exception e){
            log.error("工业电视 {} 向省级平台同步状态出错: {}", camera.getDeviceName(), e.getMessage(), e);
        }
    }

}