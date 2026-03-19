package cn.stylefeng.guns.modular.broadcast.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OnlineStatisticsResponse {

    private Integer totalNum;

    private Integer onlineNum;

}
