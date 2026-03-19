package cn.stylefeng.guns.modular.industrialTV.schedule;

import cn.stylefeng.guns.core.utils.SpringContextHolder;
import cn.stylefeng.guns.database.entity.*;
import cn.stylefeng.guns.database.service.TIndustrialTvBaseInfoService;
import cn.stylefeng.guns.database.service.TIndustrialTvPresetService;
import cn.stylefeng.guns.database.service.TVideoInspectionTaskResultRawService;
import cn.stylefeng.guns.database.service.TVideoInspectionTaskResultService;
import cn.stylefeng.guns.modular.hikvision.request.PresetRequest;
import cn.stylefeng.guns.modular.hikvision.service.HikVisionService;
import cn.stylefeng.guns.modular.industrialTV.service.VideoInspectionService;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class VideoInspectionRunnable implements Runnable {
    private TVideoInspectionTasks task;
    private TVideoInspectionTaskResult taskResult;

    @Override
    public void run() {
        VideoInspectionService videoInspectionService = SpringContextHolder.getBean(VideoInspectionService.class);
        TVideoInspectionTaskResultService resultService = SpringContextHolder.getBean(TVideoInspectionTaskResultService.class);
        TVideoInspectionTaskResultRawService resultRawService = SpringContextHolder.getBean(TVideoInspectionTaskResultRawService.class);
        TIndustrialTvPresetService presetService = SpringContextHolder.getBean(TIndustrialTvPresetService.class);
        TIndustrialTvBaseInfoService tvBaseInfoService = SpringContextHolder.getBean(TIndustrialTvBaseInfoService.class);
        try {
            //1，更新result状态
            taskResult.setInspectionStatus(TVideoInspectionTaskResult.INSPECT_STATUS_DOING);
            resultService.updateById(taskResult);
            List<TVideoInspectionCameraPreset> cameraPresents = task.getCameraPresets();
            if (cameraPresents.isEmpty()) {
                taskResult.setInspectionStatus(TVideoInspectionTaskResult.INSPECT_STATUS_ERROR);
                taskResult.setInspectionMessage("未配置摄像头预制点");
                taskResult.setEndTime(new Date());
                resultService.updateById(taskResult);
                return;
            }
            Set<String> presetIdSet = cameraPresents.stream().map(TVideoInspectionCameraPreset::getPresetId).collect(Collectors.toSet());
            Set<String> tvIdSet = cameraPresents.stream().map(TVideoInspectionCameraPreset::getIndustrialTvId).collect(Collectors.toSet());
            Map<String, TIndustrialTvPreset> presetMap = presetService.lambdaQuery().in(TIndustrialTvPreset::getPresetId, presetIdSet).list().stream().collect(Collectors.toMap(TIndustrialTvPreset::getPresetId, Function.identity()));
            Map<String, TIndustrialTvBaseInfo> tvBaseMap = tvBaseInfoService.lambdaQuery().in(TIndustrialTvBaseInfo::getDeviceId, tvIdSet).list().stream().collect(Collectors.toMap(TIndustrialTvBaseInfo::getDeviceId, Function.identity()));
            for (int i = 0; i < cameraPresents.size(); i++) {
                TVideoInspectionCameraPreset cameraPreset = cameraPresents.get(i);
                TVideoInspectionTaskResultRaw resultRaw = new TVideoInspectionTaskResultRaw();
                try{
                    resultRaw.setInspectionResultRawId(IdWorker.getIdStr());
                    resultRaw.setVideoInspectionId(task.getVideoInspectionId());
                    resultRaw.setInspectionResultId(taskResult.getInspectionResultId());
                    resultRaw.setIndustrialTvId(cameraPreset.getIndustrialTvId());
                    resultRaw.setPresetId(cameraPreset.getPresetId());
                    resultRaw.setPresetInspectResultStatus(TVideoInspectionTaskResultRaw.PRESET_INSPECT_RESULT_STATUS_SUCCESS);
                    resultRawService.save(resultRaw);
                    controlPreset(cameraPreset,presetMap,tvBaseMap);
                    Thread.sleep(cameraPreset.getStayDuration() * 1000); //停留秒数后执行下一轮
                    log.warn("taskId:{}, taskResultId:{}, cameraPresentId:{}, stayDuration:{}, ",
                            task.getVideoInspectionId(), taskResult.getInspectionResultId(), cameraPreset.getCameraPresetId(), cameraPreset.getStayDuration());
                }catch (Exception e){
                    log.error("execute video inspect error, taskId:{}, taskResult:{}", task.getVideoInspectionId(), taskResult.getInspectionResultId(), e);
                    resultRaw.setPresetInspectResultStatus(TVideoInspectionTaskResultRaw.PRESET_INSPECT_RESULT_STATUS_ERROR);
                    resultRaw.setRemark(e.getMessage());
                    resultRawService.save(resultRaw);
                }
                if(i == cameraPresents.size() - 1){
                    taskResult.setInspectionMessage("成功");
                    taskResult.setInspectionStatus(TVideoInspectionTaskResult.INSPECT_STATUS_FINISHED);
                    taskResult.setEndTime(new Date());
                    resultService.updateById(taskResult);
                }
            }
        } finally {
            videoInspectionService.generateTaskResultPlan(task);
        }
    }

    private void controlPreset(TVideoInspectionCameraPreset cameraPreset,Map<String, TIndustrialTvPreset> presetMap,Map<String, TIndustrialTvBaseInfo> tvBaseMap){
        try{
//            LiveGBSService liveGBSService = SpringContextHolder.getBean(LiveGBSService.class);
            TIndustrialTvPreset preset = presetMap.get(cameraPreset.getPresetId());
            TIndustrialTvBaseInfo tv = tvBaseMap.get(cameraPreset.getIndustrialTvId());
//            ControlPresetRequestDTO requestDto = new ControlPresetRequestDTO();
//            requestDto.setSerial(tv.getGbCode());
//            requestDto.setCode(tv.getStreamChannel());
//            requestDto.setName(preset.getPresetName());
//            requestDto.setPreset(preset.getPresetCode());
//            requestDto.setCommand("goto");
//            liveGBSService.controlPreset(requestDto);
            HikVisionService hikVisionService = SpringContextHolder.getBean(HikVisionService.class);
            PresetRequest presetRequest = new PresetRequest();
            presetRequest.setPresetIndex(preset.getPresetCode());
            presetRequest.setPresetName(preset.getPresetName());
            presetRequest.setDeviceId(tv.getDeviceId());
            hikVisionService.gotoPreset(presetRequest);
        }catch (Exception e){
            log.error("巡检预置点控制失败, 预置点:{}", cameraPreset.getCameraPresetId(), e);
        }
    }

}
