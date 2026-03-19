package cn.stylefeng.guns.database.service.impl;

import cn.stylefeng.guns.core.utils.PageToPageResultUtils;
import cn.stylefeng.guns.database.entity.TFireGasHostBaseInfo;
import cn.stylefeng.guns.database.entity.TStationBaseInfo;
import cn.stylefeng.guns.database.mapper.TFireGasHostBaseInfoMapper;
import cn.stylefeng.guns.database.service.TFireGasHostBaseInfoService;
import cn.stylefeng.guns.database.service.TStationBaseInfoService;
import cn.stylefeng.guns.modular.firegas.dto.TFireGasHostBaseInfoQueryRequest;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;
import cn.stylefeng.roses.kernel.rule.exception.base.ServiceException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 火气系统主机设备基础信息表 Service实现
 *
 * @author system
 * @date 2026-01-14
 */
@Service
public class TFireGasHostBaseInfoServiceImpl extends ServiceImpl<TFireGasHostBaseInfoMapper, TFireGasHostBaseInfo> implements TFireGasHostBaseInfoService {

    @Autowired
    private TStationBaseInfoService stationBaseInfoService;

    @Override
    public PageResult<TFireGasHostBaseInfo> pageList(TFireGasHostBaseInfoQueryRequest request) {
        if (request == null) {
            request = new TFireGasHostBaseInfoQueryRequest();
        }

        Integer pageNo = request.getPageNo() != null ? request.getPageNo() : 1;
        Integer pageSize = request.getPageSize() != null ? request.getPageSize() : 10;

        // 根据所属作业区ID、所属管线ID、所属站场ID查询站场列表，获取站场ID列表
        List<String> finalStationIdList = null;
        if (StringUtils.isNotBlank(request.getBelongStationId()) 
                || StringUtils.isNotBlank(request.getBelongOperationAreaId()) 
                || StringUtils.isNotBlank(request.getBelongPipelineId())) {
            
            LambdaQueryWrapper<TStationBaseInfo> stationWrapper = new LambdaQueryWrapper<>();
            String belongStationId = request.getBelongStationId();
            if (StringUtils.isNotBlank(belongStationId)) {
                stationWrapper.eq(TStationBaseInfo::getStationId, belongStationId.trim());
            }
            String belongOperationAreaId = request.getBelongOperationAreaId();
            if (StringUtils.isNotBlank(belongOperationAreaId)) {
                stationWrapper.eq(TStationBaseInfo::getBelongOperationArea, belongOperationAreaId.trim());
            }
            String belongPipelineId = request.getBelongPipelineId();
            if (StringUtils.isNotBlank(belongPipelineId)) {
                stationWrapper.eq(TStationBaseInfo::getBelongPipeline, belongPipelineId.trim());
            }
            
            List<TStationBaseInfo> stations = stationBaseInfoService.list(stationWrapper);
            
            if (CollectionUtils.isEmpty(stations)) {
                // 如果没有匹配的站场，返回空结果
                Page<TFireGasHostBaseInfo> emptyPage = new Page<>(pageNo, pageSize);
                return PageToPageResultUtils.pageToPageResult(emptyPage);
            }
            
            finalStationIdList = stations.stream()
                    .map(TStationBaseInfo::getStationId)
                    .collect(Collectors.toList());
        }

        LambdaQueryWrapper<TFireGasHostBaseInfo> queryWrapper = new LambdaQueryWrapper<>();

        String deviceCode = request.getDeviceCode();
        if (StringUtils.isNotBlank(deviceCode)) {
            queryWrapper.like(TFireGasHostBaseInfo::getDeviceCode, deviceCode.trim());
        }

        String deviceName = request.getDeviceName();
        if (StringUtils.isNotBlank(deviceName)) {
            queryWrapper.like(TFireGasHostBaseInfo::getDeviceName, deviceName.trim());
        }

        // 如果有站场ID列表，添加到查询条件
        if (CollectionUtils.isNotEmpty(finalStationIdList)) {
            queryWrapper.in(TFireGasHostBaseInfo::getBelongStationId, finalStationIdList);
        }

        // 按创建时间倒序
        queryWrapper.orderByDesc(TFireGasHostBaseInfo::getCreateTime);

        Page<TFireGasHostBaseInfo> page = this.page(new Page<>(pageNo, pageSize), queryWrapper);

        // 填充所属站场、所属管线、区域名称
        fillStationAndPipelineAndAreaName(page.getRecords());

        return PageToPageResultUtils.pageToPageResult(page);
    }

    @Override
    public boolean add(TFireGasHostBaseInfo hostInfo) {
        if (hostInfo == null) {
            return false;
        }
        if (StringUtils.isNotBlank(hostInfo.getDeviceCode())) {
            long count = this.lambdaQuery()
                    .eq(StringUtils.isNotBlank(hostInfo.getBelongStationId()), TFireGasHostBaseInfo::getBelongStationId, hostInfo.getBelongStationId().trim())
                    .eq(TFireGasHostBaseInfo::getDeviceCode, hostInfo.getDeviceCode().trim())
                    .count();
            if (count > 0) {
                throw new ServiceException("火气系统主机设备", "DEVICE_CODE_DUPLICATE", "设备编号已存在");
            }
        }

        // 站场ID + IP唯一性校验（同一个站场下IP不能重复）
        if (StringUtils.isNotBlank(hostInfo.getBelongStationId()) && StringUtils.isNotBlank(hostInfo.getIpAddress())) {
            long count = this.lambdaQuery()
                    .eq(TFireGasHostBaseInfo::getBelongStationId, hostInfo.getBelongStationId().trim())
                    .eq(TFireGasHostBaseInfo::getIpAddress, hostInfo.getIpAddress().trim())
                    .count();
            if (count > 0) {
                throw new ServiceException("火气系统主机设备", "IP_DUPLICATE", "同一站场下IP地址已存在");
            }
        }
        if (StringUtils.isBlank(hostInfo.getDeviceId())) {
            hostInfo.setDeviceId(IdWorker.getIdStr());
        }
        return this.save(hostInfo);
    }

    @Override
    public boolean update(TFireGasHostBaseInfo hostInfo) {
        if (hostInfo == null || StringUtils.isBlank(hostInfo.getDeviceId())) {
            return false;
        }
        if (StringUtils.isNotBlank(hostInfo.getDeviceCode())) {
            long count = this.lambdaQuery()
                    .eq(StringUtils.isNotBlank(hostInfo.getBelongStationId()), TFireGasHostBaseInfo::getBelongStationId, hostInfo.getBelongStationId().trim())
                    .eq(TFireGasHostBaseInfo::getDeviceCode, hostInfo.getDeviceCode().trim())
                    .ne(TFireGasHostBaseInfo::getDeviceId, hostInfo.getDeviceId())
                    .count();
            if (count > 0) {
                throw new ServiceException("火气系统主机设备", "DEVICE_CODE_DUPLICATE", "设备编号已存在");
            }
        }

        // 站场ID + IP唯一性校验（同一个站场下IP不能重复）
        if (StringUtils.isNotBlank(hostInfo.getBelongStationId()) && StringUtils.isNotBlank(hostInfo.getIpAddress())) {
            long count = this.lambdaQuery()
                    .eq(TFireGasHostBaseInfo::getBelongStationId, hostInfo.getBelongStationId().trim())
                    .eq(TFireGasHostBaseInfo::getIpAddress, hostInfo.getIpAddress().trim())
                    .ne(TFireGasHostBaseInfo::getDeviceId, hostInfo.getDeviceId())
                    .count();
            if (count > 0) {
                throw new ServiceException("火气系统主机设备", "IP_DUPLICATE", "同一站场下IP地址已存在");
            }
        }
        return this.updateById(hostInfo);
    }

    @Override
    public boolean delete(String deviceId) {
        if (StringUtils.isBlank(deviceId)) {
            return false;
        }
        return this.removeById(deviceId);
    }

    /**
     * 批量填充所属站场、所属管线、区域名称
     *
     * @param records 主机列表
     */
    private void fillStationAndPipelineAndAreaName(List<TFireGasHostBaseInfo> records) {
        if (CollectionUtils.isEmpty(records)) {
            return;
        }

        // 收集站场ID和区域ID
        Set<String> stationIds = records.stream()
                .map(TFireGasHostBaseInfo::getBelongStationId)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());
        Set<String> areaIds = records.stream()
                .map(TFireGasHostBaseInfo::getBelongStationAreaId)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());

        if (CollectionUtils.isEmpty(stationIds) && CollectionUtils.isEmpty(areaIds)) {
            return;
        }

        // 查询站场信息
        Map<String, TStationBaseInfo> stationMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(stationIds)) {
            List<TStationBaseInfo> stationList = stationBaseInfoService.listByIds(stationIds);
            if (CollectionUtils.isNotEmpty(stationList)) {
                for (TStationBaseInfo station : stationList) {
                    if (station != null) {
                        stationMap.put(station.getStationId(), station);
                    }
                }
            }
        }

        // 填充名称
        for (TFireGasHostBaseInfo host : records) {
            if (host == null) {
                continue;
            }
            // 填充站场名称、作业区名称和管线名称
            if (StringUtils.isNotBlank(host.getBelongStationId())) {
                TStationBaseInfo station = stationMap.get(host.getBelongStationId());
                if (station != null) {
                    host.setStationName(station.getStationName());
                    host.setAreaName(stationBaseInfoService.getBelongOperationAreaName(station));
                    host.setPipelineName(stationBaseInfoService.getBelongPipelineName(station));
                }
            }
        }
    }

}