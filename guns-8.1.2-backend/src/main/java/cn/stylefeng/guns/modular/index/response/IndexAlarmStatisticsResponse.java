package cn.stylefeng.guns.modular.index.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IndexAlarmStatisticsResponse {

    /**
     * 累计未处置
     */
    private Integer unDisposedNum;

    /**
     * 累计已处置
     */
    private Integer disposedNum;
}
