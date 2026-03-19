package cn.stylefeng.guns.modular.firegas.dto;

import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import cn.stylefeng.roses.kernel.rule.pojo.request.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 火气系统传感器设备基础信息查询请求参数
 *
 * @author system
 * @date 2026-01-14
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TFireGasSensorBaseInfoQueryRequest extends BaseRequest {

    /**
     * 设备编码（模糊查询）
     */
    @ChineseDescription("设备编码")
    private String deviceCode;

    /**
     * 设备名称（模糊查询）
     */
    @ChineseDescription("设备名称")
    private String deviceName;

    /**
     * 设备序号（模糊查询）
     */
    @ChineseDescription("设备序号")
    private String deviceSerialNumber;

    /**
     * 设备型号（模糊查询）
     */
    @ChineseDescription("设备型号")
    private String deviceModel;

    /**
     * 所属站场区域ID
     */
    @ChineseDescription("所属站场区域ID")
    private String belongStationAreaId;

    /**
     * 所在位置（模糊查询）
     */
    @ChineseDescription("所在位置")
    private String location;

    /**
     * 火气系统主机设备ID
     */
    @ChineseDescription("火气系统主机设备ID")
    private String fireGasHostId;

}
