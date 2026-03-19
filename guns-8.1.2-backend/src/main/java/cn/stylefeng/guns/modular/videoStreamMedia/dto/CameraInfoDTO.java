package cn.stylefeng.guns.modular.videoStreamMedia.dto;

import lombok.Data;

import java.util.List;

/**
 * 摄像头详细信息DTO
 */
@Data
public class CameraInfoDTO {
    /**
     * 摄像头名称
     */
    private String cameraName;

    /**
     * 摄像头存储路径
     */
    private String cameraPath;

    /**
     * 可用的视频录制日期列表（yyyy-MM-dd 格式）
     */
    private List<String> availableDates;
}