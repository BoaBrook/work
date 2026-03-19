package cn.stylefeng.guns.modular.valvechamber.service.impl;

import cn.stylefeng.guns.core.utils.PageToPageResultUtils;
import cn.stylefeng.guns.database.entity.TStationAreaBaseInfo;
import cn.stylefeng.guns.database.entity.TStationBaseInfo;
import cn.stylefeng.guns.database.entity.TValveChamberBaseInfo;
import cn.stylefeng.guns.database.mapper.TValveChamberBaseInfoMapper;
import cn.stylefeng.guns.database.service.TStationAreaBaseInfoService;
import cn.stylefeng.guns.database.service.TStationBaseInfoService;
import cn.stylefeng.guns.modular.valvechamber.dto.ValveChamberQueryRequest;
import cn.stylefeng.guns.modular.valvechamber.entity.ValveChamberListVO;
import cn.stylefeng.guns.modular.valvechamber.service.ValveChamberService;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 阀室管理 Service 实现
 */
@Service
public class ValveChamberServiceImpl extends ServiceImpl<TValveChamberBaseInfoMapper, TValveChamberBaseInfo> implements ValveChamberService {

    @Resource
    private TStationAreaBaseInfoService tStationAreaBaseInfoService;

    @Resource
    private TStationBaseInfoService tStationBaseInfoService;

    @Override
    public PageResult<ValveChamberListVO> pageList(ValveChamberQueryRequest request) {
        if (request == null) {
            request = new ValveChamberQueryRequest();
        }
        Integer pageNo = request.getPageNo() != null ? request.getPageNo() : 1;
        Integer pageSize = request.getPageSize() != null ? request.getPageSize() : 10;

        LambdaQueryWrapper<TValveChamberBaseInfo> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(request.getValveChamberName())) {
            wrapper.like(TValveChamberBaseInfo::getValveChamberName, request.getValveChamberName().trim());
        }

        if (StringUtils.isNotBlank(request.getAreaId())) {
            wrapper.eq(TValveChamberBaseInfo::getBelongStationAreaId, request.getAreaId().trim());
        }

        if (StringUtils.isNotBlank(request.getStationId())) {
            List<String> areaIds = tStationAreaBaseInfoService.lambdaQuery()
                    .eq(TStationAreaBaseInfo::getBelongStationId, request.getStationId().trim())
                    .list()
                    .stream()
                    .map(TStationAreaBaseInfo::getAreaId)
                    .collect(Collectors.toList());
            if (areaIds.isEmpty()) {
                PageResult<ValveChamberListVO> empty = new PageResult<>();
                empty.setPageNo(pageNo);
                empty.setPageSize(pageSize);
                empty.setTotalPage(0);
                empty.setTotalRows(0);
                empty.setRows(Collections.emptyList());
                return empty;
            }
            wrapper.in(TValveChamberBaseInfo::getBelongStationAreaId, areaIds);
        }

        Page<TValveChamberBaseInfo> page = this.page(new Page<>(pageNo, pageSize), wrapper);
        List<TValveChamberBaseInfo> records = page.getRecords();
        List<ValveChamberListVO> voList = new ArrayList<>();
        if (!records.isEmpty()) {
            Map<String, TStationAreaBaseInfo> areaMap = getStationAreaMap(
                    records.stream().map(TValveChamberBaseInfo::getBelongStationAreaId).filter(Objects::nonNull).distinct().collect(Collectors.toList()));
            List<String> stationIds = areaMap.values().stream()
                    .map(TStationAreaBaseInfo::getBelongStationId)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());
            Map<String, TStationBaseInfo> stationMap = stationIds.isEmpty() ? Collections.emptyMap() :
                    tStationBaseInfoService.listByIds(stationIds).stream()
                            .collect(Collectors.toMap(TStationBaseInfo::getStationId, s -> s, (a, b) -> a));

            for (TValveChamberBaseInfo r : records) {
                ValveChamberListVO vo = toListVO(r);
                TStationAreaBaseInfo area = r.getBelongStationAreaId() == null ? null : areaMap.get(r.getBelongStationAreaId());
                if (area != null) {
                    vo.setBelongStationAreaName(area.getAreaName());
                    vo.setBelongStationId(area.getBelongStationId());
                    TStationBaseInfo station = area.getBelongStationId() == null ? null : stationMap.get(area.getBelongStationId());
                    if (station != null) {
                        vo.setBelongStationName(station.getStationName());
                    }
                }
                voList.add(vo);
            }
        }

        Page<ValveChamberListVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        voPage.setRecords(voList);
        return PageToPageResultUtils.pageToPageResult(voPage);
    }

    private Map<String, TStationAreaBaseInfo> getStationAreaMap(List<String> areaIds) {
        if (areaIds == null || areaIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<TStationAreaBaseInfo> list = tStationAreaBaseInfoService.listByIds(areaIds);
        return list.stream().collect(Collectors.toMap(TStationAreaBaseInfo::getAreaId, a -> a, (a, b) -> a));
    }

    private ValveChamberListVO toListVO(TValveChamberBaseInfo r) {
        ValveChamberListVO vo = new ValveChamberListVO();
        vo.setValveChamberId(r.getValveChamberId());
        vo.setValveChamberName(r.getValveChamberName());
        vo.setBelongStationAreaId(r.getBelongStationAreaId());
        vo.setValveChamberCode(r.getValveChamberCode());
        vo.setRemark(r.getRemark());
        vo.setValveChamberLocation(r.getLocation());
        if (r.getCreateTime() != null) {
            vo.setCreateTime(toDate(r.getCreateTime()));
        }
        if (r.getUpdateTime() != null) {
            vo.setUpdateTime(toDate(r.getUpdateTime()));
        }
        return vo;
    }

    private static Date toDate(Object value) {
        if (value == null) return null;
        if (value instanceof Date) return (Date) value;
        if (value instanceof Long) return new Date((Long) value);
        return null;
    }

    @Override
    public boolean add(TValveChamberBaseInfo entity) {
        if (entity == null) return false;
        if (StringUtils.isBlank(entity.getValveChamberId())) {
            entity.setValveChamberId(IdWorker.getIdStr());
        }
        return this.save(entity);
    }
}
