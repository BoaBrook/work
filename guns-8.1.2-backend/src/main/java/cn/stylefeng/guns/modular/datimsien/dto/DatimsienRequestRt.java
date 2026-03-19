package cn.stylefeng.guns.modular.datimsien.dto;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Datimsien实时数据请求
 *
 * @author system
 */
@Data
@NoArgsConstructor
public class DatimsienRequestRt {
    /**
     * 请求单元列表
     */
    private List<DatimsienRequestUnit> units;

    /**
     * 构造函数
     *
     * @param units 请求单元列表
     */
    public DatimsienRequestRt(List<DatimsienRequestUnit> units) {
        this.units = units;
    }
}
