package cn.stylefeng.guns.modular.industrialTV.schedule;

import cn.stylefeng.guns.database.entity.TIndustrialTvBaseInfo;
import cn.stylefeng.guns.database.service.TIndustrialTvBaseInfoService;
import cn.stylefeng.guns.zlmediakit.ZlMediaKitService;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RecordVideoSchedule {

    @Autowired
    private ZlMediaKitService zlMediaKitService;

    @Autowired
    private TIndustrialTvBaseInfoService tIndustrialTvBaseInfoService;

    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);
    private final Map<String, LocalDateTime> recordingStartTimes = new ConcurrentHashMap<>();

    /**
     * 每小时的第0分钟触发，开始新的录制片段
     * 使用cron表达式: 0 0 * * * ? 表示每小时整点执行
     */
//    @Scheduled(cron = "0 0 * * * ?")
    public void scheduleHourlyRecording() {
        log.info("开始执行每小时视频录制定时任务, 时间: {}", LocalDateTime.now());
        try {
            List<TIndustrialTvBaseInfo> industrialTvList = tIndustrialTvBaseInfoService.list();
            if (CollectionUtils.isEmpty(industrialTvList)) {
                log.warn("没有找到工业电视设备");
                return;
            }

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime hourEnd = now.plusHours(1).withMinute(0).withSecond(0).withNano(0);

            log.info("当前时间: {}, 当前小时结束时间: {}", now, hourEnd);

            for (TIndustrialTvBaseInfo industrialTv : industrialTvList) {
                if (StringUtils.isBlank(industrialTv.getStreamAddress())) {
                    log.warn("工业电视设备 {} 没有配置流媒体地址，跳过录制", industrialTv.getDeviceName());
                    continue;
                }

                startRecordingWithTimer(industrialTv, hourEnd);
            }

        } catch (Exception e) {
            log.error("执行每小时视频录制定时任务失败", e);
        }
    }

    /**
     * 应用启动时立即执行一次，确保应用重启后继续录制
     */
//    @Scheduled(fixedDelay = 60000, initialDelay = 5000)
    public void checkAndResumeRecording() {
        log.debug("检查并恢复录制任务");
        try {
            List<TIndustrialTvBaseInfo> industrialTvList = tIndustrialTvBaseInfoService.list();
            if (CollectionUtils.isEmpty(industrialTvList)) {
                return;
            }

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime hourEnd = now.plusHours(1).withMinute(0).withSecond(0).withNano(0);

            for (TIndustrialTvBaseInfo industrialTv : industrialTvList) {
                if (StringUtils.isBlank(industrialTv.getStreamAddress())) {
                    continue;
                }

                String key = industrialTv.getDeviceId();
                LocalDateTime recordedStart = recordingStartTimes.get(key);

                if (recordedStart == null || hourEnd.isBefore(recordedStart)) {
                    startRecordingWithTimer(industrialTv, hourEnd);
                }
            }

        } catch (Exception e) {
            log.error("检查并恢复录制任务失败", e);
        }
    }

    /**
     * 开始录制并设置定时停止
     */
    private void startRecordingWithTimer(TIndustrialTvBaseInfo industrialTv, LocalDateTime endTime) {
        String deviceId = industrialTv.getDeviceId();
        String streamAddress = industrialTv.getStreamAddress();

        try {
            log.info("开始录制工业电视 {} (流地址: {})", industrialTv.getDeviceName(), streamAddress);

            boolean startSuccess = zlMediaKitService.startRecord(streamAddress);
            if (!startSuccess) {
                log.error("启动录制失败: 工业电视 {}, 流地址: {}", industrialTv.getDeviceName(), streamAddress);
                return;
            }

            LocalDateTime now = LocalDateTime.now();
            recordingStartTimes.put(deviceId, now);
            log.info("录制已启动: 设备ID={}, 开始时间={}", deviceId, now);

            long delaySeconds = ChronoUnit.SECONDS.between(now, endTime);
            if (delaySeconds <= 0) {
                log.warn("计算得到延迟时间小于等于0，将在10秒后停止录制: 设备ID={}", deviceId);
                delaySeconds = 10;
            }

            log.info("将在 {} 秒后停止录制: 设备ID={}, 预计结束时间={}", delaySeconds, deviceId, endTime);

            executorService.schedule(() -> stopRecording(industrialTv), delaySeconds, TimeUnit.SECONDS);

        } catch (Exception e) {
            log.error("启动录制失败: 工业电视 {}, 流地址: {}", industrialTv.getDeviceName(), streamAddress, e);
        }
    }

    /**
     * 停止录制
     */
    private void stopRecording(TIndustrialTvBaseInfo industrialTv) {
        String deviceId = industrialTv.getDeviceId();
        String streamAddress = industrialTv.getStreamAddress();

        try {
            log.info("停止录制工业电视 {} (流地址: {})", industrialTv.getDeviceName(), streamAddress);

            boolean stopSuccess = zlMediaKitService.stopRecord(streamAddress);
            if (stopSuccess) {
                LocalDateTime startTime = recordingStartTimes.remove(deviceId);
                LocalDateTime endTime = LocalDateTime.now();
                log.info("录制已停止: 设备ID={}, 开始时间={}, 结束时间={}, 持续时间={}秒",
                        deviceId, startTime, endTime,
                        startTime != null ? ChronoUnit.SECONDS.between(startTime, endTime) : "未知");
            } else {
                log.error("停止录制失败: 工业电视 {}, 流地址: {}", industrialTv.getDeviceName(), streamAddress);
            }

        } catch (Exception e) {
            log.error("停止录制失败: 工业电视 {}, 流地址: {}", industrialTv.getDeviceName(), streamAddress, e);
        }
    }

}
