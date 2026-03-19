package cn.stylefeng.guns.modular.perimeterintrusion.remote.dto;

import lombok.Data;

@Data
public class PerimeterIntrusionZone {
    /**
     * 防区ID
     */
    private String id;
    /**
     * 防区类型,0-安防防区,1-消防防区
     */
    private String clazz;
    /**
     * 防区编号
     */
    private String code;

    /**
     * 防区状态,0-布放,1-撤防
     */
    private String defenceState;
    /**
     * 防区描述
     */
    private String description;
    /**
     * 防区名称
     */
    private String name;
    /**
     * 防区完整名称
     */
    private String fullName;
    /**
     * 防区所在区域完整名称
     */
    private String fullOfficeName;
}
