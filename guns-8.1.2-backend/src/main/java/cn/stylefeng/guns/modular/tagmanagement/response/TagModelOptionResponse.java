package cn.stylefeng.guns.modular.tagmanagement.response;

import lombok.Data;

@Data
public class TagModelOptionResponse {

    /**
     * 模型ID
     */
    private String modelId;

    /**
     * 模型名称
     */
    private String modelName;

    /**
     * 模型地址
     */
    private String modelAddress;

    /**
     * 模型文件id
     */
    private Long modelFileId;

    /**
     * 位置
     */
    private String position;
}
