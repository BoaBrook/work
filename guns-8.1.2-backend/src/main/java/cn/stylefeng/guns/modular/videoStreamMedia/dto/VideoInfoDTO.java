package cn.stylefeng.guns.modular.videoStreamMedia.dto;

import lombok.Data;

/**
 * 视频文件详细信息DTO
 */
@Data
public class VideoInfoDTO {
    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 最后修改时间（ISO 8601 格式，UTC 时间）
     */
    private String lastModified;

    /**
     * 视频播放 URL
     */
    private String playUrl;

    /**
     * MIME 类型
     */
    private String contentType;

    /**
     * 文件头部信息（十六进制，用于诊断）
     */
    private String fileHeader;
}