package cn.stylefeng.guns.modular.nodeSystem.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class CommonMessage<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private String msgId;

    private String nodeCode;

    private String sign;

    private String appId;

    private Long reportTime;

    private String type;

    private T data;

}
