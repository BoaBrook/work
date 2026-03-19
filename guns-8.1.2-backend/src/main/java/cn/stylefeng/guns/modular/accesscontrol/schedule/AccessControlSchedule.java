package cn.stylefeng.guns.modular.accesscontrol.schedule;

import cn.stylefeng.guns.modular.accesscontrol.hikClient.HikAccessRecordService;
import cn.stylefeng.guns.modular.accesscontrol.hikClient.HikAlarmRocordService;
import cn.stylefeng.guns.modular.accesscontrol.hikClient.HikDeviceStateService;
import cn.stylefeng.guns.modular.accesscontrol.hikClient.HikPersonSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccessControlSchedule  {

    private final HikPersonSyncService personSyncService;
    private final HikDeviceStateService deviceStateService;
    private final HikAccessRecordService accessRecordService;
    private final HikAlarmRocordService hikAlarmRocordService;


    /**
     * 每日凌晨1点同步人员数据
     */
    @Scheduled(cron = "0 2 0/1 * * ?")
    public void syncPersonalData() {
        try {
            log.info("开始人员数据同步");
            personSyncService.syncPersonalData();
        } catch (Exception e) {
            log.error("人员数据同步失败", e);
        }
    }

    /**
     * 每4分钟检查一次设备状态（从第1分钟开始）
     */
    @Scheduled(cron = "0 1/4 * * * ?")
    public void checkDeviceState() {
        try {
            deviceStateService.checkDeviceState();
        } catch (Exception e) {
            log.error("设备状态检查失败", e);
        }
    }

    /**
     * 每3分钟同步一次出入记录
     */
    @Scheduled(cron = "0 7/6 * * * ?")
    public void syncAccessRecordsData() {
        try {
            log.info("开始同步出入记录同步");
            accessRecordService.syncAccessRecordsData();
        } catch (Exception e) {
            log.error("出入记录同步失败", e);
        }
    }

    /**
     * 每3分钟同步一次出入记录
     */
    @Scheduled(cron = "0 10/20 * * * ?")
    public void syncAlarmRecordsData() {
        try {
            log.info("开始同步门禁报警数据");
            hikAlarmRocordService.syncAlarmRecordsData();
        } catch (Exception e) {
            log.error("门禁报警数据同步失败", e);
        }
    }
}
