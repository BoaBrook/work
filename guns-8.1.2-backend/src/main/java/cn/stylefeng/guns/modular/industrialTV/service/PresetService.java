package cn.stylefeng.guns.modular.industrialTV.service;

import cn.stylefeng.guns.core.utils.StringUtils;
import cn.stylefeng.guns.database.entity.TIndustrialTvBaseInfo;
import cn.stylefeng.guns.database.entity.TIndustrialTvPreset;
import cn.stylefeng.guns.database.service.TIndustrialTvBaseInfoService;
import cn.stylefeng.guns.database.service.TIndustrialTvPresetService;
import cn.stylefeng.guns.liveGBS.LiveGBSService;
import cn.stylefeng.guns.modular.hikvision.request.PresetRequest;
import cn.stylefeng.guns.modular.hikvision.service.HikVisionService;
import cn.stylefeng.guns.modular.industrialTV.request.PresetUpdateRequest;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PresetService {

    @Autowired
    private TIndustrialTvBaseInfoService tIndustrialTvBaseInfoService;

    @Autowired
    private LiveGBSService liveGBSService;

    @Autowired
    private HikVisionService hikVisionService;

    @Autowired
    private TIndustrialTvPresetService tIndustrialTvPresetService;

    public List<TIndustrialTvPreset> presetQuery(String deviceId) {
        return tIndustrialTvPresetService.lambdaQuery().eq(TIndustrialTvPreset::getIndustrialTvId, deviceId).list();
    }

    @Transactional
    public synchronized Boolean presetUpdate(PresetUpdateRequest request) {
        TIndustrialTvBaseInfo industrialTv = tIndustrialTvBaseInfoService.getById(request.getDeviceId());
        Integer presetCode;
        List<TIndustrialTvPreset> presetList = tIndustrialTvPresetService.lambdaQuery().eq(TIndustrialTvPreset::getIndustrialTvId, request.getDeviceId()).list();
        if(StringUtils.isEmpty(request.getPresetId())){
            TIndustrialTvPreset preset = new TIndustrialTvPreset();
            preset.setIndustrialTvId(request.getDeviceId());
            preset.setPresetId(IdWorker.getIdStr());
            preset.setPresetName(request.getPresetName());
            if (!StringUtils.isEmpty(presetList)) {
                presetCode = presetList.stream()
                        .mapToInt(TIndustrialTvPreset::getPresetCode)
                        .max()
                        .orElse(0) + 1;
            } else {
                presetCode = 0; // 如果列表为空，则从1开始
            }
            preset.setPresetCode(presetCode);
            tIndustrialTvPresetService.save(preset);
        }else{
            TIndustrialTvPreset preset = tIndustrialTvPresetService.getById(request.getPresetId());
            preset.setPresetName(request.getPresetName());
            tIndustrialTvPresetService.updateById(preset);
            presetCode = preset.getPresetCode();
        }
//        ControlPresetRequestDTO controlPresetRequestDTO = new ControlPresetRequestDTO();
//        controlPresetRequestDTO.setSerial(industrialTv.getGbCode());
//        controlPresetRequestDTO.setCode(industrialTv.getStreamChannel());
//        controlPresetRequestDTO.setName(request.getPresetName());
//        controlPresetRequestDTO.setCommand("set");
//        controlPresetRequestDTO.setPreset(presetCode);
//        return liveGBSService.controlPreset(controlPresetRequestDTO);
        PresetRequest presetRequest = new PresetRequest();
        presetRequest.setDeviceId(industrialTv.getDeviceId());
        presetRequest.setPresetName(request.getPresetName());
        presetRequest.setPresetIndex(presetCode);
        return hikVisionService.setPreset(presetRequest);
    }
    
    @Transactional
    public Boolean presetBatchDelete(List<String> presetIdList) {
        List<TIndustrialTvPreset> tIndustrialTvPresetList = tIndustrialTvPresetService.listByIds(presetIdList);
        if(CollectionUtils.isEmpty(tIndustrialTvPresetList)){
            return true;
        }
        Set<String> industrialTvIdSet = tIndustrialTvPresetList.stream().map(TIndustrialTvPreset::getIndustrialTvId).collect(Collectors.toSet());
        List<TIndustrialTvBaseInfo> tvBaseInfoList = tIndustrialTvBaseInfoService.listByIds(industrialTvIdSet);
        Map<String, TIndustrialTvBaseInfo> tvMap = tvBaseInfoList.stream().collect(Collectors.toMap(TIndustrialTvBaseInfo::getDeviceId, Function.identity()));
        tIndustrialTvPresetService.removeByIds(presetIdList);
        for (TIndustrialTvPreset preset : tIndustrialTvPresetList) {
            TIndustrialTvBaseInfo tv = tvMap.get(preset.getIndustrialTvId());
            if (tv != null) {
//                ControlPresetRequestDTO controlPresetRequestDTO = new ControlPresetRequestDTO();
//                controlPresetRequestDTO.setSerial(tv.getGbCode());
//                controlPresetRequestDTO.setCode(tv.getStreamChannel());
//                controlPresetRequestDTO.setName(preset.getPresetName());
//                controlPresetRequestDTO.setCommand("remove");
//                controlPresetRequestDTO.setPreset(preset.getPresetCode());
//                liveGBSService.controlPreset(controlPresetRequestDTO);
                PresetRequest presetRequest = new PresetRequest();
                presetRequest.setDeviceId(tv.getDeviceId());
                presetRequest.setPresetName(preset.getPresetName());
                presetRequest.setPresetIndex(preset.getPresetCode());
                hikVisionService.removePreset(presetRequest);
            }
        }
        return true;
    }

}
