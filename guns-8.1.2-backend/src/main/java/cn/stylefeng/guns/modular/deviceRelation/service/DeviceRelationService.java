package cn.stylefeng.guns.modular.deviceRelation.service;

import cn.stylefeng.guns.modular.deviceRelation.entity.AccessControlRelationDTO;
import cn.stylefeng.guns.modular.deviceRelation.entity.EmergencyBroadcastRelationDTO;
import cn.stylefeng.guns.modular.deviceRelation.entity.CurrentAssociationsDTO;
import cn.stylefeng.guns.modular.deviceRelation.entity.DeviceRelationSaveDTO;
import cn.stylefeng.guns.modular.deviceRelation.entity.IndustrialTvRelationDTO;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;

import java.util.Map;

/**
 * 设备关联
 */
public interface DeviceRelationService {

    /**
     * 工业电视列表
     *
     */
    PageResult<IndustrialTvRelationDTO> listIndustrialTvForRelation(Map<String, Object> params);

    /**
     * 门禁设备列表
     */
    PageResult<AccessControlRelationDTO> listAccessControlForRelation(Map<String, Object> params);

    /**
     * 应急广播列表
     */
    PageResult<EmergencyBroadcastRelationDTO> listEmergencyBroadcastForRelation(Map<String, Object> params);

    /**
     * 保存关联关系
     */
    boolean saveRelations(DeviceRelationSaveDTO dto);

    /**
     * 获取当前设备已关联设备
     */
    CurrentAssociationsDTO getCurrentAssociations(String subsystemType, String relatedDeviceId);
}

