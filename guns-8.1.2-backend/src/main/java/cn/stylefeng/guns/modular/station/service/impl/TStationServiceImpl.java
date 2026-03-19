package cn.stylefeng.guns.modular.station.service.impl;

import cn.stylefeng.guns.database.entity.TPipelineBaseInfo;
import cn.stylefeng.guns.database.entity.TStationBaseInfo;
import cn.stylefeng.guns.database.service.TPipelineBaseInfoService;
import cn.stylefeng.guns.modular.station.dto.OperationAreaOptionResponse;
import cn.stylefeng.guns.modular.station.dto.StationListRequest;
import cn.stylefeng.guns.modular.station.dto.StationListResponse;
import cn.stylefeng.guns.modular.station.mapper.TStationMapper;
import cn.stylefeng.guns.modular.station.service.TStationService;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;
import cn.stylefeng.roses.kernel.sys.modular.org.entity.HrOrganization;
import cn.stylefeng.roses.kernel.sys.modular.org.service.HrOrganizationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 站点Service实现类
 *
 * @author system
 * @date 2026-01-21
 */
@Service
@Slf4j
public class TStationServiceImpl extends ServiceImpl<TStationMapper, TStationBaseInfo> implements TStationService {

    @Resource
    private HrOrganizationService sysHrOrganizationService;

    @Resource
    private TPipelineBaseInfoService pipelineBaseInfoService;

    @Override
    public List<TStationBaseInfo> getAllStations() {
        try {
            // 获取所有站点，无需分页
            List<TStationBaseInfo> stations = this.list();
            log.info("获取到站点数量: {}", stations.size());
            return stations;
        } catch (Exception e) {
            log.error("获取站点列表失败", e);
            return null;
        }
    }

    @Override
    public PageResult<StationListResponse> listByOrganizationRule(StationListRequest request) {
        StationListRequest safeRequest = request == null ? new StationListRequest() : request;
        int pageNo = safeRequest.getPageNo() == null ? 1 : safeRequest.getPageNo();
        int pageSize = safeRequest.getPageSize() == null ? 10 : safeRequest.getPageSize();

        LambdaQueryWrapper<HrOrganization> orgWrapper = new LambdaQueryWrapper<>();
        orgWrapper.eq(HrOrganization::getIsSecondaryOrg, 1);
        if (StringUtils.isNotBlank(safeRequest.getStationName())) {
            orgWrapper.like(HrOrganization::getOrgName, safeRequest.getStationName().trim());
        }
        if (StringUtils.isNotBlank(safeRequest.getBelongOperationArea())) {
            Long parentId = tryParseLong(safeRequest.getBelongOperationArea().trim());
            if (parentId != null) {
                orgWrapper.eq(HrOrganization::getOrgParentId, parentId);
            } else {
                return emptyPage(pageNo, pageSize);
            }
        }
        orgWrapper.orderByAsc(HrOrganization::getOrgId);
        List<HrOrganization> stationOrgs = sysHrOrganizationService.list(orgWrapper);
        if (stationOrgs == null || stationOrgs.isEmpty()) {
            return emptyPage(pageNo, pageSize);
        }

        Map<String, TStationBaseInfo> stationInfoMap = this.listByIds(
                stationOrgs.stream().map(org -> String.valueOf(org.getOrgId())).collect(Collectors.toList())
        ).stream().collect(Collectors.toMap(TStationBaseInfo::getStationId, v -> v, (a, b) -> a, LinkedHashMap::new));

        Set<Long> parentOrgIds = stationOrgs.stream()
                .map(HrOrganization::getOrgParentId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, String> parentOrgNameMap = parentOrgIds.isEmpty() ? Collections.emptyMap() :
                sysHrOrganizationService.listByIds(parentOrgIds).stream()
                        .collect(Collectors.toMap(HrOrganization::getOrgId, HrOrganization::getOrgName, (a, b) -> a));

        Set<String> pipelineIds = stationInfoMap.values().stream()
                .map(TStationBaseInfo::getBelongPipeline)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());
        Map<String, String> pipelineNameMap = pipelineIds.isEmpty() ? Collections.emptyMap() :
                pipelineBaseInfoService.listByIds(pipelineIds).stream()
                        .collect(Collectors.toMap(TPipelineBaseInfo::getPipelineId, TPipelineBaseInfo::getPipelineName, (a, b) -> a));

        List<StationListResponse> allRows = stationOrgs.stream()
                .map(org -> buildRow(org, stationInfoMap.get(String.valueOf(org.getOrgId())), parentOrgNameMap, pipelineNameMap))
                .filter(row -> StringUtils.isBlank(safeRequest.getBelongPipeline())
                        || StringUtils.equals(row.getBelongPipeline(), safeRequest.getBelongPipeline().trim()))
                .collect(Collectors.toList());

        int totalRows = allRows.size();
        int fromIndex = Math.max((pageNo - 1) * pageSize, 0);
        int toIndex = Math.min(fromIndex + pageSize, totalRows);
        List<StationListResponse> pageRows = fromIndex >= toIndex ? Collections.emptyList() : allRows.subList(fromIndex, toIndex);

        PageResult<StationListResponse> result = new PageResult<>();
        result.setPageNo(pageNo);
        result.setPageSize(pageSize);
        result.setTotalRows(totalRows);
        result.setTotalPage((int) Math.ceil(totalRows * 1.0 / pageSize));
        result.setRows(pageRows);
        return result;
    }

    @Override
    public boolean editStation(TStationBaseInfo station) {
        if (station == null || StringUtils.isBlank(station.getStationId())) {
            return false;
        }
        String stationId = station.getStationId().trim();
        Long orgId = tryParseLong(stationId);
        if (orgId == null) {
            return false;
        }

        HrOrganization stationOrg = sysHrOrganizationService.getById(orgId);
        if (stationOrg == null || !Integer.valueOf(1).equals(stationOrg.getIsSecondaryOrg())) {
            return false;
        }

        TStationBaseInfo toSave = new TStationBaseInfo();
        BeanUtils.copyProperties(station, toSave);
        toSave.setStationId(stationId);

        toSave.setStationName(stationOrg.getOrgName());
        toSave.setBelongOperationArea(stationOrg.getOrgParentId() == null ? null : String.valueOf(stationOrg.getOrgParentId()));
        toSave.setStationCode(stationOrg.getOrgCode());

        TStationBaseInfo existing = this.getById(stationId);
        return existing == null ? this.save(toSave) : this.updateById(toSave);
    }

    @Override
    public List<OperationAreaOptionResponse> operationAreaOptions() {
        LambdaQueryWrapper<HrOrganization> stationOrgWrapper = new LambdaQueryWrapper<>();
        stationOrgWrapper.eq(HrOrganization::getIsSecondaryOrg, 1);
        List<HrOrganization> stationOrgs = sysHrOrganizationService.list(stationOrgWrapper);
        if (stationOrgs == null || stationOrgs.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> parentOrgIds = stationOrgs.stream()
                .map(HrOrganization::getOrgParentId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (parentOrgIds.isEmpty()) {
            return Collections.emptyList();
        }

        return sysHrOrganizationService.listByIds(parentOrgIds).stream()
                .sorted((a, b) -> Long.compare(a.getOrgId(), b.getOrgId()))
                .map(org -> {
                    OperationAreaOptionResponse option = new OperationAreaOptionResponse();
                    option.setOperationAreaId(String.valueOf(org.getOrgId()));
                    option.setOperationAreaName(org.getOrgName());
                    return option;
                })
                .collect(Collectors.toList());
    }

    private StationListResponse buildRow(HrOrganization org, TStationBaseInfo station, Map<Long, String> parentOrgNameMap,
                                         Map<String, String> pipelineNameMap) {
        StationListResponse row = new StationListResponse();
        String stationId = String.valueOf(org.getOrgId());
        row.setStationId(stationId);
        row.setStationName(org.getOrgName());
        if (org.getOrgParentId() != null) {
            row.setBelongOperationArea(String.valueOf(org.getOrgParentId()));
            row.setBelongOperationAreaName(parentOrgNameMap.get(org.getOrgParentId()));
        }
        if (station != null) {
            row.setBelongPipeline(station.getBelongPipeline());
            row.setBelongPipelineName(pipelineNameMap.get(station.getBelongPipeline()));
            row.setStationCode(station.getStationCode());
            row.setStationLocation(station.getStationLocation());
            row.setRemark(station.getRemark());
        }
        return row;
    }

    private PageResult<StationListResponse> emptyPage(int pageNo, int pageSize) {
        PageResult<StationListResponse> result = new PageResult<>();
        result.setPageNo(pageNo);
        result.setPageSize(pageSize);
        result.setTotalRows(0);
        result.setTotalPage(0);
        result.setRows(Collections.emptyList());
        return result;
    }

    private Long tryParseLong(String value) {
        try {
            return Long.parseLong(value);
        } catch (Exception e) {
            return null;
        }
    }
}
