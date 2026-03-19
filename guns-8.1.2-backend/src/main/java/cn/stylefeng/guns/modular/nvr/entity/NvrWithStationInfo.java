package cn.stylefeng.guns.modular.nvr.entity;

import cn.stylefeng.guns.database.entity.TNvrBaseInfo;
import lombok.Data;

/**
 * 包含站场信息的硬盘录像机设备
 *
 * @author system
 * @date 2026-01-30
 */
@Data
public class NvrWithStationInfo extends TNvrBaseInfo {

    /**
     * 所属作业区
     */
    private String belongOperationArea;

    /**
     * 所属管线
     */
    private String belongPipeline;

    /**
     * 所属站场名称
     */
    private String belongStationName;

    /**
     * 站场区域名称
     */
    private String stationAreaName;
}
