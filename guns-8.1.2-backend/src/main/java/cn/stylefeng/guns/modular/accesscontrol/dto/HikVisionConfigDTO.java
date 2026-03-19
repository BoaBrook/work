package cn.stylefeng.guns.modular.accesscontrol.dto;

import lombok.Data;

@Data
public class HikVisionConfigDTO {
    private String ip;
    private short port;
    private String user;
    private String psw;
    private String belongStationId;
    private String deviceName;
}
