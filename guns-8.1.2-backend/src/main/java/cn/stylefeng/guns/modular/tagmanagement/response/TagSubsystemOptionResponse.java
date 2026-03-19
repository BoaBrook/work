package cn.stylefeng.guns.modular.tagmanagement.response;

import lombok.Data;

@Data
public class TagSubsystemOptionResponse {

    /**
     * 子系统类型编码（SystemTypeEnum.code）
     */
    private String subsystemType;

    /**
     * 子系统类型名称（SystemTypeEnum.description）
     */
    private String subsystemTypeName;
}
