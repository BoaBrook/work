package cn.stylefeng.guns.modular.deviceRelation.entity;

import cn.stylefeng.guns.database.entity.TIndustrialTvPreset;
import lombok.Data;

import java.util.List;

/**
 * 工业电视列表 DTO
 */
@Data
public class IndustrialTvRelationDTO {

    /**
     * 工业电视设备ID
     */
    private String deviceId;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 品牌
     */
    private String brand;

    /**
     * 型号
     */
    private String model;

    /**
     * 摄像头IP
     */
    private String cameraIp;

    /**
     * 该工业电视下的预设位列表
     */
    private List<TIndustrialTvPreset> presetList;

    /**
     * 当前子系统 / 当前设备已经关联的预设位ID集合
     */
    private List<String> relatedPresetIds;
}

