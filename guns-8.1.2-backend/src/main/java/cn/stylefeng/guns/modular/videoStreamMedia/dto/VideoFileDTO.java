package cn.stylefeng.guns.modular.videoStreamMedia.dto;

import lombok.Data;

/**
 * 视频文件信息DTO
 */
@Data
public class VideoFileDTO {
    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件完整路径
     */
    private String filePath;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 最后修改时间（ISO 8601 格式）
     */
    private String lastModified;

    /**
     * 视频播放 URL
     */
    private String playUrl;
}