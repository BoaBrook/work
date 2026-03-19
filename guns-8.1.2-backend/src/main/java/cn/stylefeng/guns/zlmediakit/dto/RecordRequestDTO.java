package cn.stylefeng.guns.zlmediakit.dto;

import lombok.Data;

@Data
public class RecordRequestDTO {

    private String secret;
    private String vhost;
    private String app;
    private String stream;
    private Integer type = 1;
    private String customized_path;
    private Integer max_second = 10;

}
