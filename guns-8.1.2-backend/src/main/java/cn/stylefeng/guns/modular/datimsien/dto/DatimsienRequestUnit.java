package cn.stylefeng.guns.modular.datimsien.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Datimsien请求单元
 *
 * @author system
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatimsienRequestUnit {
    /**
     * 采集单元ID
     */
    private String unitId;

    /**
     * 标签列表
     */
    private List<String> tags = new ArrayList<>();

    /**
     * 添加标签
     *
     * @param tag 标签
     */
    public void addTag(String tag) {
        tags.add(tag);
    }
}
