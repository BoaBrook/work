package cn.stylefeng.guns.modular.accesscontrol.hikClient;

import cn.stylefeng.guns.core.consts.AlarmResultConstants;
import cn.stylefeng.guns.database.entity.TAccessControlBaseInfo;
import cn.stylefeng.guns.database.entity.TAlarmResultRecords;
import cn.stylefeng.guns.database.entity.TIndustrialTvBaseInfo;
import cn.stylefeng.guns.database.service.TAccessControlBaseInfoService;
import cn.stylefeng.guns.database.service.TAlarmResultRecordsService;
import cn.stylefeng.guns.enums.SystemTypeEnum;
import cn.stylefeng.guns.modular.accesscontrol.request.AccessLinkageAlarmRequest;
import cn.stylefeng.guns.modular.accesscontrol.service.AccessControlManageService;
import cn.stylefeng.guns.modular.accesscontrol.util.AlarmTypeUtil;
import cn.stylefeng.guns.modular.hikvision.NetSDKDemo.HCNetSDK;
import cn.stylefeng.guns.modular.industrialTV.request.LinkageAlarmRequest;
import cn.stylefeng.guns.modular.nodeSystem.constants.dict.DeviceTypeEnum;
import cn.stylefeng.guns.modular.nodeSystem.dto.AlarmRawDTO;
import cn.stylefeng.guns.modular.nodeSystem.service.NodeSystemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HikAlarmRocordService {

    private final HikSdkManager hikSdkManager;
    private final HikUtil hikUtil;
    private final TAccessControlBaseInfoService deviceService;
    @Resource
    private TAlarmResultRecordsService tAlarmResultRecordsService;
    @Resource
    private AccessControlManageService accessControlManageService;
    @Resource
    private NodeSystemService nodeSystemService;
    @Resource
    private TAccessControlBaseInfoService tAccessControlBaseInfoService;



    public void syncAlarmRecordsData() {
        if (!hikSdkManager.initSdk()) {
            log.error("SDK初始化失败，终止出入记录同步");
            return;
        }

        // LambdaQueryWrapper<TAccessControlBaseInfo> wrapper = new LambdaQueryWrapper<>();
        // wrapper.eq(TAccessControlBaseInfo::getIpAddress,"171.7.99.86");
        // List<TAccessControlBaseInfo> deviceList = deviceService.list(wrapper);
        List<TAccessControlBaseInfo> deviceList = deviceService.list();
        HCNetSDK hcNetSDK = hikSdkManager.getHCNetSDK();

        for (TAccessControlBaseInfo device : deviceList) {
            int userID = hikSdkManager.loginDevice(device.getIpAddress(), device.getPort().shortValue(), device.getAccessAccount(), device.getAccessPassword());
            if (userID == -1) {
                log.error("设备[{}]登录失败，跳过", device.getIpAddress());
                continue;
            }

            try {
                // 构建查询条件
                HCNetSDK.NET_DVR_ACS_EVENT_COND eventCond = new HCNetSDK.NET_DVR_ACS_EVENT_COND();
                eventCond.read();
                eventCond.dwSize = eventCond.size();
                eventCond.dwMajor = 1; // 门禁主事件-报警
                eventCond.dwMinor = 0; // 所有事件
                eventCond.byPicEnable = 1; // 带图片
                eventCond.wInductiveEventType = 1;

                // 设置查询时间范围（当日0点到当前时间）
                LocalDateTime endTime = LocalDateTime.now();
                LocalDateTime startTime = endTime.minusMinutes(30);
                hikUtil.fillHikTime(eventCond.struStartTime, startTime);
                hikUtil.fillHikTime(eventCond.struEndTime, endTime);
                eventCond.write();

                // 启动事件查询
                int handler = hcNetSDK.NET_DVR_StartRemoteConfig(
                        userID,
                        HCNetSDK.NET_DVR_GET_ACS_EVENT,
                        eventCond.getPointer(),
                        eventCond.size(),
                        null,
                        null
                );
                if (handler <= -1) {
                    log.error("设备[{}]启动事件查询失败，错误码：{}", device.getIpAddress(), hcNetSDK.NET_DVR_GetLastError());
                    continue;
                }

                // 循环获取事件
                HCNetSDK.NET_DVR_ACS_EVENT_CFG eventCfg = new HCNetSDK.NET_DVR_ACS_EVENT_CFG();
                eventCfg.read();
                eventCfg.dwSize = eventCfg.size();
                eventCfg.write();
                int eventCount = 0;

                while (true) {
                    int state = hcNetSDK.NET_DVR_GetNextRemoteConfig(handler, eventCfg.getPointer(), eventCfg.size());

                    if (state <= -1) {
                        log.error("设备[{}]获取事件失败，错误码：{}", device.getIpAddress(), hcNetSDK.NET_DVR_GetLastError());
                        break;
                    } else if (state == HCNetSDK.NET_SDK_GET_NEXT_STATUS_NEED_WAIT) {
                        Thread.sleep(10);
                        continue;
                    } else if (state == HCNetSDK.NET_SDK_NEXT_STATUS__FINISH) {
                        log.info("设备[{}]事件获取完成，共{}条", device.getIpAddress(), eventCount);
                        break;
                    } else if (state == HCNetSDK.NET_SDK_GET_NEXT_STATUS_FAILED) {
                        log.error("设备[{}]获取事件出现异常，错误码：{}", device.getIpAddress(), hcNetSDK.NET_DVR_GetLastError());
                        break;
                    } else if (state == HCNetSDK.NET_SDK_GET_NEXT_STATUS_SUCCESS) {
                        eventCfg.read();
                        log.info("获取事件成功, 报警主类型：{} 报警次类型：{} 卡号：{}",
                                Integer.toHexString(eventCfg.dwMajor),
                                Integer.toHexString(eventCfg.dwMinor),
                                new String(eventCfg.struAcsEventInfo.byEmployeeNo).trim());
                        log.info("刷卡时间：年：{} 月：{} 日：{} 时：{} 分：{} 秒：{}",
                                eventCfg.struTime.dwYear, eventCfg.struTime.dwMonth, eventCfg.struTime.dwDay,
                                eventCfg.struTime.dwHour, eventCfg.struTime.dwMinute, eventCfg.struTime.dwSecond);
                        Long picUrl = null;
                        if (eventCfg.dwPicDataLen > 0 && eventCfg.pPicData != null) {
                            String fileName = device.getIpAddress() + "_" + eventCount + "_event.jpg";
                            picUrl = hikUtil.uploadPicWithoutUrl(
                                    eventCfg.pPicData.getByteBuffer(0, eventCfg.dwPicDataLen),
                                    eventCfg.dwPicDataLen,
                                    fileName
                            );
                        }
                        String dateStr = hikUtil.buildHikDateStr(
                                eventCfg.struTime.dwYear, eventCfg.struTime.dwMonth, eventCfg.struTime.dwDay,
                                eventCfg.struTime.dwHour, eventCfg.struTime.dwMinute, eventCfg.struTime.dwSecond
                        );
                        Date entryTime = hikUtil.parseNormalDate(dateStr);
                        int major = eventCfg.dwMajor;
                        int minor = eventCfg.dwMinor;
                        String majorDesc = AlarmTypeUtil.getMajorDescription(major);
                        String minorDesc = AlarmTypeUtil.getMinorDescription(major, minor);

                        //保存报警记录
                        TAlarmResultRecords alarmRecord = new TAlarmResultRecords();
                        alarmRecord.setAlarmDeviceId(device.getDeviceId());
                        alarmRecord.setAlarmDeviceName(device.getDeviceName());
                        // alarmRecord.setAlarmLocation(areaName);
                        alarmRecord.setSubsystemType(SystemTypeEnum.MJXT.getCode());
                        alarmRecord.setAlarmType("门禁");
                        alarmRecord.setAlarmLevel("I");
                        alarmRecord.setAlarmContent(alarmRecord.getAlarmType() + "-" + majorDesc + "-" + minorDesc);
                        alarmRecord.setAlarmTime(entryTime);
                        alarmRecord.setDisposalStatus(AlarmResultConstants.DISPOSAL_STATUS_UNDISPOSED);
                        alarmRecord.setCreateTime(new Date());
                        if (picUrl != null) {
                            alarmRecord.setAlarmImage(picUrl.toString());
                        }
                        tAlarmResultRecordsService.save(alarmRecord);

                        //联动报警
                        AccessLinkageAlarmRequest request = new AccessLinkageAlarmRequest();
                        request.setAccessControlDeviceId(device.getDeviceId());
                        request.setAlarmType("I");
                        accessControlManageService.linkageAlarm(request);

                        // 推送省级平台
                        sendAlarmMessage(alarmRecord);

                        eventCount++;
                    }
                }

                // 停止远程配置
                if (!hcNetSDK.NET_DVR_StopRemoteConfig(handler)) {
                    log.error("设备[{}]停止事件查询失败，错误码：{}", device.getIpAddress(), hcNetSDK.NET_DVR_GetLastError());
                }
            } catch (InterruptedException e) {
                log.error("设备[{}]事件查询线程等待异常", device.getIpAddress(), e);
                Thread.currentThread().interrupt();
            } finally {
                hikSdkManager.logoutDevice(userID);
            }
        }
    }

    private void sendAlarmMessage(TAlarmResultRecords alarmResultRecords) {
        try {
            AlarmRawDTO alarmRaw = new AlarmRawDTO();
            // 告警唯一标识：节点编码+告警标识
            String nodeCode = nodeSystemService.getNodeCode();
            alarmRaw.setAlarmId(nodeCode + "_" + alarmResultRecords.getAlarmId());
            // 节点编码
            alarmRaw.setNodeCode(nodeCode);
            // 设备编码（使用传感器设备编码）
            TAccessControlBaseInfo device = tAccessControlBaseInfoService.getById(alarmResultRecords.getAlarmDeviceId());
            alarmRaw.setDeviceCode(device.getDeviceCode());
            // 设备名称（使用传感器设备名称）
            alarmRaw.setDeviceName(device.getDeviceName());
            // 设备点位（位置）
//            alarmRaw.setDeviceLocation(device.getAlarmLocation());
            // 设备类型：火气系统
            alarmRaw.setDeviceType(DeviceTypeEnum.ACCESS_CONTROL_SYSTEM.getCode());
            // 告警级别：1-I级
            alarmRaw.setAlarmLevel(Integer.parseInt(alarmResultRecords.getAlarmLevel()));
            // 告警类型
            alarmRaw.setAlarmType(alarmResultRecords.getAlarmType());
            // 告警内容
            alarmRaw.setAlarmContent(alarmResultRecords.getAlarmContent());
            // 告警时间（格式：yyyy-MM-dd HH:mm:ss）
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            alarmRaw.setAlarmTime(sdf.format(alarmResultRecords.getAlarmTime()));
            // 发送报警
            nodeSystemService.sendAlarmRaw(alarmRaw);
        } catch (Exception e) {
            log.error("调用sendAlarmRaw接口失败，alarmId: {}", alarmResultRecords.getAlarmId(), e);
        }
    }
}
