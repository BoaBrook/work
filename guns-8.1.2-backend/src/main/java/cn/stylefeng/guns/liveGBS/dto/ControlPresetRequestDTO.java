package cn.stylefeng.guns.liveGBS.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ControlPresetRequestDTO extends BaseRequestDTO {
    private String command;
    private Integer preset;
    private String name;
}
