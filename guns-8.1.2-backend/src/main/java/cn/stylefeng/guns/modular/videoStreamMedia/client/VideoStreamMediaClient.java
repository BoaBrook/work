package cn.stylefeng.guns.modular.videoStreamMedia.client;

import cn.stylefeng.guns.modular.videoStreamMedia.config.VideoStreamMediaConfig;
import cn.stylefeng.guns.modular.videoStreamMedia.dto.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 视频流媒体服务客户端
 * 提供完整的视频流媒体服务接口调用功能
 */
@Slf4j
public class VideoStreamMediaClient {

    /**
     * -- SETTER --
     *  设置配置
     */
    @Setter
    private VideoStreamMediaConfig config;

    /**
     * -- SETTER --
     *  设置 RestTemplate
     */
    @Setter
    private RestTemplate restTemplate;

    /**
     * -- SETTER --
     *  设置 ObjectMapper
     */
    @Setter
    private ObjectMapper objectMapper;

    /**
     * 默认构造函数
     */
    public VideoStreamMediaClient() {
    }

    /**
     * 构造函数，接受配置和依赖
     */
    public VideoStreamMediaClient(VideoStreamMediaConfig config, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.config = config;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * 初始化客户端
     */
    public void initialize(VideoStreamMediaConfig config, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.config = config;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * ============================================
     * 一、摄像头管理接口
     * ============================================
     */

    /**
     * 获取所有摄像头列表
     * @return 摄像头名称列表
     */
    public VideoStreamMediaResponse<List<String>> getCameras() {
        try {
            String url = config.getApiUrl() + "/cameras";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            VideoStreamMediaResponse<List<String>> result = objectMapper.readValue(response.getBody(),
                    new TypeReference<VideoStreamMediaResponse<List<String>>>() {});

            return result;
        } catch (Exception e) {
            log.error("获取摄像头列表失败", e);
            throw new RuntimeException("获取摄像头列表失败", e);
        }
    }

    /**
     * 获取摄像头详细信息
     * @return 摄像头详细信息列表
     */
    public VideoStreamMediaResponse<List<CameraInfoDTO>> getCamerasInfo() {
        try {
            String url = config.getApiUrl() + "/cameras/info";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            VideoStreamMediaResponse<List<CameraInfoDTO>> result = objectMapper.readValue(response.getBody(),
                    new TypeReference<VideoStreamMediaResponse<List<CameraInfoDTO>>>() {});

            return result;
        } catch (Exception e) {
            log.error("获取摄像头详细信息失败", e);
            throw new RuntimeException("获取摄像头详细信息失败", e);
        }
    }

    /**
     * 获取指定摄像头的可用日期
     * @param cameraName 摄像头名称
     * @return 日期列表
     */
    public VideoStreamMediaResponse<List<String>> getCameraDates(String cameraName) {
        try {
            String encodedCameraName = URLEncoder.encode(cameraName, StandardCharsets.UTF_8.name());
            String url = config.getApiUrl() + "/cameras/" + encodedCameraName + "/dates";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            VideoStreamMediaResponse<List<String>> result = objectMapper.readValue(response.getBody(),
                    new TypeReference<VideoStreamMediaResponse<List<String>>>() {});

            return result;
        } catch (Exception e) {
            log.error("获取摄像头可用日期失败, cameraName: {}", cameraName, e);
            throw new RuntimeException("获取摄像头可用日期失败", e);
        }
    }

    /**
     * ============================================
     * 二、视频文件管理接口
     * ============================================
     */

    /**
     * 获取指定摄像头和日期的视频列表（使用 Query 参数）
     * @param cameraName 摄像头名称
     * @param date 日期（yyyy-MM-dd 格式）
     * @return 视频列表
     */
    public VideoStreamMediaResponse<VideoListDTO> getVideosByQuery(String cameraName, String date) {
        try {
            String encodedCameraName = URLEncoder.encode(cameraName, StandardCharsets.UTF_8.name());
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(config.getApiUrl() + "/cameras/" + encodedCameraName + "/videos")
                    .queryParam("date", date);
            String url = builder.toUriString();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            VideoStreamMediaResponse<VideoListDTO> result = objectMapper.readValue(response.getBody(),
                    new TypeReference<VideoStreamMediaResponse<VideoListDTO>>() {});

            return result;
        } catch (Exception e) {
            log.error("获取视频列表失败, cameraName: {}, date: {}", cameraName, date, e);
            throw new RuntimeException("获取视频列表失败", e);
        }
    }

    /**
     * 获取指定摄像头和日期的视频列表（使用路径参数）
     * @param cameraName 摄像头名称
     * @param date 日期（yyyy-MM-dd 格式）
     * @return 视频列表
     */
    public VideoStreamMediaResponse<VideoListDTO> getVideosByPath(String cameraName, String date) {
        try {
            String encodedCameraName = URLEncoder.encode(cameraName, StandardCharsets.UTF_8.name());
            String url = config.getApiUrl() + "/cameras/" + encodedCameraName + "/videos/" + date;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            VideoStreamMediaResponse<VideoListDTO> result = objectMapper.readValue(response.getBody(),
                    new TypeReference<VideoStreamMediaResponse<VideoListDTO>>() {});

            return result;
        } catch (Exception e) {
            log.error("获取视频列表失败, cameraName: {}, date: {}", cameraName, date, e);
            throw new RuntimeException("获取视频列表失败", e);
        }
    }

    /**
     * 获取指定视频文件的详细信息
     * @param cameraName 摄像头名称
     * @param date 日期（yyyy-MM-dd 格式）
     * @param fileName 视频文件名
     * @return 视频文件详细信息
     */
    public VideoInfoDTO getVideoInfo(String cameraName, String date, String fileName) {
        try {
            String encodedCameraName = URLEncoder.encode(cameraName, StandardCharsets.UTF_8.name());
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.name());
            String url = config.getApiUrl() + "/video/info/" + encodedCameraName + "/" + date + "/" + encodedFileName;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<VideoInfoDTO> response = restTemplate.exchange(url, HttpMethod.GET, entity, VideoInfoDTO.class);

            return response.getBody();
        } catch (Exception e) {
            log.error("获取视频文件信息失败, cameraName: {}, date: {}, fileName: {}", cameraName, date, fileName, e);
            throw new RuntimeException("获取视频文件信息失败", e);
        }
    }

    /**
     * ============================================
     * 三、视频播放接口
     * ============================================
     */

    /**
     * 获取视频播放URL
     * @param cameraName 摄像头名称
     * @param date 日期（yyyy-MM-dd 格式）
     * @param fileName 视频文件名
     * @return 视频播放URL
     */
    public String getVideoPlayUrl(String cameraName, String date, String fileName) {
        try {
            String encodedCameraName = URLEncoder.encode(cameraName, StandardCharsets.UTF_8.name());
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.name());
            return config.getApiUrl() + "/video/play/" + encodedCameraName + "/" + date + "/" + encodedFileName;
        } catch (UnsupportedEncodingException e) {
            log.error("编码URL失败", e);
            throw new RuntimeException("编码URL失败", e);
        }
    }

    /**
     * 获取视频播放URL（完整路径）
     * @param cameraName 摄像头名称
     * @param date 日期（yyyy-MM-dd 格式）
     * @param fileName 视频文件名
     * @return 视频播放URL（包含完整域名）
     */
    public String getFullVideoPlayUrl(String cameraName, String date, String fileName) {
        try {
            String encodedCameraName = URLEncoder.encode(cameraName, StandardCharsets.UTF_8.name());
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.name());
            return config.getBaseUrl() + "/api/video/play/" + encodedCameraName + "/" + date + "/" + encodedFileName;
        } catch (UnsupportedEncodingException e) {
            log.error("编码URL失败", e);
            throw new RuntimeException("编码URL失败", e);
        }
    }

    /**
     * ============================================
     * 四、便捷方法
     * ============================================
     */

    /**
     * 获取所有摄像头名称（便捷方法）
     */
    public List<String> getAllCameraNames() {
        VideoStreamMediaResponse<List<String>> response = getCameras();
        return response != null && response.isSuccess() ? response.getData() : null;
    }

    /**
     * 获取所有摄像头详细信息（便捷方法）
     */
    public List<CameraInfoDTO> getAllCameraInfo() {
        VideoStreamMediaResponse<List<CameraInfoDTO>> response = getCamerasInfo();
        return response != null && response.isSuccess() ? response.getData() : null;
    }

    /**
     * 获取指定摄像头的日期列表（便捷方法）
     */
    public List<String> getCameraAvailableDates(String cameraName) {
        VideoStreamMediaResponse<List<String>> response = getCameraDates(cameraName);
        return response != null && response.isSuccess() ? response.getData() : null;
    }

    /**
     * 获取指定摄像头和日期的视频文件列表（便捷方法）
     */
    public List<VideoFileDTO> getVideoFiles(String cameraName, String date) {
        VideoStreamMediaResponse<VideoListDTO> response = getVideosByQuery(cameraName, date);
        return response != null && response.isSuccess() && response.getData() != null ? response.getData().getVideos() : null;
    }
}