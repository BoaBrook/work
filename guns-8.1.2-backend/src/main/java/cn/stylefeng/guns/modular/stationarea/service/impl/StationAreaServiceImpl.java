package cn.stylefeng.guns.modular.stationarea.service.impl;

import cn.stylefeng.guns.core.utils.PageToPageResultUtils;
import cn.stylefeng.guns.database.entity.TStationAreaBaseInfo;
import cn.stylefeng.guns.database.entity.TStationBaseInfo;
import cn.stylefeng.guns.database.mapper.TStationAreaBaseInfoMapper;
import cn.stylefeng.guns.database.service.TStationBaseInfoService;
import cn.stylefeng.guns.modular.pipeline.dto.StationOption;
import cn.stylefeng.guns.modular.pipeline.service.PipelineService;
import cn.stylefeng.guns.modular.stationarea.dto.StationAreaQueryRequest;
import cn.stylefeng.guns.modular.stationarea.service.StationAreaService;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 站场区域基础信息 Service 实现
 *
 *
 * @author system
 */
@Service
public class StationAreaServiceImpl extends ServiceImpl<TStationAreaBaseInfoMapper, TStationAreaBaseInfo> implements StationAreaService {

    @Resource
    private PipelineService pipelineService;

    @Resource
    private TStationBaseInfoService stationBaseInfoService;

    @Override
    public PageResult<TStationAreaBaseInfo> pageList(StationAreaQueryRequest request) {
        if (request == null) {
            request = new StationAreaQueryRequest();
        }

        Integer pageNo = request.getPageNo() != null ? request.getPageNo() : 1;
        Integer pageSize = request.getPageSize() != null ? request.getPageSize() : 10;

        LambdaQueryWrapper<TStationAreaBaseInfo> queryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(request.getAreaName())) {
            queryWrapper.like(TStationAreaBaseInfo::getAreaName, request.getAreaName().trim());
        }

        if (StringUtils.isNotBlank(request.getStationId())) {
            queryWrapper.eq(TStationAreaBaseInfo::getBelongStationId, request.getStationId().trim());
        }
        Page<TStationAreaBaseInfo> page = this.page(new Page<>(pageNo, pageSize), queryWrapper);

        List<TStationAreaBaseInfo> records = page.getRecords();
        if (records != null && !records.isEmpty()) {
            List<String> stationIds = records.stream()
                    .map(TStationAreaBaseInfo::getBelongStationId)
                    .distinct()
                    .collect(Collectors.toList());
            if (!stationIds.isEmpty()) {
                List<TStationBaseInfo> stations = stationBaseInfoService.listByIds(stationIds);
                Map<String, String> stationIdToName = stations.stream()
                        .collect(Collectors.toMap(TStationBaseInfo::getStationId, TStationBaseInfo::getStationName, (a, b) -> a));
                for (TStationAreaBaseInfo area : records) {
                    String stationId = area.getBelongStationId();
                    if (stationId != null) {
                        area.setBelongStationName(stationIdToName.get(stationId));
                    }
                }
            }
        }

        return PageToPageResultUtils.pageToPageResult(page);
    }

    @Override
    public boolean add(TStationAreaBaseInfo areaInfo) {
        if (areaInfo == null) {
            return false;
        }
        if (StringUtils.isBlank(areaInfo.getAreaId())) {
            areaInfo.setAreaId(IdWorker.getIdStr());
        }
        return this.save(areaInfo);
    }

    @Override
    public boolean update(TStationAreaBaseInfo areaInfo) {
        if (areaInfo == null || StringUtils.isBlank(areaInfo.getAreaId())) {
            return false;
        }
        return this.updateById(areaInfo);
    }

    @Override
    public boolean delete(String areaId) {
        if (StringUtils.isBlank(areaId)) {
            return false;
        }
        return this.removeById(areaId);
    }

    @Override
    public List<StationOption> stationOptions() {
        return pipelineService.getAllStationsForOption();
    }
}

