package cn.stylefeng.guns.modular.datimsien.websocketClient.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

@Data
public class DatimsienWebSocketRecord {

    private Update update;

    @Data
    public static class Update {
        private String unitId;
        private Long time;
        private String[] tags;
        private Integer[] types;
        private Object[] values;
        @JSONField(name = "tag_types")
        private Integer[] tagTypes;
    }
}
