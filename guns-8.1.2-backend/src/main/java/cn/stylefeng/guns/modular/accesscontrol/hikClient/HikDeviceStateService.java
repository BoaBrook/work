package cn.stylefeng.guns.modular.accesscontrol.hikClient;


import cn.hutool.core.date.DateUtil;
import cn.stylefeng.guns.database.entity.*;
import cn.stylefeng.guns.database.service.TAccessControlBaseInfoService;
import cn.stylefeng.guns.database.service.TStationBaseInfoService;
import cn.stylefeng.guns.modular.hikvision.NetSDKDemo.HCNetSDK;
import cn.stylefeng.guns.modular.nodeSystem.constants.dict.DeviceTypeEnum;
import cn.stylefeng.guns.modular.nodeSystem.dto.AlarmRawDTO;
import cn.stylefeng.guns.modular.nodeSystem.dto.DeviceStatusDTO;
import cn.stylefeng.guns.modular.nodeSystem.service.NodeSystemService;
import com.sun.jna.ptr.IntByReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HikDeviceStateService {

    private final HikSdkManager hikSdkManager;
    private final TAccessControlBaseInfoService deviceService;
    @Autowired
    private NodeSystemService nodeSystemService;
    @Autowired
    private TStationBaseInfoService stationService;

    /**
     * 检查设备在线状态
     */
    public void checkDeviceState() {
        if (!hikSdkManager.initSdk()) {
            log.error("SDK初始化失败，终止设备状态检查");
            return;
        }

        List<TAccessControlBaseInfo> deviceList = deviceService.list();
        HCNetSDK hcNetSDK = hikSdkManager.getHCNetSDK();

        for (TAccessControlBaseInfo device : deviceList) {
            int userID = hikSdkManager.loginDevice(device.getIpAddress(), device.getPort().shortValue(), device.getAccessAccount(), device.getAccessPassword());
            if (userID == -1) {
                if(device.getState() == 1){
                    onDeviceState(device);
                }
                device.setState(0); // 离线
                deviceService.updateById(device);
                continue;
            }

            try {
                // 查询设备门禁工作状态
                HCNetSDK.NET_DVR_ACS_WORK_STATUS_V50 workStatus = new HCNetSDK.NET_DVR_ACS_WORK_STATUS_V50();
                workStatus.dwSize = workStatus.size();
                workStatus.write();

                IntByReference intRef = new IntByReference(0);
                boolean isOnline = hcNetSDK.NET_DVR_GetDVRConfig(userID, HCNetSDK.NET_DVR_GET_ACS_WORK_STATUS_V50,
                        0xFFFFFFFF, workStatus.getPointer(), workStatus.size(), intRef);
                int state = isOnline ? 1 : 0;
                if(device.getState() != state){
                    onDeviceState(device);
                }
                device.setState(state); // 0-离线，1-在线
                deviceService.updateById(device);
                log.info("设备[{}]状态：{}", device.getIpAddress(), isOnline ? "在线" : "离线");
            } finally {
                hikSdkManager.logoutDevice(userID);
            }
        }
    }

    public void onDeviceState(TAccessControlBaseInfo device) {
        try {
            TStationBaseInfo station = stationService.getById(device.getBelongStationId());
            DeviceStatusDTO dto = new DeviceStatusDTO();
            dto.setNodeCode(nodeSystemService.getNodeCode());
            dto.setPipelineCode(station.getBelongPipeline());
            dto.setWorkAreaCode(station.getBelongOperationArea());
            dto.setStationCode(station.getStationCode());
            dto.setDeviceName(String.format("%s-%s-%s", station.getBelongPipeline(), station.getStationName(), device.getDeviceName()));
            dto.setDeviceCode(device.getDeviceCode());
            dto.setType(device.getState() == 1 ? 1 : 2);
            dto.setTriggerTime(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            nodeSystemService.sendDeviceStatus(dto);
        } catch (NumberFormatException e) {
            log.error("Error sending access state", e);
        }
    }

    public void onAlarm(TAccessControlBaseInfo device) {
        // try {
//             TStationBaseInfo station = stationService.getById(device.getBelongStationId());
//             AlarmRawDTO dto = new AlarmRawDTO();
//             String nodeCode = nodeSystemService.getNodeCode();
//             dto.setAlarmId(nodeCode + "_" + alarmResultRecords.getAlarmId());
//
//             // 节点编码
//             dto.setNodeCode(nodeCode);
//
//             // 设备编码（使用传感器设备编码）
//             TIndustrialTvBaseInfo device = tIndustrialTvBaseInfoService.getById(alarmResultRecords.getAlarmDeviceId());
//             dto.setDeviceCode(device.getDeviceCode());
//
//             // 设备名称（使用传感器设备名称）
//             dto.setDeviceName(device.getDeviceName());
//
//             // 设备点位（位置）
// //            alarmRaw.setDeviceLocation(device.getAlarmLocation());
//
//             // 设备类型：火气系统
//             dto.setDeviceType(DeviceTypeEnum.INDUSTRIAL_TELEVISION.getCode());
//
//             // 告警级别：1-I级
//             dto.setAlarmLevel(Integer.parseInt(alarmResultRecords.getAlarmLevel()));
//
//             // 告警类型
//             dto.setAlarmType(alarmResultRecords.getAlarmType());
//
//             // 告警内容
//             dto.setAlarmContent(alarmResultRecords.getAlarmContent());
//
//             // 告警时间（格式：yyyy-MM-dd HH:mm:ss）
//             SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//             dto.setAlarmTime(sdf.format(alarmResultRecords.getAlarmTime()));
//             // 发送报警
//             boolean success = nodeSystemService.sendAlarmRaw(dto);
//             if (success) {
//                 log.info("上报报警成功，alarmId: {}, pipelineCode: {}, workAreaCode: {}",
//                         dto.getAlarmId(), pipelineCode, workAreaCode);
//             } else {
//                 log.warn("上报报警失败，alarmId: {}", dto.getAlarmId());
//             }
//         } catch (NumberFormatException e) {
//             log.error("Error sending access state", e);
//         }
    }
}
