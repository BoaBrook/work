package cn.stylefeng.guns.liveGBS.dto;

import lombok.Data;

@Data
public class AuthTokenRequestDTO {
    private String username;
    private String password;
    private Boolean url_token_only = true;
}
