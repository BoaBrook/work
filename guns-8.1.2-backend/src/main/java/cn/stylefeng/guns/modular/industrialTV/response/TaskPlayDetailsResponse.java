package cn.stylefeng.guns.modular.industrialTV.response;

import cn.stylefeng.guns.database.entity.TIndustrialTvBaseInfo;
import cn.stylefeng.guns.database.entity.TVideoInspectionCameraPreset;
import cn.stylefeng.guns.database.entity.TVideoInspectionTaskResult;
import lombok.Data;

import java.util.List;

@Data
public class TaskPlayDetailsResponse {

    private TVideoInspectionTaskResult taskResult;

    private List<TVideoInspectionCameraPreset> CameraPresetList;

    private List<TIndustrialTvBaseInfo> industrialTvBaseInfoList;

}
