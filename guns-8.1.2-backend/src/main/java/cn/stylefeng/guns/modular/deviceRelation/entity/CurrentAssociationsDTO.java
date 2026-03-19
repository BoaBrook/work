package cn.stylefeng.guns.modular.deviceRelation.entity;

import lombok.Data;

import java.util.List;

/**
 * 当前设备已关联设备
 */
@Data
public class CurrentAssociationsDTO {

    /**
     * 已关联的工业电视预设位ID列表
     */
    private List<String> presetIds;

    /**
     * 已关联的门禁设备ID列表
     */
    private List<String> accessControlDeviceIds;

    /**
     * 已关联的应急广播设备ID列表
     */
    private List<String> emergencyBroadcastIds;
}
