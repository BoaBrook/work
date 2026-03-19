package cn.stylefeng.guns.modular.nodeSystem.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 指令下发
 */
@Data
public class CommandDownlinkDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 设备唯一编码
     * 预留字段
     */
    private String deviceCode;

    /**
     * 指令下发时间（格式：yyyy-MM-dd HH:mm:ss）
     */
    private String commandTime;

    /**
     * 操作类型
     * HZTJ-设备汇总统计
     * JPFK-作业计划反馈
     */
    private String operationType;

    /**
     * 参数（JSON字符串）
     */
    private String parameters;

    /**
     * 作业计划参数
     */
    @Data
    public static class JobParameters implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 作业计划ID
         * 与节点上报的作业计划数据中作业计划ID一致
         */
        private String jobId;

        /**
         * 确认状态：1-已确认，2-驳回
         */
        private Integer status;

        /**
         * 驳回原因
         * 确认状态为驳回时有值
         */
        private String rejectReason;

        /**
         * 审核人
         */
        private String checkerName;

    }

}
