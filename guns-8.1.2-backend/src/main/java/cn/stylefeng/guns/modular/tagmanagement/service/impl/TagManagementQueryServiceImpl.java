package cn.stylefeng.guns.modular.tagmanagement.service.impl;

import cn.stylefeng.guns.core.utils.PageToPageResultUtils;
import cn.stylefeng.guns.core.utils.SpringContextHolder;
import cn.stylefeng.guns.database.entity.TModelMapManagement;
import cn.stylefeng.guns.database.entity.TTagManagement;
import cn.stylefeng.guns.database.entity.TFireGasSensorBaseInfo;
import cn.stylefeng.guns.database.service.TModelMapManagementService;
import cn.stylefeng.guns.database.service.TTagManagementService;
import cn.stylefeng.guns.database.service.TFireGasSensorBaseInfoService;
import cn.stylefeng.guns.enums.SystemTypeEnum;
import cn.stylefeng.guns.modular.tagmanagement.mapper.TagManagementQueryMapper;
import cn.stylefeng.guns.modular.tagmanagement.request.TagManagementListRequest;
import cn.stylefeng.guns.modular.tagmanagement.response.TagDeviceOptionResponse;
import cn.stylefeng.guns.modular.tagmanagement.response.TagManagementListResponse;
import cn.stylefeng.guns.modular.tagmanagement.response.TagModelOptionResponse;
import cn.stylefeng.guns.modular.tagmanagement.response.TagSubsystemOptionResponse;
import cn.stylefeng.guns.modular.tagmanagement.service.TagManagementQueryService;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TagManagementQueryServiceImpl implements TagManagementQueryService {

    @Resource
    private TagManagementQueryMapper tagManagementQueryMapper;

    @Resource
    private TModelMapManagementService tModelMapManagementService;

    @Resource
    private TTagManagementService tTagManagementService;

    @Resource
    private TFireGasSensorBaseInfoService tFireGasSensorBaseInfoService;

    @Override
    public PageResult<TagManagementListResponse> getTagList(TagManagementListRequest request) {
        Page<TagManagementListResponse> page = new Page<>(defaultPageNo(request), defaultPageSize(request));
        IPage<TagManagementListResponse> tagPage = tagManagementQueryMapper.selectTagPage(page, request);

        List<TagManagementListResponse> records = tagPage.getRecords();
        if (records == null || records.isEmpty()) {
            return PageToPageResultUtils.pageToPageResult(tagPage);
        }

        Map<String, String> deviceNameMap = getDeviceNameMap(records);
        for (TagManagementListResponse item : records) {
            item.setSubsystemTypeName(SystemTypeEnum.getDescriptionByCode(item.getSubsystemType()));
            item.setDeviceName(deviceNameMap.get(buildDeviceKey(item.getSubsystemType(), item.getDeviceId())));
        }
        return PageToPageResultUtils.pageToPageResult(tagPage);
    }

    @Override
    public List<TagSubsystemOptionResponse> subsystemOptions() {
        List<TagSubsystemOptionResponse> result = new ArrayList<>();
        for (SystemTypeEnum systemType : SystemTypeEnum.values()) {
            TagSubsystemOptionResponse item = new TagSubsystemOptionResponse();
            item.setSubsystemType(systemType.getCode());
            item.setSubsystemTypeName(systemType.getDescription());
            result.add(item);
        }
        return result;
    }

    @Override
    public List<TagDeviceOptionResponse> deviceOptions(String belongStationId, String subsystemType) {
        if (StringUtils.isBlank(belongStationId) || StringUtils.isBlank(subsystemType)) {
            return Collections.emptyList();
        }
        Class<? extends IService> serviceClass = SystemTypeEnum.getServiceByCode(subsystemType);
        if (serviceClass == null) {
            return Collections.emptyList();
        }
        IService service = SpringContextHolder.getBean(serviceClass);
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("belong_station_id", belongStationId);
        List<?> deviceList = service.list(queryWrapper);

        if ("HQXT".equals(subsystemType)) {
            return fireGasSensorDeviceOptions(deviceList);
        }

        List<TagDeviceOptionResponse> result = new ArrayList<>();
        for (Object deviceObj : deviceList) {
            JSONObject json = JSONObject.parseObject(JSON.toJSONString(deviceObj));
            String deviceId = json.getString("deviceId");
            if (StringUtils.isBlank(deviceId)) {
                continue;
            }
            TagDeviceOptionResponse item = new TagDeviceOptionResponse();
            item.setDeviceId(deviceId);
            item.setDeviceName(json.getString("deviceName"));
            result.add(item);
        }
        return result;
    }

    @Override
    public List<TagModelOptionResponse> modelOptions(String belongStationId) {
        if (StringUtils.isBlank(belongStationId)) {
            return Collections.emptyList();
        }
        List<TModelMapManagement> modelList = tModelMapManagementService.lambdaQuery()
                .eq(TModelMapManagement::getBelongStationValveChamberId, belongStationId)
                .orderByDesc(TModelMapManagement::getCreateTime)
                .list();

        List<TagModelOptionResponse> result = new ArrayList<>();
        for (TModelMapManagement model : modelList) {
            TagModelOptionResponse item = new TagModelOptionResponse();
            item.setModelId(model.getModelId());
            item.setModelName(model.getModelName());
            item.setModelAddress(model.getModelAddress());
            item.setModelFileId(model.getModelFileId());
            item.setPosition(model.getPosition());
            result.add(item);
        }
        return result;
    }

    @Override
    public boolean addTag(TTagManagement tagManagement) {
        if (tagManagement == null) {
            return false;
        }
        if (StringUtils.isBlank(tagManagement.getTagId())) {
            tagManagement.setTagId(IdWorker.getIdStr());
        }
        return tTagManagementService.save(tagManagement);
    }

    @Override
    public boolean updateTag(TTagManagement tagManagement) {
        if (tagManagement == null || StringUtils.isBlank(tagManagement.getTagId())) {
            return false;
        }
        return tTagManagementService.updateById(tagManagement);
    }

    @Override
    public boolean deleteTag(String tagId) {
        if (StringUtils.isBlank(tagId)) {
            return false;
        }
        return tTagManagementService.removeById(tagId);
    }

    private Map<String, String> getDeviceNameMap(List<TagManagementListResponse> records) {
        Map<String, List<TagManagementListResponse>> subsystemMap = records.stream()
                .filter(item -> StringUtils.isNotBlank(item.getSubsystemType()) && StringUtils.isNotBlank(item.getDeviceId()))
                .collect(Collectors.groupingBy(TagManagementListResponse::getSubsystemType));

        Map<String, String> result = new HashMap<>();
        for (Map.Entry<String, List<TagManagementListResponse>> entry : subsystemMap.entrySet()) {
            Class<? extends IService> serviceClass = SystemTypeEnum.getServiceByCode(entry.getKey());
            if (serviceClass == null) {
                continue;
            }
            Set<String> deviceIdSet = entry.getValue().stream()
                    .map(TagManagementListResponse::getDeviceId)
                    .collect(Collectors.toSet());
            if (deviceIdSet.isEmpty()) {
                continue;
            }
            IService service = SpringContextHolder.getBean(serviceClass);
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.in("device_id", deviceIdSet);
            List<?> deviceList = service.list(queryWrapper);

            for (Object deviceObj : deviceList) {
                JSONObject json = JSONObject.parseObject(JSON.toJSONString(deviceObj));
                String deviceId = json.getString("deviceId");
                if (StringUtils.isBlank(deviceId)) {
                    continue;
                }
                result.put(buildDeviceKey(entry.getKey(), deviceId), json.getString("deviceName"));
            }
        }
        return result;
    }

    private int defaultPageNo(TagManagementListRequest request) {
        return request.getPageNo() == null || request.getPageNo() < 1 ? 1 : request.getPageNo();
    }

    private int defaultPageSize(TagManagementListRequest request) {
        return request.getPageSize() == null || request.getPageSize() < 1 ? 10 : request.getPageSize();
    }

    private String buildDeviceKey(String subsystemType, String deviceId) {
        return subsystemType + "#" + deviceId;
    }

    private List<TagDeviceOptionResponse> fireGasSensorDeviceOptions(List<?> hostDeviceList) {
        if (hostDeviceList == null || hostDeviceList.isEmpty()) {
            return Collections.emptyList();
        }

        Set<String> hostIds = new HashSet<>();
        for (Object hostObj : hostDeviceList) {
            JSONObject json = JSONObject.parseObject(JSON.toJSONString(hostObj));
            String hostId = json.getString("deviceId");
            if (StringUtils.isBlank(hostId)) {
                continue;
            }
            hostIds.add(hostId);
        }
        if (hostIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<TFireGasSensorBaseInfo> sensorList = tFireGasSensorBaseInfoService.lambdaQuery()
                .in(TFireGasSensorBaseInfo::getFireGasHostId, hostIds)
                .list();
        if (sensorList == null || sensorList.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<TagDeviceOptionResponse> result = new ArrayList<>();
        for (TFireGasSensorBaseInfo sensor : sensorList) {
            if (sensor == null || StringUtils.isBlank(sensor.getDeviceId())) {
                continue;
            }
            TagDeviceOptionResponse item = new TagDeviceOptionResponse();
            item.setDeviceId(sensor.getDeviceId());
            item.setDeviceName(sensor.getDeviceName());
            result.add(item);
        }
        return result;
    }
}
