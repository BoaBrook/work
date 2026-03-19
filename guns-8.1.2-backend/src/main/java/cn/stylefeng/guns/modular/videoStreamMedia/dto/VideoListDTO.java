package cn.stylefeng.guns.modular.videoStreamMedia.dto;

import lombok.Data;

import java.util.List;

/**
 * 视频列表响应DTO
 */
@Data
public class VideoListDTO {
    /**
     * 摄像头名称
     */
    private String cameraName;

    /**
     * 日期
     */
    private String date;

    /**
     * 视频文件列表
     */
    private List<VideoFileDTO> videos;
}