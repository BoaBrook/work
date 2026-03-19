package cn.stylefeng.guns.modular.safeday.entity;

import lombok.Data;

/**
 * 安全运行天数保存参数
 *
 * @author system
 * @date 2026-01-20
 */
@Data
public class SafeDaysParam {

    /**
     * 站点ID
     */
    private String stationId;

    /**
     * 安全运行开始日期，格式 yyyy-MM-dd
     */
    private String initialDate;

    /**
     * 定义
     */
    private String definition;
}
