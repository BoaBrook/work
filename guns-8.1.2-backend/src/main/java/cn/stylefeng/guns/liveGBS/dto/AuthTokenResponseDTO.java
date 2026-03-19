package cn.stylefeng.guns.liveGBS.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AuthTokenResponseDTO {

    @JsonProperty("AuthToken")
    private String AuthToken;
    @JsonProperty("CookieToken")
    private String CookieToken;
    @JsonProperty("Token")
    private String Token;
    @JsonProperty("TokenTimeout")
    private Integer TokenTimeout;
    @JsonProperty("URLToken")
    private String URLToken;

}
