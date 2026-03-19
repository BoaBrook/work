package cn.stylefeng.guns.modular.firegas.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 火气系统传感器在线状态统计响应
 *
 * @author system
 */
@Data
public class FireGasSensorOnlineStatusResponse implements Serializable {

    /**
     * 所有传感器数量
     */
    private Integer totalCount;

    /**
     * 在线传感器数量
     */
    private Integer onlineCount;

}
