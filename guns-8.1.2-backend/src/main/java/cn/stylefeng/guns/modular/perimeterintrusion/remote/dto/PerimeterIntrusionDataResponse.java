package cn.stylefeng.guns.modular.perimeterintrusion.remote.dto;

import lombok.Data;

import java.util.List;

@Data
public class PerimeterIntrusionDataResponse<T> {

    private Integer code;

    private String msg;

    private List<T> data;
}
