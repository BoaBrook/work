package cn.stylefeng.guns.modular.nvr.service.impl;

import cn.stylefeng.guns.core.utils.PageToPageResultUtils;
import cn.stylefeng.guns.database.entity.TNvrBaseInfo;
import cn.stylefeng.guns.database.entity.TStationAreaBaseInfo;
import cn.stylefeng.guns.database.entity.TStationBaseInfo;
import cn.stylefeng.guns.database.service.TNvrBaseInfoService;
import cn.stylefeng.guns.database.service.TStationAreaBaseInfoService;
import cn.stylefeng.guns.database.service.TStationBaseInfoService;
import cn.stylefeng.guns.modular.nvr.entity.NvrWithStationInfo;
import cn.stylefeng.guns.modular.nvr.service.NvrManagementService;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 硬盘录像机设备管理Service实现
 *
 * @author system
 * @date 2026-01-30
 */
@Service
public class NvrManagementServiceImpl implements NvrManagementService {

    @Resource
    private TNvrBaseInfoService nvrBaseInfoService;

    @Resource
    private TStationBaseInfoService stationBaseInfoService;

    @Resource
    private TStationAreaBaseInfoService stationAreaBaseInfoService;

    @Override
    public PageResult<NvrWithStationInfo> list(Map<String, Object> params) {
        // 获取分页参数
        int pageNo = getIntParam(params, "pageNo", 1);
        int pageSize = getIntParam(params, "pageSize", 10);

        // 构建查询条件
        LambdaQueryWrapper<TNvrBaseInfo> queryWrapper = new LambdaQueryWrapper<>();
        
        // 处理作业区和管线的关联查询
        handleOperationAreaAndPipelineQuery(queryWrapper, params);
        
        // 构建其他查询条件
        buildQueryCondition(queryWrapper, params);

        // 执行分页查询
        Page<TNvrBaseInfo> pageResult = nvrBaseInfoService.page(new Page<>(pageNo, pageSize), queryWrapper);

        // 转换为包含站场信息的结果
        Page<NvrWithStationInfo> resultPage = new Page<>(pageNo, pageSize);
        resultPage.setTotal(pageResult.getTotal());
        resultPage.setRecords(
            pageResult.getRecords().stream()
                .map(this::convertToWithStationInfo)
                .collect(Collectors.toList())
        );

        return PageToPageResultUtils.pageToPageResult(resultPage);
    }
    
    /**
     * 处理作业区和管线的关联查询
     */
    private void handleOperationAreaAndPipelineQuery(LambdaQueryWrapper<TNvrBaseInfo> queryWrapper, Map<String, Object> params) {
        // 检查是否有作业区或管线参数
        boolean hasOperationArea = params.containsKey("belongOperationArea") && params.get("belongOperationArea") != null;
        boolean hasPipeline = params.containsKey("belongPipeline") && params.get("belongPipeline") != null;
        
        if (!hasOperationArea && !hasPipeline) {
            return;
        }
        
        // 构建站场查询条件
        LambdaQueryWrapper<TStationBaseInfo> stationQueryWrapper = new LambdaQueryWrapper<>();
        
        if (hasOperationArea) {
            Object operationArea = params.get("belongOperationArea");
            if (operationArea instanceof String && ((String) operationArea).isEmpty()) {
                // 如果作业区为空字符串，查询为 null 的记录
                stationQueryWrapper.isNull(TStationBaseInfo::getBelongOperationArea);
            } else {
                // 否则查询等于指定值的记录
                stationQueryWrapper.eq(TStationBaseInfo::getBelongOperationArea, operationArea);
            }
        }
        
        if (hasPipeline) {
            Object pipeline = params.get("belongPipeline");
            if (pipeline instanceof String && ((String) pipeline).isEmpty()) {
                // 如果管线为空字符串，查询为 null 的记录
                stationQueryWrapper.isNull(TStationBaseInfo::getBelongPipeline);
            } else {
                // 否则查询等于指定值的记录
                stationQueryWrapper.eq(TStationBaseInfo::getBelongPipeline, pipeline);
            }
        }
        
        // 查询符合条件的站场
        List<TStationBaseInfo> stations = stationBaseInfoService.list(stationQueryWrapper);
        
        if (stations.isEmpty()) {
            // 如果没有符合条件的站场，添加一个永远为 false 的条件，返回空结果
            queryWrapper.eq(TNvrBaseInfo::getDeviceId, "");
            return;
        }
        
        // 提取站场 ID 列表
        List<String> stationIds = stations.stream()
            .map(TStationBaseInfo::getStationId)
            .collect(Collectors.toList());
        
        // 添加站场 ID 条件
        queryWrapper.in(TNvrBaseInfo::getBelongStationId, stationIds);
    }

    /**
     * 转换为包含站场信息的DTO
     */
    private NvrWithStationInfo convertToWithStationInfo(TNvrBaseInfo nvr) {
        NvrWithStationInfo result = new NvrWithStationInfo();
        // 复制基础属性
        BeanUtils.copyProperties(nvr, result);

        // 查询站场信息
        if (nvr.getBelongStationId() != null) {
            TStationBaseInfo station = stationBaseInfoService.getById(nvr.getBelongStationId());
            if (station != null) {
                result.setBelongStationName(station.getStationName());
                result.setBelongOperationArea(station.getBelongOperationArea());
                result.setBelongPipeline(station.getBelongPipeline());
            }
        }

        if (nvr.getBelongStationAreaId() != null) {
            TStationAreaBaseInfo area = stationAreaBaseInfoService.getById(nvr.getBelongStationAreaId());
            if (area != null) {
                result.setStationAreaName(area.getAreaName());
            }
        }

        return result;
    }

    /**
     * 从Map中获取整数参数
     */
    private int getIntParam(Map<String, Object> params, String key, int defaultValue) {
        Object value = params.get(key);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 构建查询条件
     */
    private void buildQueryCondition(LambdaQueryWrapper<TNvrBaseInfo> queryWrapper, Map<String, Object> params) {
        // 设备名称
        if (params.containsKey("deviceName")) {
            queryWrapper.like(TNvrBaseInfo::getDeviceName, params.get("deviceName"));
        }
        // 设备编码
        if (params.containsKey("deviceCode")) {
            queryWrapper.like(TNvrBaseInfo::getDeviceCode, params.get("deviceCode"));
        }
        // 所属站场
        if (params.containsKey("belongStationId")) {
            queryWrapper.eq(TNvrBaseInfo::getBelongStationId, params.get("belongStationId"));
        }
    }

    @Override
    public NvrWithStationInfo getId(String deviceId) {
        TNvrBaseInfo nvr = nvrBaseInfoService.getById(deviceId);
        return convertToWithStationInfo(nvr);
    }

    @Override
    public boolean add(TNvrBaseInfo nvrBaseInfo) {
        // 检查设备编码是否唯一
        LambdaQueryWrapper<TNvrBaseInfo> checkWrapper = new LambdaQueryWrapper<>();
        checkWrapper.eq(TNvrBaseInfo::getDeviceCode, nvrBaseInfo.getDeviceCode());
        if (nvrBaseInfoService.count(checkWrapper) > 0) {
            throw new RuntimeException("设备编码已存在，不可重复");
        }
        return nvrBaseInfoService.save(nvrBaseInfo);
    }

    @Override
    public boolean update(TNvrBaseInfo nvrBaseInfo) {
        if (nvrBaseInfo.getDeviceId() == null) {
            throw new RuntimeException("设备ID不能为空");
        }
        
        // 检查设备是否存在
        TNvrBaseInfo existingNvr = nvrBaseInfoService.getById(nvrBaseInfo.getDeviceId());
        if (existingNvr == null) {
            throw new RuntimeException("设备不存在");
        }
        
        // 检查设备编码是否发生变化
        String existingDeviceCode = existingNvr.getDeviceCode();
        String newDeviceCode = nvrBaseInfo.getDeviceCode();
        
        if (newDeviceCode != null && !newDeviceCode.equals(existingDeviceCode)) {
            // 设备编码发生变化，检查唯一性
            LambdaQueryWrapper<TNvrBaseInfo> checkWrapper = new LambdaQueryWrapper<>();
            checkWrapper.eq(TNvrBaseInfo::getDeviceCode, newDeviceCode);
            checkWrapper.ne(TNvrBaseInfo::getDeviceId, nvrBaseInfo.getDeviceId());
            if (nvrBaseInfoService.count(checkWrapper) > 0) {
                throw new RuntimeException("设备编码已存在，不可重复");
            }
        }
        
        return nvrBaseInfoService.updateById(nvrBaseInfo);
    }

    @Override
    public boolean delete(String deviceId) {
        return nvrBaseInfoService.removeById(deviceId);
    }
}
