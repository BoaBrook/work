package cn.stylefeng.guns.modular.datimsien.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Datimsien实时数据响应
 *
 * @author system
 */
@Data
@NoArgsConstructor
public class DatimsienResponseRt {
    /**
     * 时间戳数组
     */
    private Long[] times;

    /**
     * 采集单元ID
     */
    private String unitId;

    /**
     * 值数组
     */
    private Object[] values;

    /**
     * 类型数组
     */
    private Integer[] types;

    /**
     * 标签类型数组
     */
    @JSONField(name = "tag_types")
    private Integer[] tagTypes;

    /**
     * 有效期数组（毫秒）
     */
    private Long[] expiredTimes = new Long[]{0L};
}
