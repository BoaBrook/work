package cn.stylefeng.guns.modular.broadcast.remoteClient.dto;

import lombok.Data;

/**
 * 媒体信息
 */
@Data
public class MediaInfo {
    /**
     * 媒体库ID
     */
    private Integer medias_groups_id;

    /**
     * 用户ID
     */
    private Integer users_id;

    /**
     * 相对路径
     */
    private String relative_path;

    /**
     * 媒体名
     */
    private String name;

    /**
     * 字节大小
     */
    private Integer size;

    /**
     * MD5
     */
    private String md5_hash;

    /**
     * 秒数
     */
    private String length;

    /**
     * 创建时间
     */
    private String create_time;

    /**
     * 更新时间
     */
    private String update_time;

    /**
     * 媒体ID
     */
    private Integer id;

    /**
     * 上传的用户信息
     */
    private UserInfo user;

    @Data
    public static class UserInfo {
        private Integer id;
        private Integer groups_users_id;
        private String name;
        private String avatar;
        private String email;
        private Integer priority;
        private String theme;
        private Integer type;
        private Integer is_deleted;
        private String register_date;
    }
}