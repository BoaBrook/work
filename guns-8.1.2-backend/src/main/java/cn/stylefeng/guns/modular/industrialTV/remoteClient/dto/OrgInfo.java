package cn.stylefeng.guns.modular.industrialTV.remoteClient.dto;

import lombok.Data;

import java.util.List;

/**
 * 区域信息
 */
@Data
public class OrgInfo {
    /**
     * 区域ID
     */
    private Integer id;

    /**
     * 区域名称
     */
    private String name;

    /**
     * 区域编号
     */
    private String orgNum;

    /**
     * 父级区域ID
     */
    private Integer parentId;

    /**
     * 父级区域名称
     */
    private String parentName;

    /**
     * 区域类型 1:公司 2:站点 3:普通区域
     */
    private Integer orgType;

    /**
     * 子节点列表
     */
    private List<OrgInfo> children;
}