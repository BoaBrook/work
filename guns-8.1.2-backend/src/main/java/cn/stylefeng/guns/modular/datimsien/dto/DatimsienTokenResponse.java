package cn.stylefeng.guns.modular.datimsien.dto;

import lombok.Data;

/**
 * Datimsien Token响应
 *
 * @author system
 */
@Data
public class DatimsienTokenResponse {
    private String result;
    private Content content;

    @Data
    public static class Content {
        private String token_type;
        private String access_token;
    }
}
