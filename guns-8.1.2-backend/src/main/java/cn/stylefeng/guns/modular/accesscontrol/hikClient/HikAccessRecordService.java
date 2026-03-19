package cn.stylefeng.guns.modular.accesscontrol.hikClient;

import cn.hutool.core.util.ObjectUtil;
import cn.stylefeng.guns.database.entity.TAccessControlBaseInfo;
import cn.stylefeng.guns.database.entity.TAccessControlEntryExitRecords;
import cn.stylefeng.guns.database.service.TAccessControlBaseInfoService;
import cn.stylefeng.guns.database.service.TAccessControlEntryExitRecordsService;
import cn.stylefeng.guns.modular.accesscontrol.util.AlarmTypeUtil;
import cn.stylefeng.guns.modular.hikvision.NetSDKDemo.HCNetSDK;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HikAccessRecordService {

    private final HikSdkManager hikSdkManager;
    private final HikUtil hikUtil;
    private final TAccessControlBaseInfoService deviceService;
    private final TAccessControlEntryExitRecordsService recordService;

    /**
     * 同步出入记录（存在则更新，不存在则插入）
     */
    public void syncAccessRecordsData() {
        if (!hikSdkManager.initSdk()) {
            log.error("SDK初始化失败，终止出入记录同步");
            return;
        }

        // 1. 加载历史组合键（设备ID_时间戳），用于快速判断
        Set<String> uniqueRecordKeys = recordService.list().stream()
                .map(record -> record.getAccessControlDeviceId() + "_" + record.getEntryTime().getTime())
                .collect(Collectors.toSet());

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
                eventCond.dwMajor = 5; // 门禁主事件
                eventCond.dwMinor = 75; // 人脸认证成功次事件
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
                        // 保存/更新记录
                        saveOrUpdateAccessRecord(eventCfg, device, eventCount, uniqueRecordKeys);
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

    /**
     * 保存/更新出入记录（核心修改：存在则更新，不存在则插入）
     */
    private void saveOrUpdateAccessRecord(HCNetSDK.NET_DVR_ACS_EVENT_CFG eventCfg, TAccessControlBaseInfo device, int eventCount, Set<String> uniqueRecordKeys) {
        String employeeNo = new String(eventCfg.struAcsEventInfo.byEmployeeNo).trim();
        if (ObjectUtil.isNotEmpty(employeeNo)) {
            // 上传事件图片
            Long picUrl = null;
            if (eventCfg.dwPicDataLen > 0 && eventCfg.pPicData != null) {
                String fileName = device.getIpAddress() + "_" + eventCount + "_event.jpg";
                picUrl = hikUtil.uploadPicWithoutUrl(
                        eventCfg.pPicData.getByteBuffer(0, eventCfg.dwPicDataLen),
                        eventCfg.dwPicDataLen,
                        fileName
                );
            }

            // 构建记录对象
            TAccessControlEntryExitRecords record = new TAccessControlEntryExitRecords();
            String dateStr = hikUtil.buildHikDateStr(
                    eventCfg.struTime.dwYear, eventCfg.struTime.dwMonth, eventCfg.struTime.dwDay,
                    eventCfg.struTime.dwHour, eventCfg.struTime.dwMinute, eventCfg.struTime.dwSecond
            );
            Date entryTime = hikUtil.parseNormalDate(dateStr);
            String deviceId = device.getDeviceId();
            // 生成唯一组合键
            String uniqueKey = deviceId + "_" + entryTime.getTime();

            // 核心逻辑：判断是否存在，存在则更新，不存在则插入
            if (uniqueRecordKeys.contains(uniqueKey)) {
                // 1. 已存在 → 执行更新操作
                LambdaUpdateWrapper<TAccessControlEntryExitRecords> updateWrapper = new LambdaUpdateWrapper<>();
                // 更新条件：设备ID + 进入时间 匹配
                updateWrapper.eq(TAccessControlEntryExitRecords::getAccessControlDeviceId, deviceId)
                        .eq(TAccessControlEntryExitRecords::getEntryTime, entryTime);
                // 设置需要更新的字段（根据业务需求调整，示例更新人员ID、图片、进入方式）
                record.setPersonnelId(employeeNo);
                if (picUrl != null) {
                    record.setImageAddress(picUrl.toString());
                }
                int major = eventCfg.dwMajor;
                int minor = eventCfg.dwMinor;
                String majorDesc = AlarmTypeUtil.getMajorDescription(major);
                String minorDesc = AlarmTypeUtil.getMinorDescription(major, minor);
                record.setEntryMethod(majorDesc + ":" + minorDesc);

                boolean updateSuccess = recordService.update(record, updateWrapper);
                if (updateSuccess) {
                    log.info("更新出入记录（设备ID：{}，时间：{}）成功", deviceId, entryTime);
                } else {
                    log.error("更新出入记录（设备ID：{}，时间：{}）失败", deviceId, entryTime);
                }
            } else {
                // 2. 不存在 → 执行插入操作
                record.setEntryTime(entryTime);
                record.setAccessControlDeviceId(deviceId);
                record.setPersonnelId(employeeNo);
                if (picUrl != null) {
                    record.setImageAddress(picUrl.toString());
                }
                int major = eventCfg.dwMajor;
                int minor = eventCfg.dwMinor;
                String majorDesc = AlarmTypeUtil.getMajorDescription(major);
                String minorDesc = AlarmTypeUtil.getMinorDescription(major, minor);
                record.setEntryMethod(majorDesc + ":" + minorDesc);

                recordService.save(record);
                // 将新键加入集合，避免后续重复判断
                uniqueRecordKeys.add(uniqueKey);
                log.info("插入新出入记录：{}", record);
            }
        }
    }
}