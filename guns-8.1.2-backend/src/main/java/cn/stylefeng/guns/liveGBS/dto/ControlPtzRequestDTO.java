package cn.stylefeng.guns.liveGBS.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ControlPtzRequestDTO extends BaseRequestDTO {
    private String command;
    private Integer speed = 129;
}
