package cn.stylefeng.guns.database.service.impl;

import cn.stylefeng.guns.core.utils.PageToPageResultUtils;
import cn.stylefeng.guns.database.entity.TFireGasSensorBaseInfo;
import cn.stylefeng.guns.database.mapper.TFireGasSensorBaseInfoMapper;
import cn.stylefeng.guns.database.service.TFireGasSensorBaseInfoService;
import cn.stylefeng.guns.modular.firegas.dto.TFireGasSensorBaseInfoQueryRequest;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;
import cn.stylefeng.roses.kernel.rule.exception.base.ServiceException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 火气系统传感器设备基础信息表 Service实现
 *
 * @author system
 * @date 2026-01-14
 */
@Service
public class TFireGasSensorBaseInfoServiceImpl extends ServiceImpl<TFireGasSensorBaseInfoMapper, TFireGasSensorBaseInfo> implements TFireGasSensorBaseInfoService {

    @Override
    public PageResult<TFireGasSensorBaseInfo> pageList(TFireGasSensorBaseInfoQueryRequest request) {
        if (request == null) {
            request = new TFireGasSensorBaseInfoQueryRequest();
        }

        Integer pageNo = request.getPageNo() != null ? request.getPageNo() : 1;
        Integer pageSize = request.getPageSize() != null ? request.getPageSize() : 10;

        LambdaQueryWrapper<TFireGasSensorBaseInfo> queryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(request.getDeviceCode())) {
            queryWrapper.like(TFireGasSensorBaseInfo::getDeviceCode, request.getDeviceCode().trim());
        }

        if (StringUtils.isNotBlank(request.getDeviceName())) {
            queryWrapper.like(TFireGasSensorBaseInfo::getDeviceName, request.getDeviceName().trim());
        }

        if (StringUtils.isNotBlank(request.getDeviceSerialNumber())) {
            queryWrapper.like(TFireGasSensorBaseInfo::getDeviceSerialNumber, request.getDeviceSerialNumber().trim());
        }

        if (StringUtils.isNotBlank(request.getDeviceModel())) {
            queryWrapper.like(TFireGasSensorBaseInfo::getDeviceModel, request.getDeviceModel().trim());
        }

        if (StringUtils.isNotBlank(request.getBelongStationAreaId())) {
            queryWrapper.eq(TFireGasSensorBaseInfo::getBelongStationAreaId, request.getBelongStationAreaId().trim());
        }

        if (StringUtils.isNotBlank(request.getLocation())) {
            queryWrapper.like(TFireGasSensorBaseInfo::getLocation, request.getLocation().trim());
        }

        if (StringUtils.isNotBlank(request.getFireGasHostId())) {
            queryWrapper.eq(TFireGasSensorBaseInfo::getFireGasHostId, request.getFireGasHostId().trim());
        }

        // 按创建时间倒序
        queryWrapper.orderByDesc(TFireGasSensorBaseInfo::getCreateTime);

        Page<TFireGasSensorBaseInfo> page = this.page(new Page<>(pageNo, pageSize), queryWrapper);
        return PageToPageResultUtils.pageToPageResult(page);
    }

    @Override
    public boolean add(TFireGasSensorBaseInfo sensorInfo) {
        if (sensorInfo == null) {
            return false;
        }
        if (StringUtils.isNotBlank(sensorInfo.getDeviceCode())) {
            long count = this.lambdaQuery()
                    .eq(TFireGasSensorBaseInfo::getDeviceCode, sensorInfo.getDeviceCode().trim())
                    .count();
            if (count > 0) {
                throw new ServiceException("火气系统传感器设备", "DEVICE_CODE_DUPLICATE", "设备编号已存在");
            }
        }
        if (StringUtils.isBlank(sensorInfo.getDeviceId())) {
            sensorInfo.setDeviceId(IdWorker.getIdStr());
        }
        return this.save(sensorInfo);
    }

    @Override
    public boolean update(TFireGasSensorBaseInfo sensorInfo) {
        if (sensorInfo == null || StringUtils.isBlank(sensorInfo.getDeviceId())) {
            return false;
        }
        if (StringUtils.isNotBlank(sensorInfo.getDeviceCode())) {
            long count = this.lambdaQuery()
                    .eq(TFireGasSensorBaseInfo::getDeviceCode, sensorInfo.getDeviceCode().trim())
                    .ne(TFireGasSensorBaseInfo::getDeviceId, sensorInfo.getDeviceId())
                    .count();
            if (count > 0) {
                throw new ServiceException("火气系统传感器设备", "DEVICE_CODE_DUPLICATE", "设备编号已存在");
            }
        }
        return this.updateById(sensorInfo);
    }

    @Override
    public boolean batchDelete(List<String> deviceIds) {
        if (CollectionUtils.isEmpty(deviceIds)) {
            return false;
        }
        return this.removeByIds(deviceIds);
    }

}