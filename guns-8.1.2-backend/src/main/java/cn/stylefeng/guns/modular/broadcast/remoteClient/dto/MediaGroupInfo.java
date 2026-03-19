package cn.stylefeng.guns.modular.broadcast.remoteClient.dto;

import lombok.Data;

/**
 * 媒体组信息
 */
@Data
public class MediaGroupInfo {
    /**
     * 媒体库名称
     */
    private String name;

    /**
     * 是否公开（0-私有, 1-公开）
     */
    private Integer is_public;

    /**
     * 用户ID
     */
    private Integer users_id;

    /**
     * 创建时间
     */
    private String create_time;

    /**
     * 更新时间
     */
    private String update_time;

    /**
     * 媒体库ID
     */
    private Integer id;
}