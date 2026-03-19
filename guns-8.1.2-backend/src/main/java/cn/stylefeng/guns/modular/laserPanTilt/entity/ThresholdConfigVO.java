package cn.stylefeng.guns.modular.laserPanTilt.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ThresholdConfigVO implements Serializable {

    private String deviceId;
    private String highHighOperator;
    private String highHighValue;
    private String highOperator;
    private String highValueMin;
    private String highValueMax;
    private String lowOperator;
    private String lowValue;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
    private Long createUser;
    private Long updateUser;
}
