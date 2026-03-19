package cn.stylefeng.guns.modular.industrialTV.remoteClient.dto;

import lombok.Data;

import java.util.List;

/**
 * 告警消息分页数据
 */
@Data
public class AlarmMessagePage {
    /**
     * 当前页
     */
    private Integer pageNum;

    /**
     * 每页数量
     */
    private Integer pageSize;

    /**
     * 总记录数
     */
    private Integer totalCount;

    /**
     * 总页数
     */
    private Integer totalPage;

    /**
     * 告警记录列表
     */
    private List<AlarmMessage> items;

    /**
     * 系统时间
     */
    private Long systemTime;

    /**
     * 服务器文件地址
     */
    private String serverFileAddress;
}