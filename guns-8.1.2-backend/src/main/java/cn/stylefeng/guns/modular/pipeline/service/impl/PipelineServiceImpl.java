package cn.stylefeng.guns.modular.pipeline.service.impl;

import cn.stylefeng.guns.database.entity.TPipelineBaseInfo;
import cn.stylefeng.guns.database.entity.TStationBaseInfo;
import cn.stylefeng.guns.database.service.TPipelineBaseInfoService;
import cn.stylefeng.guns.database.service.TStationBaseInfoService;
import cn.stylefeng.guns.modular.pipeline.dto.PipelineWithStations;
import cn.stylefeng.guns.modular.pipeline.dto.StationOption;
import cn.stylefeng.guns.modular.pipeline.request.PipelineListRequest;
import cn.stylefeng.guns.modular.pipeline.service.PipelineService;
import cn.stylefeng.guns.core.utils.PageToPageResultUtils;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PipelineServiceImpl implements PipelineService {

    @Autowired
    private TPipelineBaseInfoService pipelineBaseInfoService;

    @Autowired
    private TStationBaseInfoService stationBaseInfoService;

    @Override
    public PageResult<PipelineWithStations> getPipelineList(PipelineListRequest request) {
        Integer pageNo = request.getPageNo();
        Integer pageSize = request.getPageSize();
        String pipelineName = request.getPipelineName();
        String stationId = request.getStationId();
        
        Page<TPipelineBaseInfo> page = new Page<>(pageNo == null ? 1 : pageNo, pageSize == null ? 10 : pageSize);
        LambdaQueryWrapper<TPipelineBaseInfo> wrapper = new LambdaQueryWrapper<>();

        if (stationId != null && !stationId.isEmpty()) {
            TStationBaseInfo station = stationBaseInfoService.getById(stationId);
            String belongPipeline = (station != null && station.getBelongPipeline() != null)
                    ? station.getBelongPipeline().trim() : "";
            if (belongPipeline.isEmpty()) {
                wrapper.eq(TPipelineBaseInfo::getPipelineId, "");
            } else {
                wrapper.eq(TPipelineBaseInfo::getPipelineId, belongPipeline);
            }
        }

        if (pipelineName != null && !pipelineName.isEmpty()) {
            wrapper.like(TPipelineBaseInfo::getPipelineName, pipelineName);
        }

        Page<TPipelineBaseInfo> result = pipelineBaseInfoService.page(page, wrapper);

        List<PipelineWithStations> pipelineWithStationsList = result.getRecords().stream()
            .map(this::convertToPipelineWithStations)
            .collect(Collectors.toList());
        
        Page<PipelineWithStations> pipelineWithStationsPage = new Page<>();
        pipelineWithStationsPage.setCurrent(result.getCurrent());
        pipelineWithStationsPage.setSize(result.getSize());
        pipelineWithStationsPage.setTotal(result.getTotal());
        pipelineWithStationsPage.setPages(result.getPages());
        pipelineWithStationsPage.setRecords(pipelineWithStationsList);
        
        return PageToPageResultUtils.pageToPageResult(pipelineWithStationsPage);
    }

    private PipelineWithStations convertToPipelineWithStations(TPipelineBaseInfo pipeline) {
        PipelineWithStations pipelineWithStations = new PipelineWithStations();
        BeanUtils.copyProperties(pipeline, pipelineWithStations);
        
        List<TStationBaseInfo> stations = getStationsByPipeline(pipeline.getPipelineId());
        String stationName = stations.stream()
            .map(TStationBaseInfo::getStationName)
            .collect(Collectors.joining(","));
        pipelineWithStations.setStationName(stationName);
        
        return pipelineWithStations;
    }

    @Override
    public boolean savePipeline(TPipelineBaseInfo pipelineBaseInfo) {
        return pipelineBaseInfoService.save(pipelineBaseInfo);
    }

    @Override
    public boolean updatePipeline(TPipelineBaseInfo pipelineBaseInfo) {
        return pipelineBaseInfoService.updateById(pipelineBaseInfo);
    }

    @Override
    public boolean deletePipeline(List<String> pipelineIds) {
        if (pipelineIds == null || pipelineIds.isEmpty()) {
            return true;
        }
        return pipelineBaseInfoService.removeByIds(pipelineIds);
    }

    @Override
    public List<TStationBaseInfo> getStationsByPipeline(String pipelineId) {
        LambdaQueryWrapper<TStationBaseInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TStationBaseInfo::getBelongPipeline, pipelineId);
        return stationBaseInfoService.list(wrapper);
    }

    @Override
    public List<StationOption> getAllStationsForOption() {
        List<TStationBaseInfo> stationList = stationBaseInfoService.list();
        return stationList.stream().map(station -> {
            StationOption option = new StationOption();
            option.setStationId(station.getStationId());
            option.setStationName(station.getStationName());
            return option;
        }).collect(Collectors.toList());
    }

    @Override
    public PipelineWithStations getPipelineById(String pipelineId) {
        TPipelineBaseInfo pipeline = pipelineBaseInfoService.getById(pipelineId);
        
        return convertToPipelineWithStations(pipeline);
    }

    @Override
    public List<TPipelineBaseInfo> getAllPipelines() {
        return pipelineBaseInfoService.list();
    }

}
