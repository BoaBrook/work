package cn.stylefeng.guns.modular.perimeterintrusion.remote.dto;

import lombok.Data;

import java.util.List;

@Data
public class PerimeterIntrusionResponse<T> {

    private Integer code;

    private String msg;

    private CommonData<T> data;

    @Data
    public static class CommonData<T> {

        private Integer total;

        private List<T> rows;
    }
}
