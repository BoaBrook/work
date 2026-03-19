package cn.stylefeng.guns.modular.industrialTV.remoteClient.client;

import cn.stylefeng.guns.modular.industrialTV.remoteClient.config.SmartSecurityConfig;
import cn.stylefeng.guns.modular.industrialTV.remoteClient.dto.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 视频智能分析系统客户端
 * 提供完整的视频智能分析系统接口调用功能
 */
@Slf4j
public class SmartSecurityClient {

    /**
     * -- SETTER --
     *  设置配置
     */
    @Setter
    private SmartSecurityConfig config;

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
     * 当前有效的Token
     */
    private String currentToken;

    /**
     * Token过期时间戳（毫秒）
     */
    private long tokenExpireTime;

    /**
     * 默认构造函数
     */
    public SmartSecurityClient() {
    }

    /**
     * 构造函数，接受配置和依赖
     */
    public SmartSecurityClient(SmartSecurityConfig config, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.config = config;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * 初始化客户端
     */
    public void initialize(SmartSecurityConfig config, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.config = config;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * ============================================
     * 一、基础数据接口
     * ============================================
     */

    /**
     * 登录接口 - 获取接口访问token
     */
    public AuthResponse login() {
        try {
            String url = config.getBaseUrl() + "/butler/sso/api/login";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> body = new HashMap<>();
            body.put("account", config.getUsername());
            // 密码需使用base64加密传输
            String encodedPassword = Base64Utils.encodeToString(config.getPassword().getBytes(StandardCharsets.UTF_8));
            body.put("password", encodedPassword);

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            AuthResponse authResponse = objectMapper.readValue(response.getBody(), AuthResponse.class);

            if (authResponse != null && authResponse.getCode() == 0) {
                this.currentToken = authResponse.getData().getToken();
                // Token有效期100分钟，设置90分钟后过期（提前刷新）
                this.tokenExpireTime = System.currentTimeMillis() + 90 * 60 * 1000;
                log.info("获取视频智能分析系统Token成功");
            }

            return authResponse;
        } catch (Exception e) {
            log.error("获取视频智能分析系统Token失败", e);
            throw new RuntimeException("获取视频智能分析系统Token失败", e);
        }
    }

    /**
     * 获取有效的Token（自动刷新）
     */
    private String getValidToken() {
        if (currentToken == null || System.currentTimeMillis() >= tokenExpireTime) {
            login();
        }
        return currentToken;
    }

    /**
     * 查询区域列表
     */
    public SmartSecurityResponse<List<OrgInfo>> getOrgTree() {
        try {
            String url = config.getBaseUrl() + "/butler/manage/org/get_org_tree";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Token", getValidToken());
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            SmartSecurityResponse<List<OrgInfo>> result = objectMapper.readValue(response.getBody(),
                    new TypeReference<SmartSecurityResponse<List<OrgInfo>>>() {});

            return result;
        } catch (Exception e) {
            log.error("查询区域列表失败", e);
            throw new RuntimeException("查询区域列表失败", e);
        }
    }

    /**
     * 根据区域ID查询摄像头列表
     */
    public SmartSecurityResponse<List<CameraInfo>> getCamerasByOrgId(Integer orgId) {
        try {
            String url = config.getBaseUrl() + "/smartsecurity/v1/camera/getCamByOrgId/" + orgId;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Token", getValidToken());
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            SmartSecurityResponse<List<CameraInfo>> result = objectMapper.readValue(response.getBody(),
                    new TypeReference<SmartSecurityResponse<List<CameraInfo>>>() {});

            return result;
        } catch (Exception e) {
            log.error("查询摄像头列表失败", e);
            throw new RuntimeException("查询摄像头列表失败", e);
        }
    }

    /**
     * 获取多个摄像头状态
     */
    public SmartSecurityResponse<List<CameraLineStatus>> getCamerasLineStatus(List<String> streamChannelSerials) {
        try {
            String url = config.getBaseUrl() + "/smartsecurity/v1/camera/getCamerasLineStatus";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<List<String>> entity = new HttpEntity<>(streamChannelSerials, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            SmartSecurityResponse<List<CameraLineStatus>> result = objectMapper.readValue(response.getBody(),
                    new TypeReference<SmartSecurityResponse<List<CameraLineStatus>>>() {});

            return result;
        } catch (Exception e) {
            log.error("获取摄像头状态失败", e);
            throw new RuntimeException("获取摄像头状态失败", e);
        }
    }

    /**
     * 根据摄像头流媒体国标通道ID获取视频流
     * @param cameraNo 流媒体国标通道ID（摄像头编号）
     * @param protocolType 协议类型 1-rtsp；2-rtmp；3-hls；4-http-flv；5-wss；6-阿启视私协
     * @param plugId 流媒体播放器插件id（阿启视私协必传）
     */
    public SmartSecurityResponse<VideoStreamInfo> findLiveStreamInfo(String cameraNo, String protocolType, String plugId) {
        try {
            String url = config.getBaseUrl() + "/smartsecurity/v1/camera/liveStream/findLiveStreamInfo";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> body = new HashMap<>();
            body.put("cameraNo", cameraNo);
            body.put("protocolType", protocolType);
            if (plugId != null) {
                body.put("plugId", plugId);
            }

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            SmartSecurityResponse<VideoStreamInfo> result = objectMapper.readValue(response.getBody(),
                    new TypeReference<SmartSecurityResponse<VideoStreamInfo>>() {});

            return result;
        } catch (Exception e) {
            log.error("获取视频流失败", e);
            throw new RuntimeException("获取视频流失败", e);
        }
    }

    /**
     * ============================================
     * 二、告警订阅接口
     * ============================================
     */

    /**
     * 通用告警查询接口
     * @param pageNum 第几页
     * @param pageSize 每页数量
     * @param msgType 事件类型,多个事件类型逗号隔开
     * @param orgId 区域ID（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 截止时间（可选）
     * @param dealResult 处理状态（可选）ignore:忽略 deal:已处理 unDeal:未处理 being:处理中
     * @param msgLevel 事件等级（可选）1、2、3
     */
    public SmartSecurityResponse<AlarmMessagePage> getMessageHistorys(
            Integer pageNum, Integer pageSize, String msgType,
            Integer orgId, String startTime, String endTime,
            String dealResult, Integer msgLevel) {
        try {
            String url = config.getBaseUrl() + "/smartsecurity/v1/messageSever/getMessageHistorys";

            HttpHeaders headers = new HttpHeaders();
            headers.set("token", getValidToken());
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = new HashMap<>();
            body.put("pageNum", pageNum);
            body.put("pageSize", pageSize);
            body.put("msgType", msgType);
            body.put("sourceType", "computeAI");
            if (orgId != null) {
                body.put("orgId", orgId);
            }
            if (startTime != null) {
                body.put("startTime", startTime);
            }
            if (endTime != null) {
                body.put("endTime", endTime);
            }
            if (dealResult != null) {
                body.put("dealResult", dealResult);
            }
            if (msgLevel != null) {
                body.put("msgLvel", msgLevel);
            }

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            SmartSecurityResponse<AlarmMessagePage> result = objectMapper.readValue(response.getBody(),
                    new TypeReference<SmartSecurityResponse<AlarmMessagePage>>() {});

            return result;
        } catch (Exception e) {
            log.error("查询告警消息失败", e);
            throw new RuntimeException("查询告警消息失败", e);
        }
    }

    /**
     * ============================================
     * 三、便捷方法
     * ============================================
     */

    /**
     * 获取所有区域（便捷方法）
     */
    public List<OrgInfo> getAllOrgs() {
        SmartSecurityResponse<List<OrgInfo>> response = getOrgTree();
        return response != null && response.isSuccess() ? response.getData() : null;
    }

    /**
     * 根据区域ID获取摄像头列表（便捷方法）
     */
    public List<CameraInfo> getCamerasByOrgIdList(Integer orgId) {
        SmartSecurityResponse<List<CameraInfo>> response = getCamerasByOrgId(orgId);
        return response != null && response.isSuccess() ? response.getData() : null;
    }

    /**
     * 获取告警消息列表（便捷方法）
     */
    public List<AlarmMessage> getAlarmMessages(Integer pageNum, Integer pageSize, String msgType) {
        SmartSecurityResponse<AlarmMessagePage> response = getMessageHistorys(pageNum, pageSize, msgType, null, null, null, null, null);
        return response != null && response.isSuccess() && response.getData() != null ? response.getData().getItems() : null;
    }
}