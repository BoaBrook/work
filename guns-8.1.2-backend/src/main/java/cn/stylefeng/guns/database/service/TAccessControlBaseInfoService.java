package cn.stylefeng.guns.database.service;

import cn.stylefeng.guns.database.entity.TAccessControlBaseInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 门禁设备基础信息表 Service接口
 *
 * @author system
 * @date 2026-01-14
 */
public interface TAccessControlBaseInfoService extends IService<TAccessControlBaseInfo> {

    long countByBelongStationId(String belongStationId, String deviceCode, String ipAddress);

}