package cn.stylefeng.guns.modular.broadcast.service;

import cn.stylefeng.guns.database.entity.TEmergencyBroadcastHostBaseInfo;
import cn.stylefeng.guns.database.entity.TStationBaseInfo;
import cn.stylefeng.guns.database.service.TEmergencyBroadcastHostBaseInfoService;
import cn.stylefeng.guns.database.service.TStationBaseInfoService;
import cn.stylefeng.guns.modular.broadcast.remoteClient.client.BroadcastClient;
import cn.stylefeng.guns.modular.broadcast.remoteClient.dto.TerminalInfo;
import cn.stylefeng.guns.modular.broadcast.remoteClient.factory.BroadcastClientFactory;
import cn.stylefeng.guns.modular.nodeSystem.dto.DeviceStatusDTO;
import cn.stylefeng.guns.modular.nodeSystem.service.NodeSystemService;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 异步在线状态服务类
 * 定时同步设备的在线状态
 */
@Service
@Slf4j
public class AsyncOnlineStatusService {

    @Autowired
    private TEmergencyBroadcastHostBaseInfoService tEmergencyBroadcastHostBaseInfoService;
    
    @Autowired
    private BroadcastClientFactory broadcastClientFactory;

    @Autowired
    private NodeSystemService nodeSystemService;

    @Autowired
    private TStationBaseInfoService tStationBaseInfoService;
    
    private final ReentrantLock lock = new ReentrantLock();
    
    /**
     * 定时同步设备在线状态，每次执行完后等待30秒再执行下次
     */
    @Scheduled(initialDelay = 5000, fixedDelay = 30000) // 初始延迟5秒，执行完后等待30秒再执行
    public void syncDeviceOnlineStatus() {
        if (!lock.tryLock()) {
            log.info("设备在线状态同步已在执行中，跳过本次执行");
            return;
        }
        
        try {
            log.info("开始同步设备在线状态...");
            
            // 查询所有设备数据
            List<TEmergencyBroadcastHostBaseInfo> devices = tEmergencyBroadcastHostBaseInfoService.list();
            
            if (devices.isEmpty()) {
                log.info("未找到任何设备，跳过同步");
                return;
            }
            
            // 遍历每个设备，获取在线状态并更新数据库
            for (TEmergencyBroadcastHostBaseInfo device : devices) {
                try {
                    // 创建广播客户端
                    BroadcastClient broadcastClient = broadcastClientFactory.createClient(
                            device.getIpAddress(), 
                            device.getPort(), 
                            device.getUsername(), 
                            device.getPassword()
                    );
                    
                    // 获取终端信息
                    List<TerminalInfo> terminalList = broadcastClient.getTerminalsById(
                            Collections.singletonList(Integer.valueOf(device.getDeviceCode()))
                    );
                    
                    if (terminalList != null && !terminalList.isEmpty()) {
                        TerminalInfo terminal = terminalList.get(0);
                        
                        // 根据终端状态更新数据库
                        // Status: 0-离线, 1-在线, 2-占用
                        // StatusDsp: 终端在线与否
                        Integer status = terminal.getStatus();
                        String statusDsp = terminal.getStatusDsp();

                        if(StringUtils.isEmpty(device.getOnlineStatus()) || !device.getOnlineStatus().equals(status.toString())){
                            sendDeviceStatus(device, status.toString());
                        }
                        // 更新设备状态信息到数据库
                        device.setOnlineStatus(status.toString());
                        
                        // 更新数据库记录
                        tEmergencyBroadcastHostBaseInfoService.updateById(device);
                        
                        log.debug("设备 {} (ID: {}) 状态更新成功: {} ({})", 
                                device.getDeviceName(), device.getDeviceId(), statusDsp, status);
                    } else {
                        log.warn("无法获取设备 {} (ID: {}) 的状态信息", 
                                device.getDeviceName(), device.getDeviceId());

                        if(StringUtils.isEmpty(device.getOnlineStatus()) || !device.getOnlineStatus().equals("0")){
                            sendDeviceStatus(device, "0");
                        }
                        // 如果无法获取状态，默认设置为离线
                        device.setOnlineStatus("0");
                        tEmergencyBroadcastHostBaseInfoService.updateById(device);
                    }
                } catch (Exception e) {
                    log.error("更新设备 {} (ID: {}) 状态时出错: {}", 
                            device.getDeviceName(), device.getDeviceId(), e.getMessage(), e);
                    if(StringUtils.isEmpty(device.getOnlineStatus()) || !device.getOnlineStatus().equals("0")){
                        sendDeviceStatus(device, "0");
                    }
                    // 出错时设置为离线状态
                    device.setOnlineStatus("0");
                    tEmergencyBroadcastHostBaseInfoService.updateById(device);
                }
            }
            
            log.info("设备在线状态同步完成，共处理 {} 台设备", devices.size());
            
        } catch (Exception e) {
            log.error("同步设备在线状态时发生异常", e);
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * 手动触发同步设备状态
     */
    public void manualSyncDeviceStatus() {
        if (!lock.tryLock()) {
            log.info("设备在线状态同步已在执行中，跳过本次执行");
            return;
        }
        
        try {
            syncDeviceOnlineStatus();
        } finally {
            lock.unlock();
        }
    }

    private void sendDeviceStatus(TEmergencyBroadcastHostBaseInfo device, String status) {
        try{
            DeviceStatusDTO deviceStatusDTO = new DeviceStatusDTO();
            deviceStatusDTO.setDeviceCode(device.getDeviceCode());
            deviceStatusDTO.setDeviceName(device.getDeviceName());
            deviceStatusDTO.setType(Integer.valueOf(status));
            deviceStatusDTO.setNodeCode(nodeSystemService.getNodeCode());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            deviceStatusDTO.setTriggerTime(sdf.format(new Date()));
            deviceStatusDTO.setWorkAreaCode(device.getBelongStationAreaId());
            TStationBaseInfo station = tStationBaseInfoService.getById(device.getBelongStationId());
            deviceStatusDTO.setStationCode(station.getStationCode());
            deviceStatusDTO.setPipelineCode(tStationBaseInfoService.getBelongPipelineCode(station));
            deviceStatusDTO.setWorkAreaCode(tStationBaseInfoService.getBelongOperationAreaCode(station));
            if(!nodeSystemService.sendDeviceStatus(deviceStatusDTO)){
                log.info("应急广播 {} 向省级平台同步状态失败", device.getDeviceName());
            }
        }catch (Exception e){
            log.error("应急广播 {} 向省级平台同步状态出错: {}", device.getDeviceName(), e.getMessage(), e);
        }
    }
}