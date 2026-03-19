package cn.stylefeng.guns.modular.broadcast.remoteClient.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 终端信息数组
 */
@Data
public class TerminalInfoArray {
    /**
     * 终端数组
     */
    @JsonProperty("EndPointsArray")
    private List<TerminalInfo> EndPointsArray;
}