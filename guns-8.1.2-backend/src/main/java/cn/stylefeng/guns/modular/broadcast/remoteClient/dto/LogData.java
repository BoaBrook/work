package cn.stylefeng.guns.modular.broadcast.remoteClient.dto;

import lombok.Data;

import java.util.List;

/**
 * 日志数据
 */
@Data
public class LogData {
    /**
     * 总条数
     */
    private Integer total;

    /**
     * 一页多少条
     */
    private Integer per_page;

    /**
     * 当前页
     */
    private Integer current_page;

    /**
     * 最后一页
     */
    private Integer last_page;

    /**
     * 日志记录
     */
    private List<LogRecord> data;

    @Data
    public static class LogRecord {
        /**
         * 日志ID
         */
        private Integer id;

        /**
         * 用户ID
         */
        private Integer users_id;

        /**
         * 用户组ID
         */
        private Integer groups_users_id;

        /**
         * 时间
         */
        private String time;

        /**
         * 日志描述
         */
        private String description;

        /**
         * 平台
         */
        private Platform platform;

        /**
         * 登录IP
         */
        private String ip_address;

        /**
         * 操作
         */
        private String action;

        /**
         * 用户信息
         */
        private UserInfo user;

        @Data
        public static class Platform {
            /**
             * 平台类型
             */
            private String platform;
        }

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
}