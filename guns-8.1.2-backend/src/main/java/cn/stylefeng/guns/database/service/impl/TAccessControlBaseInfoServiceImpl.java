package cn.stylefeng.guns.database.service.impl;

import cn.stylefeng.guns.database.entity.TAccessControlBaseInfo;
import cn.stylefeng.guns.database.mapper.TAccessControlBaseInfoMapper;
import cn.stylefeng.guns.database.service.TAccessControlBaseInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 门禁设备基础信息表 Service实现
 *
 * @author system
 * @date 2026-01-14
 */
@Service
public class TAccessControlBaseInfoServiceImpl extends ServiceImpl<TAccessControlBaseInfoMapper, TAccessControlBaseInfo> implements TAccessControlBaseInfoService {

    @Resource
    private TAccessControlBaseInfoMapper tAccessControlBaseInfoMapper;
    @Override
    public long countByBelongStationId(String belongStationId, String deviceCode, String ipAddress) {
        return tAccessControlBaseInfoMapper.countByBelongStationId(belongStationId, deviceCode, ipAddress);
    }
}