package cn.stylefeng.guns.database.service.impl;

import cn.stylefeng.guns.database.entity.TPipelineBaseInfo;
import cn.stylefeng.guns.database.entity.TStationBaseInfo;
import cn.stylefeng.guns.database.entity.TWorkareaBaseInfo;
import cn.stylefeng.guns.database.mapper.TStationBaseInfoMapper;
import cn.stylefeng.guns.database.service.TPipelineBaseInfoService;
import cn.stylefeng.guns.database.service.TStationBaseInfoService;
import cn.stylefeng.guns.database.service.TWorkareaBaseInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 站场基础信息表 Service实现
 *
 * @author system
 * @date 2026-01-14
 */
@Service
public class TStationBaseInfoServiceImpl extends ServiceImpl<TStationBaseInfoMapper, TStationBaseInfo> implements TStationBaseInfoService {

    @Autowired
    private TWorkareaBaseInfoService tWorkareaBaseInfoService;

    @Autowired
    private TPipelineBaseInfoService tPipelineBaseInfoService;

    @Override
    public String getBelongOperationAreaCode(TStationBaseInfo stationBaseInfo) {
        TWorkareaBaseInfo workArea = tWorkareaBaseInfoService.getById(stationBaseInfo.getBelongOperationArea());
        return workArea != null ? workArea.getWorkareaCode() : null;
    }

    @Override
    public String getBelongOperationAreaName(TStationBaseInfo stationBaseInfo) {
        TWorkareaBaseInfo workArea = tWorkareaBaseInfoService.getById(stationBaseInfo.getBelongOperationArea());
        return workArea != null ? workArea.getWorkareaName() : null;
    }

    @Override
    public String getBelongPipelineCode(TStationBaseInfo stationBaseInfo) {
        TPipelineBaseInfo pipeline = tPipelineBaseInfoService.getById(stationBaseInfo.getBelongPipeline());
        return pipeline != null ? pipeline.getPipelineCode() : null;
    }

    @Override
    public String getBelongPipelineName(TStationBaseInfo stationBaseInfo) {
        TPipelineBaseInfo pipeline = tPipelineBaseInfoService.getById(stationBaseInfo.getBelongPipeline());
        return pipeline != null ? pipeline.getPipelineName() : null;
    }
}