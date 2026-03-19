package cn.stylefeng.guns.modular.nodeSystem.entity;

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
@TableName("t_node_system_kafka_log")
public class NodeSystemKafkaLog extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 日志ID
     */
    @TableField("log_id")
    private String logId;
    /**
     * 消息ID
     */
    @TableField("msg_id")
    private String msgId;
    /**
     * 操作类型：PRODUCE-生产，CONSUME-消费
     */
    @TableField("operation_type")
    private String operationType;
    /**
     * 主题
     */
    @TableField("topic")
    private String topic;
    /**
     * 分区
     */
    @TableField("partition_id")
    private Integer partitionId;
    /**
     * 偏移量
     */
    @TableField("offset_idx")
    private Long offsetIdx;
    /**
     * 消息类型
     */
    @TableField("message_type")
    private String messageType;
    /**
     * 节点编码
     */
    @TableField("node_code")
    private String nodeCode;
    /**
     * 状态：1-成功，2-失败
     */
    @TableField("status")
    private Integer status;
    /**
     * 错误信息
     */
    @TableField("error_message")
    private String errorMessage;
    /**
     * 消息内容（JSON字符串）
     */
    @TableField("message_content")
    private String messageContent;
    /**
     * 处理时间（毫秒）
     */
    @TableField("process_time")
    private Long processTime;

}
