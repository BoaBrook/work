package cn.stylefeng.guns.liveGBS;

import cn.stylefeng.guns.liveGBS.dto.*;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class LiveGBSService {

    private static final String LOGIN_URL = "/api/v1/login";
    private static final String CONTROL_PTZ_URL = "/api/v1/control/ptz";
    private static final String CONTROL_PRESET_URL = "/api/v1/control/preset";
    private static final String CHANNEL_INFO_URL = "/api/v1/device/channelinfo";

    private String authToken;
    private LocalDateTime tokenExpireTime;
    private final ScheduledExecutorService tokenRefreshExecutor = Executors.newSingleThreadScheduledExecutor();

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private LiveGBSConfig liveGBSConfig;

    @PostConstruct
    public void init() {
        login();
    }

    private void login() {
        try {
            String md5Password = md5Encrypt(liveGBSConfig.getPassword());
            AuthTokenRequestDTO requestDto = new AuthTokenRequestDTO();
            requestDto.setUsername(liveGBSConfig.getUsername());
            requestDto.setPassword(md5Password);
            AuthTokenResponseDTO authTokenResponse = restTemplate.postForObject(liveGBSConfig.getServer() + LOGIN_URL, requestDto, AuthTokenResponseDTO.class);
            if (authTokenResponse != null && authTokenResponse.getAuthToken() != null) {
                authToken = authTokenResponse.getAuthToken();
                Integer tokenTimeout = authTokenResponse.getTokenTimeout();
                if (tokenTimeout != null && tokenTimeout > 0) {
                    tokenExpireTime = LocalDateTime.now().plusSeconds(tokenTimeout);
                    scheduleTokenRefresh(tokenTimeout);
                    log.info("登录成功，Token过期时间: {}", tokenExpireTime);
                } else {
                    log.warn("Token过期时间为空或无效，使用默认刷新策略");
                    scheduleTokenRefresh(3600);
                }
            }
        } catch (Exception e) {
            log.error("登录失败", e);
        }
    }

    private void scheduleTokenRefresh(long tokenTimeoutSeconds) {
        long refreshDelay = Math.max(tokenTimeoutSeconds - 300, 60);
        tokenRefreshExecutor.schedule(() -> {
            log.info("开始定时刷新Token");
            login();
        }, refreshDelay, TimeUnit.SECONDS);
    }

    // 云台控制
    public Boolean controlPtz(ControlPtzRequestDTO requestDto) {
        try {
            HttpEntity<Object> requestEntity = getRequestEntity(requestDto);
            ResponseEntity<String> response = restTemplate.exchange(liveGBSConfig.getServer() + CONTROL_PTZ_URL + "?token=" + authToken, HttpMethod.POST, requestEntity, String.class);
            return StringUtils.isNotEmpty(response.getBody()) && response.getBody().equals("\"OK\"");
        } catch (Exception e) {
            log.error("控制云台失败", e);
            return false;
        }
    }

    // 预制点控制
    public Boolean controlPreset(ControlPresetRequestDTO requestDto) {
        try {
            HttpEntity<Object> requestEntity = getRequestEntity(requestDto);
            ResponseEntity<String> response = restTemplate.exchange(liveGBSConfig.getServer() + CONTROL_PRESET_URL + "?token=" + authToken, HttpMethod.POST, requestEntity, String.class);
            return StringUtils.isNotEmpty(response.getBody()) && response.getBody().equals("\"OK\"");
        } catch (Exception e) {
            log.error("预置点控制失败", e);
            return false;
        }
    }

    // 查询单挑通道信息
    public ChannelinfoResponseDTO channelinfo(BaseRequestDTO requestDto) {
        try {
//            addInterceptor();
            String url = liveGBSConfig.getServer() + CHANNEL_INFO_URL +
                    "?serial="+requestDto.getSerial()+"&code="+requestDto.getCode()+"&token="+authToken;
            return restTemplate.getForObject(url, ChannelinfoResponseDTO.class, requestDto);
        } catch (Exception e) {
            log.error("获取通道信息失败", e);
            return null;
        }
    }

    // 添加拦截器来设置请求头
//    public void addInterceptor() {
//        if(StringUtils.isBlank(authToken)){
//            login();
//        }
//        restTemplate.getInterceptors().add((request, body, execution) -> {
//            request.getHeaders().add("Authorization", authToken);
//            return execution.execute(request, body);
//        });
//    }

    /**
     * MD5加密方法
     * @param input 需要加密的字符串
     * @return 加密后的32位小写MD5值
     */
    public String md5Encrypt(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            log.error("MD5加密失败", e);
            return null;
        }
    }

    private HttpEntity<Object> getRequestEntity(Object requestBody){
//        addInterceptor();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "qGHrmCe_SoVcpazadx9EfD-IjzTJfeVdtv0BQ8-gedl.oxNzcwNzE3OTUxLCJwIjoiZjgwNWJiNzk3MzFhNzkwM2Q1ODBhMzlhNTcxNmRkOTc4M2IzOWE3ZDdiZDI5NDA2YjdhM2FhZmE3NTdlYmMxMCIsInQiOjE3NzAxMTMxNTEsInUiOiJhOTUwZWYyMjJkIn0eyJlIj.GciOiJIUzI1NiIsInR5cCI6IkpXVCJ9eyJhb");
        return new HttpEntity<>(requestBody, headers);
    }

}
