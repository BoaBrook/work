package cn.stylefeng.guns.modular.deviceRelation.entity;

import lombok.Data;

import java.util.List;

/**
 * 设备关联保存 DTO
 */
@Data
public class DeviceRelationSaveDTO {

    /**
     * 子系统类型
     */
    private String subsystemType;

    /**
     * 关联的设备ID
     */
    private String relatedDeviceId;

    /**
     * 工业电视预设位ID集合
     */
    private List<String> presetIds;

    /**
     * 门禁设备ID集合
     */
    private List<String> accessControlDeviceIds;

    /**
     * 应急广播设备ID集合
     */
    private List<String> emergencyBroadcastIds;
}

