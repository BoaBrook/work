package cn.stylefeng.guns.modular.hikvision.response;

import lombok.Data;

import java.util.Date;

/**
 * 录像文件信息响应
 */
@Data
public class RecordFileResponse {

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件大小 (字节)
     */
    private Long fileSize;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 通道号
     */
    private Integer channel;

    /**
     * 文件类型 (0-普通录像, 1-报警录像)
     */
    private Integer fileType;

}
