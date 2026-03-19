package cn.stylefeng.guns.modular.datimsien.service;

import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.apache.http.HttpStatus;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;

import cn.stylefeng.guns.modular.datimsien.DatimsienConfig;
import cn.stylefeng.guns.modular.datimsien.dto.DatimsienRequestRt;
import cn.stylefeng.guns.modular.datimsien.dto.DatimsienResponseRt;
import cn.stylefeng.guns.modular.datimsien.dto.DatimsienTokenResponse;
import cn.stylefeng.roses.kernel.rule.pojo.response.ResponseData;
import lombok.extern.slf4j.Slf4j;

/**
 * Datimsien实时数据服务
 *
 * @author system
 */
@Slf4j
@Service
public class DatimsienRtService {

    @Autowired(required = false)
    private DatimsienConfig datimsienConfig;

    @Autowired(required = false)
    private RestTemplate restTemplate;

    /**
     * Token缓存
     */
    private DatimsienTokenResponse.Content token;

    /**
     * 获取实时数据（从远程服务）
     *
     * @param request 请求参数
     * @return 实时数据列表
     */
    public List<DatimsienResponseRt> getRtDataRemote(DatimsienRequestRt request) {
        try {
            if (datimsienConfig == null || datimsienConfig.getServiceBaseURL() == null) {
                log.warn("Datimsien配置未设置或serviceBaseURL为空");
                return new ArrayList<>();
            }

            if (restTemplate == null) {
                log.warn("RestTemplate未配置");
                return new ArrayList<>();
            }

            // 构建请求URL
            String url = datimsienConfig.getServiceBaseURL() + "/datimsien/rt";

            // 构建请求参数
            MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
            params.add("units", JSON.toJSONString(request.getUnits()));

            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            // 发送请求（带重试逻辑）
            HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(params, headers);
            return callWithRetry(url, httpEntity, new TypeReference<ResponseData<List<DatimsienResponseRt>>>() {}, false);
        } catch (Exception e) {
            log.error("获取实时数据失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 调用远程服务（带token和重试逻辑）
     *
     * @param url 请求URL
     * @param httpEntity 请求实体
     * @param tf 响应类型引用
     * @param retry 是否重试
     * @return 响应数据列表
     */
    private <T> List<T> callWithRetry(String url, HttpEntity<?> httpEntity, TypeReference<ResponseData<List<T>>> tf, boolean retry) {
        // 获取或刷新token
        if (token == null || retry) {
            token = getToken();
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.addAll(httpEntity.getHeaders());
            // 设置Bearer token
            headers.setBearerAuth(URLEncoder.encode(token.getAccess_token(), "UTF-8"));

            ResponseEntity<String> response = restTemplate.postForEntity(
                    url, 
                    new HttpEntity<>(httpEntity.getBody(), headers), 
                    String.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                // 如果是401未授权错误且未重试过，则刷新token并重试
                if (response.getStatusCode().is4xxClientError()
                        && response.getStatusCode().value() == HttpStatus.SC_UNAUTHORIZED
                        && !retry) {
                    return callWithRetry(url, httpEntity, tf, true);
                }
                log.error("调用远程服务失败, url: {}, error code: {}", url, response.getStatusCode());
                return new ArrayList<>();
            }

            // 解析响应（tf已定义为ResponseData<List<T>>，直接解析）
            ResponseData<List<T>> repData = JSON.parseObject(response.getBody(), tf);
            if (repData != null && "0".equals(repData.getCode())) {
                // 直接返回data列表（为空则返回空列表）
                return repData.getData() == null ? new ArrayList<>() : new ArrayList<>(repData.getData());
            } else if (repData != null) {
                log.error("调用远程服务失败, url: {}, error: {}", url, repData.getMessage());
                return new ArrayList<>();
            }

            return new ArrayList<>();
        } catch (Exception e) {
            // 如果是401未授权错误且未重试过，则刷新token并重试
            if (e instanceof HttpClientErrorException) {
                int status = ((HttpClientErrorException) e).getRawStatusCode();
                if (status == HttpStatus.SC_UNAUTHORIZED && !retry) {
                    try {
                        return callWithRetry(url, httpEntity, tf, true);
                    } catch (Exception reCallException) {
                        log.error("重复调用服务失败, url: {}, param: {}", url, httpEntity.getBody(), reCallException);
                        return new ArrayList<>();
                    }
                }
            }
            log.error("调用远程服务失败, url: {}, param: {}", url, httpEntity.getBody(), e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取Token
     *
     * @return Token内容
     */
    private DatimsienTokenResponse.Content getToken() {
        try {
            if (datimsienConfig == null || datimsienConfig.getServiceBaseURL() == null) {
                log.error("Datimsien配置未设置或serviceBaseURL为空");
                throw new RuntimeException("Datimsien配置未设置");
            }

            if (restTemplate == null) {
                log.error("RestTemplate未配置");
                throw new RuntimeException("RestTemplate未配置");
            }

            String url = datimsienConfig.getServiceBaseURL() + "/token";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            String password = getEncodedPassword();
            String requestBody = String.format("username=%s&password=%s", 
                    datimsienConfig.getUser(), password);
            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                log.error("获取Token失败, url: {}, error code: {}", url, response.getStatusCode());
                throw new RuntimeException("获取Token失败, error code: " + response.getStatusCode());
            }

            DatimsienTokenResponse repData = JSON.parseObject(response.getBody(), DatimsienTokenResponse.class);
            if ("ok".equals(repData.getResult())) {
                return repData.getContent();
            } else {
                log.error("获取Token失败, resp: {}", response.getBody());
                throw new RuntimeException("获取Token失败, resp: " + response.getBody());
            }
        } catch (Exception e) {
            log.error("获取Token异常", e);
            throw new RuntimeException("获取Token异常", e);
        }
    }

    /**
     * 获取加密后的密码（MD5）
     *
     * @return 加密后的密码
     */
    private String getEncodedPassword() {
        try {
            String password = datimsienConfig.getPassword();
            if (password == null) {
                return "";
            }
            return DigestUtils.md5DigestAsHex(password.getBytes());
        } catch (Exception e) {
            log.error("密码加密失败", e);
            return datimsienConfig.getPassword();
        }
    }
}
