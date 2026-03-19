package cn.stylefeng.guns.modular.broadcast.remoteClient.dto;

import lombok.Data;

import java.util.List;

/**
 * 分组信息
 */
@Data
public class GroupInfo {
    /**
     * 分组ID
     */
    private Integer id;

    /**
     * 分组名
     */
    private String name;

    /**
     * 创建的用户ID
     */
    private Integer users_id;

    /**
     * 用户组ID
     */
    private Integer groups_users_id;

    /**
     * 分组呼叫编码
     */
    private Integer call_code;

    /**
     * 创建时间
     */
    private String create_time;

    /**
     * 最后更新时间
     */
    private String update_time;

    /**
     * 分组终端
     */
    private List<TerminalGroup> terminals;

    @Data
    public static class TerminalGroup {
        /**
         * 终端ID
         */
        private Integer terminals_id;

        /**
         * 创建的用户ID
         */
        private Integer users_id;

        /**
         * 用户组ID
         */
        private Integer groups_users_id;

        /**
         * 分组ID
         */
        private Integer terminals_groups_id;

        /**
         * 八分区配置
         */
        private Object amplifier;

        /**
         * 电源配置
         */
        private Object power;

        /**
         * 报警配置
         */
        private Object alarm;

        /**
         * 记录索引
         */
        private Integer id;

        /**
         * 终端名
         */
        private String name;

        /**
         * 终端类型
         */
        private Integer type;

        /**
         * IP地址
         */
        private String ip_address;

        /**
         * 终端呼叫编码
         */
        private Integer call_code;

        /**
         * 中继ID
         */
        private Integer relay_servers_id;

        /**
         * 其他配置
         */
        private String other_config;
    }
}