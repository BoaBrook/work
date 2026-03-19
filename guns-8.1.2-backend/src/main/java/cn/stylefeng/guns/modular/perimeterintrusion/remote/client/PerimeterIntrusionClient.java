package cn.stylefeng.guns.modular.perimeterintrusion.remote.client;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ObjectUtil;
import cn.stylefeng.guns.core.consts.ProjectConstants;
import cn.stylefeng.guns.core.utils.StringUtils;
import cn.stylefeng.guns.database.entity.TPerimeterIntrusionHostBaseInfo;
import cn.stylefeng.guns.database.service.TPerimeterIntrusionHostBaseInfoService;
import cn.stylefeng.guns.database.service.TPerimeterIntrusionZoneBaseInfoService;
import cn.stylefeng.guns.modular.perimeterintrusion.remote.config.PerimeterInstrusionConfigs;
import cn.stylefeng.guns.modular.perimeterintrusion.remote.dto.*;
import cn.stylefeng.roses.kernel.rule.exception.base.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class PerimeterIntrusionClient {

    private static final String API_KEY = "apikey";
    private static final String ARM_ZONE_URL = "/api/DefenceArea/ChangeDefenceState";
    private static final String ZONE_LIST_URL = "/api/DefenceArea/GetWithStatusPageList?pageNumber=1&pageSize=1000";
    private static final String HOST_STATE_LIST_URL = "/api/device/getdevicestate?deviceType=D.A";

    @Autowired
    private PerimeterInstrusionConfigs configs;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private TPerimeterIntrusionZoneBaseInfoService perimeterIntrusionZoneBaseInfoService;
    @Autowired
    private TPerimeterIntrusionHostBaseInfoService perimeterIntrusionHostBaseInfoService;

    public String getBaseUrl(String host) {
        for (PerimeterInstrusionConfigs.PerimeterInstrusionConfig config : configs.getConfigs()) {
            if(config.getHost().equals(host)){
                return String.format("http://%s:%d", config.getHost(), config.getPort());
            }
        }
        return null;
    }

    public List<PerimeterIntrusionHostState> getHostStateList() {
        HttpHeaders httpHeader = getHttpHeader();
        HttpEntity<PerimeterIntrusionHostState> entity = new HttpEntity<>(httpHeader);
        List<TPerimeterIntrusionHostBaseInfo> list = perimeterIntrusionHostBaseInfoService.list();
        if(ObjectUtil.isEmpty(list)){
            return null;
        }
        List<PerimeterIntrusionHostState> result = new ArrayList<>();
        for (TPerimeterIntrusionHostBaseInfo dto : list) {
            ResponseEntity<PerimeterIntrusionDataResponse<PerimeterIntrusionHostState>> response = restTemplate.exchange(getBaseUrl(dto.getIpAddress()) + HOST_STATE_LIST_URL, HttpMethod.GET, entity,
                    new ParameterizedTypeReference<PerimeterIntrusionDataResponse<PerimeterIntrusionHostState>>(){});
            log.info("查询厂方周界设备状态返回：{}",response);
            if (response.getBody() == null) {
                throw new ServiceException(ProjectConstants.PROJECT_MODULE_NAME, "0", "周界系统设备状态接口返回结果为空");
            }
            if (response.getBody().getCode() != 200) {
                throw new ServiceException(ProjectConstants.PROJECT_MODULE_NAME, "0", "周界系统设备状态接口失败：" + response.getBody().getMsg());
            }
            result.addAll(response.getBody().getData());
        }
        return result;
    }

    public List<PerimeterIntrusionZone> getZoneList() {
        HttpHeaders httpHeader = getHttpHeader();
        HttpEntity entity = new HttpEntity<>(httpHeader);
        List<TPerimeterIntrusionHostBaseInfo> list = perimeterIntrusionHostBaseInfoService.list();
        if(ObjectUtil.isEmpty(list)){
            return null;
        }
        List<PerimeterIntrusionZone> result = new ArrayList<>();
        for (TPerimeterIntrusionHostBaseInfo dto : list) {
            ResponseEntity<PerimeterIntrusionResponse<PerimeterIntrusionZone>> response = restTemplate.exchange(getBaseUrl(dto.getIpAddress()) + ZONE_LIST_URL, HttpMethod.GET, entity,
                    new ParameterizedTypeReference<PerimeterIntrusionResponse<PerimeterIntrusionZone>>(){});
            log.info("查询厂方周界防区状态返回：{}",response);
            if (response.getBody() == null) {
                throw new ServiceException(ProjectConstants.PROJECT_MODULE_NAME, "0", "周界系统防区列表接口返回结果为空");
            }
            if (response.getBody().getCode() != 200) {
                throw new ServiceException(ProjectConstants.PROJECT_MODULE_NAME, "0", "周界系统防区列表接口失败：" + response.getBody().getMsg());
            }
            result.addAll(response.getBody().getData().getRows());
        }
        return result;
    }

    public void armZone(List<PerimeterIntrusionArmZoneReq> armZoneReqList) {
        HttpHeaders httpHeader = getHttpHeader();
        httpHeader.setContentType(MediaType.APPLICATION_JSON);
        for (PerimeterIntrusionArmZoneReq request : armZoneReqList) {
            HttpEntity<PerimeterIntrusionArmZoneRequest> entity = new HttpEntity<>(request.getRequest(), httpHeader);
            ResponseEntity<PerimeterIntrusionResponse<Boolean>> response = restTemplate.exchange(getBaseUrl(request.getIpAddress()) + ARM_ZONE_URL, HttpMethod.POST, entity,
                    new ParameterizedTypeReference<PerimeterIntrusionResponse<Boolean>>(){});
            if (response.getBody() == null) {
                throw new ServiceException(ProjectConstants.PROJECT_MODULE_NAME, "0", "周界系统布防撤防接口返回结果为空");
            }
            if (response.getBody().getCode() != 200) {
                throw new ServiceException(ProjectConstants.PROJECT_MODULE_NAME, "0", "周界系统布防撤防接口失败：" + response.getBody().getMsg());
            }
        }

    }

    private HttpHeaders getHttpHeader() {
        HttpHeaders headers = new HttpHeaders();
        String apiKey = !StringUtils.isNullOrEmpty(configs.getConfigs().get(0).getApiKey()) ? configs.getConfigs().get(0).getApiKey() : UUID.fastUUID().toString();
        headers.add(API_KEY, apiKey);
        return headers;
    }

    public static void main(String[] args) {
        System.out.println(UUID.fastUUID().toString());
    }
}
