package cn.stylefeng.guns.modular.report.entity;

import java.io.Serializable;
import java.util.Date;

import cn.stylefeng.roses.kernel.db.api.pojo.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_report_records")
public class ReportRecords extends BaseEntity {

    /**
     * 报备ID
     */
    @TableId(value = "report_id", type = IdType.ASSIGN_UUID)
    private String reportId;
    /**
     * 联系人
     */
    @TableField("contact_person")
    private String contactPerson;
    /**
     * 站场ID
     */
    @TableField("station_id")
    private String stationId;
    /**
     * 报备内容
     */
    @TableField("report_content")
    private String reportContent;
    /**
     * 预计整改开始时间
     */
    @TableField("expected_rectification_start_time")
    private Date expectedRectificationStartTime;
    /**
     * 预计整改结束时间
     */
    @TableField("expected_rectification_end_time")
    private Date expectedRectificationEndTime;
    /**
     * 状态 0-待上报、1-审核中、2-已通过、3-已驳回
     */
    @TableField("status")
    private Integer status;
    /**
     * 审核内容
     */
    @TableField("flow_content")
    private String flowContent;
    /**
     * 上报时间
     */
    @TableField("report_time")
    private Date reportTime;

    @TableField(exist = false)
    private ApprovalRecord approvalRecord;

    /**
     * 审批记录
     */
    @Data
    public static class ApprovalRecord implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * 审核人
         */
        private String checkerName;

        /**
         * 审核内容
         */
        private String rejectReason;

        /**
         * 审核时间（格式：yyyy-MM-dd HH:mm:ss）
         */
        private String reviewTime;

        /**
         * 确认状态：1-已确认，2-驳回
         */
        private Integer status;
    }

}
