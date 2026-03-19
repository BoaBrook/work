package cn.stylefeng.guns.modular.nodeSystem.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 设备清单
 */
@Data
public class DeviceInventoryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 节点编码
     * 来源：cn.stylefeng.guns.modular.nodeSystem.service.NodeSystemService.getNodeCode()
     */
    private String nodeCode;

    /**
     * 管线编码
     * 来源：cn.stylefeng.guns.modular.nodeSystem.constants.dict.PipelineCodeEnum
     */
    private String pipelineCode;

    /**
     * 作业区编码
     * 来源：cn.stylefeng.guns.modular.nodeSystem.constants.dict.WorkAreaCodeEnum
     */
    private String workAreaCode;

    /**
     * 场站编码
     * 来源：cn.stylefeng.guns.modular.nodeSystem.constants.dict.StationCodeEnum
     */
    private String stationCode;

    /**
     * 设备区域编码
     */
    private String deviceAreaCode;

    /**
     * 设备区域名称
     */
    private String deviceAreaName;

    /**
     * 设备类型
     */
    private String deviceType;

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * NVR IP地址（工业电视必填）
     */
    private String nvrIp;

    /**
     * 通道号（工业电视必填）
     */
    private String portNumber;

    /**
     * 通道国标编码（工业电视必填）
     */
    private String channelGbId;

    /**
     * 工业电视类型（工业电视必填），如：枪机、球机
     */
    private String industrialTvType;

    /**
     * 应急广播类型 应急广播必传，1-话机，2-功放
     */
    private String broadcastType;
    /**
     * 经度
     */
    private Integer longitude;

    /**
     * 纬度
     */
    private Integer latitude;

    /**
     * 设备品牌
     */
    private String deviceBrand;

    /**
     * 设备型号
     */
    private String deviceModel;

    /**
     * 设备厂家（必须使用全称，不能使用简称）
     */
    private String deviceManufacturer;

    /**
     * 设备IP（周界防护除外必填）
     */
    private String deviceIp;

    /**
     * 设备端口号（周界防护除外必填）
     */
    private Integer devicePort;

    /**
     * 账号（工业电视必填）
     */
    private String account;

    /**
     * 密码（工业电视必填，base64编码）
     */
    private String password;

    /**
     * 联系人
     */
    private String contactPerson;

    /**
     * 联系方式
     */
    private String contactInfo;

    /**
     * 入库时间（格式：yyyy-MM-dd HH:mm:ss）
     */
    private String storageTime;

    /**
     * 操作标识：A-新增，U-修改，D-删除
     */
    private String operateType;

}
