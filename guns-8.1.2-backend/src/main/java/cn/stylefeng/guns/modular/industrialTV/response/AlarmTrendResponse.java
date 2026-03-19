package cn.stylefeng.guns.modular.industrialTV.response;

import lombok.Data;

@Data
public class AlarmTrendResponse {

    /**
     * 已处置
     */
    private Integer disposed;

    /**
     * 未处置
     */
    private Integer undisposed;

}
