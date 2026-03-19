package cn.stylefeng.guns.modular.deviceRelation.service.impl;

import cn.stylefeng.guns.core.utils.PageToPageResultUtils;
import cn.stylefeng.guns.database.entity.TAccessControlBaseInfo;
import cn.stylefeng.guns.database.entity.TDeviceRelationRecords;
import cn.stylefeng.guns.database.entity.TEmergencyBroadcastHostBaseInfo;
import cn.stylefeng.guns.database.entity.TIndustrialTvBaseInfo;
import cn.stylefeng.guns.database.entity.TIndustrialTvPreset;
import cn.stylefeng.guns.database.service.TAccessControlBaseInfoService;
import cn.stylefeng.guns.database.service.TDeviceRelationRecordsService;
import cn.stylefeng.guns.database.service.TEmergencyBroadcastHostBaseInfoService;
import cn.stylefeng.guns.database.service.TIndustrialTvBaseInfoService;
import cn.stylefeng.guns.database.service.TIndustrialTvPresetService;
import cn.stylefeng.guns.modular.deviceRelation.entity.AccessControlRelationDTO;
import cn.stylefeng.guns.modular.deviceRelation.entity.CurrentAssociationsDTO;
import cn.stylefeng.guns.modular.deviceRelation.entity.EmergencyBroadcastRelationDTO;
import cn.stylefeng.guns.modular.deviceRelation.entity.IndustrialTvRelationDTO;
import cn.stylefeng.guns.modular.deviceRelation.service.DeviceRelationService;
import cn.stylefeng.guns.modular.deviceRelation.entity.DeviceRelationSaveDTO;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 设备关联Service 实现
 */
@Service
public class DeviceRelationServiceImpl implements DeviceRelationService {

    @Resource
    private TIndustrialTvBaseInfoService industrialTvBaseInfoService;

    @Resource
    private TAccessControlBaseInfoService accessControlBaseInfoService;

    @Resource
    private TEmergencyBroadcastHostBaseInfoService emergencyBroadcastHostBaseInfoService;

    @Resource
    private TIndustrialTvPresetService industrialTvPresetService;

    @Resource
    private TDeviceRelationRecordsService deviceRelationRecordsService;

    @Override
    public PageResult<IndustrialTvRelationDTO> listIndustrialTvForRelation(Map<String, Object> params) {
        int pageNo = getIntParam(params, "pageNo", 1);
        int pageSize = getIntParam(params, "pageSize", 10);
        String subsystemType = paramStr(params, "subsystemType");
        String relatedDeviceId = paramStr(params, "relatedDeviceId");

        LambdaQueryWrapper<TIndustrialTvBaseInfo> queryWrapper = new LambdaQueryWrapper<>();
        String stationId = paramStr(params, "stationId");
        if (stationId != null) {
            queryWrapper.eq(TIndustrialTvBaseInfo::getBelongStationId, stationId);
        }
        String deviceNameVal = paramStr(params, "deviceName");
        String deviceCodeVal = paramStr(params, "deviceCode");
        String brandVal = paramStr(params, "brand");
        if (deviceNameVal != null) {
            queryWrapper.like(TIndustrialTvBaseInfo::getDeviceName, deviceNameVal);
        }
        if (deviceCodeVal != null) {
            queryWrapper.like(TIndustrialTvBaseInfo::getDeviceCode, deviceCodeVal);
        }
        if (brandVal != null) {
            queryWrapper.like(TIndustrialTvBaseInfo::getBrand, brandVal);
        }

        Page<TIndustrialTvBaseInfo> page = industrialTvBaseInfoService.page(new Page<>(pageNo, pageSize), queryWrapper);
        List<TIndustrialTvBaseInfo> tvList = page.getRecords();
        if (tvList.isEmpty()) {
            Page<IndustrialTvRelationDTO> emptyPage = new Page<>(pageNo, pageSize);
            emptyPage.setTotal(0);
            emptyPage.setRecords(Collections.emptyList());
            return PageToPageResultUtils.pageToPageResult(emptyPage);
        }

        List<String> tvIds = tvList.stream()
                .map(TIndustrialTvBaseInfo::getDeviceId)
                .filter(id -> id != null && !id.trim().isEmpty())
                .collect(Collectors.toList());

        Map<String, List<TIndustrialTvPreset>> presetsByTvId;
        if (tvIds.isEmpty()) {
            presetsByTvId = Collections.emptyMap();
        } else {
            List<TIndustrialTvPreset> allPresets = industrialTvPresetService.lambdaQuery()
                    .in(TIndustrialTvPreset::getIndustrialTvId, tvIds)
                    .list();
            presetsByTvId = allPresets.stream()
                    .collect(Collectors.groupingBy(TIndustrialTvPreset::getIndustrialTvId));
        }

        Set<String> relatedPresetIds = Collections.emptySet();
        if (subsystemType != null && relatedDeviceId != null) {
            List<TDeviceRelationRecords> relationRecords = deviceRelationRecordsService.lambdaQuery()
                    .eq(TDeviceRelationRecords::getSubsystemType, subsystemType)
                    .eq(TDeviceRelationRecords::getRelatedDeviceId, relatedDeviceId)
                    .isNotNull(TDeviceRelationRecords::getPresetId)
                    .list();
            relatedPresetIds = relationRecords.stream()
                    .map(TDeviceRelationRecords::getPresetId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
        }

        final Set<String> relatedPresetIdsFinal = relatedPresetIds;

        List<IndustrialTvRelationDTO> dtoList = tvList.stream().map(tv -> {
            IndustrialTvRelationDTO dto = new IndustrialTvRelationDTO();
            dto.setDeviceId(tv.getDeviceId());
            dto.setDeviceName(tv.getDeviceName());
            dto.setDeviceCode(tv.getDeviceCode());
            dto.setBrand(tv.getBrand());
            dto.setModel(tv.getModel());
            dto.setCameraIp(tv.getCameraIp());

            List<TIndustrialTvPreset> presets = presetsByTvId.getOrDefault(tv.getDeviceId(), Collections.emptyList());
            dto.setPresetList(presets);

            List<String> relatedIdsForTv = presets.stream()
                    .map(TIndustrialTvPreset::getPresetId)
                    .filter(id -> relatedPresetIdsFinal.contains(id))
                    .collect(Collectors.toList());
            dto.setRelatedPresetIds(relatedIdsForTv);

            return dto;
        }).collect(Collectors.toList());

        Page<IndustrialTvRelationDTO> dtoPage = new Page<>(pageNo, pageSize);
        dtoPage.setTotal(page.getTotal());
        dtoPage.setRecords(dtoList);
        return PageToPageResultUtils.pageToPageResult(dtoPage);
    }

    @Override
    public PageResult<AccessControlRelationDTO> listAccessControlForRelation(Map<String, Object> params) {
        int pageNo = getIntParam(params, "pageNo", 1);
        int pageSize = getIntParam(params, "pageSize", 10);
        String subsystemType = paramStr(params, "subsystemType");
        String relatedDeviceId = paramStr(params, "relatedDeviceId");

        LambdaQueryWrapper<TAccessControlBaseInfo> queryWrapper = new LambdaQueryWrapper<>();
        String stationId = paramStr(params, "stationId");
        if (stationId != null) {
            queryWrapper.eq(TAccessControlBaseInfo::getBelongStationId, stationId);
        }
        String deviceNameVal = paramStr(params, "deviceName");
        String deviceCodeVal = paramStr(params, "deviceCode");
        String brandVal = paramStr(params, "brand");
        if (deviceNameVal != null) {
            queryWrapper.like(TAccessControlBaseInfo::getDeviceName, deviceNameVal);
        }
        if (deviceCodeVal != null) {
            queryWrapper.like(TAccessControlBaseInfo::getDeviceCode, deviceCodeVal);
        }
        if (brandVal != null) {
            queryWrapper.like(TAccessControlBaseInfo::getBrand, brandVal);
        }

        Page<TAccessControlBaseInfo> page = accessControlBaseInfoService.page(new Page<>(pageNo, pageSize), queryWrapper);
        List<TAccessControlBaseInfo> deviceList = page.getRecords();

        Set<String> relatedIds = Collections.emptySet();
        if (subsystemType != null && relatedDeviceId != null) {
            List<TDeviceRelationRecords> relationRecords = deviceRelationRecordsService.lambdaQuery()
                    .eq(TDeviceRelationRecords::getSubsystemType, subsystemType)
                    .eq(TDeviceRelationRecords::getRelatedDeviceId, relatedDeviceId)
                    .isNotNull(TDeviceRelationRecords::getAccessControlDeviceId)
                    .list();
            relatedIds = relationRecords.stream()
                    .map(TDeviceRelationRecords::getAccessControlDeviceId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
        }

        final Set<String> relatedAccessControlIds = relatedIds;
        List<AccessControlRelationDTO> dtoList = deviceList.stream().map(device -> {
            AccessControlRelationDTO dto = new AccessControlRelationDTO();
            dto.setDeviceId(device.getDeviceId());
            dto.setDeviceName(device.getDeviceName());
            dto.setDeviceCode(device.getDeviceCode());
            dto.setBrand(device.getBrand());
            dto.setModel(device.getModel());
            dto.setIpAddress(device.getIpAddress());
            dto.setChecked(relatedAccessControlIds.contains(device.getDeviceId()));
            return dto;
        }).collect(Collectors.toList());

        Page<AccessControlRelationDTO> dtoPage = new Page<>(pageNo, pageSize);
        dtoPage.setTotal(page.getTotal());
        dtoPage.setRecords(dtoList);
        return PageToPageResultUtils.pageToPageResult(dtoPage);
    }

    @Override
    public PageResult<EmergencyBroadcastRelationDTO> listEmergencyBroadcastForRelation(Map<String, Object> params) {
        int pageNo = getIntParam(params, "pageNo", 1);
        int pageSize = getIntParam(params, "pageSize", 10);
        String subsystemType = paramStr(params, "subsystemType");
        String relatedDeviceId = paramStr(params, "relatedDeviceId");

        LambdaQueryWrapper<TEmergencyBroadcastHostBaseInfo> queryWrapper = new LambdaQueryWrapper<>();
        String stationId = paramStr(params, "stationId");
        if (stationId != null) {
            queryWrapper.eq(TEmergencyBroadcastHostBaseInfo::getBelongStationId, stationId);
        }
        String deviceNameVal = paramStr(params, "deviceName");
        String deviceCodeVal = paramStr(params, "deviceCode");
        String brandVal = paramStr(params, "brand");
        if (deviceNameVal != null) {
            queryWrapper.like(TEmergencyBroadcastHostBaseInfo::getDeviceName, deviceNameVal);
        }
        if (deviceCodeVal != null) {
            queryWrapper.like(TEmergencyBroadcastHostBaseInfo::getDeviceCode, deviceCodeVal);
        }
        if (brandVal != null) {
            queryWrapper.like(TEmergencyBroadcastHostBaseInfo::getBrand, brandVal);
        }

        Page<TEmergencyBroadcastHostBaseInfo> page = emergencyBroadcastHostBaseInfoService.page(new Page<>(pageNo, pageSize), queryWrapper);
        List<TEmergencyBroadcastHostBaseInfo> deviceList = page.getRecords();

        Set<String> relatedIds = Collections.emptySet();
        if (subsystemType != null && relatedDeviceId != null) {
            List<TDeviceRelationRecords> relationRecords = deviceRelationRecordsService.lambdaQuery()
                    .eq(TDeviceRelationRecords::getSubsystemType, subsystemType)
                    .eq(TDeviceRelationRecords::getRelatedDeviceId, relatedDeviceId)
                    .isNotNull(TDeviceRelationRecords::getEmergencyBroadcastId)
                    .list();
            relatedIds = relationRecords.stream()
                    .map(TDeviceRelationRecords::getEmergencyBroadcastId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
        }

        final Set<String> relatedEmergencyBroadcastIds = relatedIds;

        List<EmergencyBroadcastRelationDTO> dtoList = deviceList.stream().map(device -> {
            EmergencyBroadcastRelationDTO dto = new EmergencyBroadcastRelationDTO();
            dto.setDeviceId(device.getDeviceId());
            dto.setDeviceName(device.getDeviceName());
            dto.setDeviceCode(device.getDeviceCode());
            dto.setBrand(device.getBrand());
            dto.setModel(device.getModel());
            dto.setIpAddress(device.getIpAddress());
            dto.setChecked(relatedEmergencyBroadcastIds.contains(device.getDeviceId()));
            return dto;
        }).collect(Collectors.toList());

        Page<EmergencyBroadcastRelationDTO> dtoPage = new Page<>(pageNo, pageSize);
        dtoPage.setTotal(page.getTotal());
        dtoPage.setRecords(dtoList);
        return PageToPageResultUtils.pageToPageResult(dtoPage);
    }

    @Override
    public CurrentAssociationsDTO getCurrentAssociations(String subsystemType, String relatedDeviceId) {
        CurrentAssociationsDTO result = new CurrentAssociationsDTO();
        result.setPresetIds(Collections.emptyList());
        result.setAccessControlDeviceIds(Collections.emptyList());
        result.setEmergencyBroadcastIds(Collections.emptyList());
        if (subsystemType == null || subsystemType.trim().isEmpty()
                || relatedDeviceId == null || relatedDeviceId.trim().isEmpty()) {
            return result;
        }
        List<TDeviceRelationRecords> list = deviceRelationRecordsService.lambdaQuery()
                .eq(TDeviceRelationRecords::getSubsystemType, subsystemType)
                .eq(TDeviceRelationRecords::getRelatedDeviceId, relatedDeviceId)
                .list();
        List<String> presetIds = list.stream()
                .map(TDeviceRelationRecords::getPresetId)
                .filter(Objects::nonNull)
                .filter(id -> !id.trim().isEmpty())
                .distinct()
                .collect(Collectors.toList());
        List<String> acIds = list.stream()
                .map(TDeviceRelationRecords::getAccessControlDeviceId)
                .filter(Objects::nonNull)
                .filter(id -> !id.trim().isEmpty())
                .distinct()
                .collect(Collectors.toList());
        List<String> ebIds = list.stream()
                .map(TDeviceRelationRecords::getEmergencyBroadcastId)
                .filter(Objects::nonNull)
                .filter(id -> !id.trim().isEmpty())
                .distinct()
                .collect(Collectors.toList());
        result.setPresetIds(presetIds);
        result.setAccessControlDeviceIds(acIds);
        result.setEmergencyBroadcastIds(ebIds);
        return result;
    }

    private String paramStr(Map<String, Object> params, String key) {
        if (params == null || !params.containsKey(key) || params.get(key) == null) {
            return null;
        }
        String s = String.valueOf(params.get(key)).trim();
        return s.isEmpty() ? null : s;
    }

    private int getIntParam(Map<String, Object> params, String key, int defaultValue) {
        if (params == null) {
            return defaultValue;
        }
        Object value = params.get(key);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveRelations(DeviceRelationSaveDTO dto) {
        String subsystemType = dto.getSubsystemType();
        String relatedDeviceId = dto.getRelatedDeviceId();
        if (subsystemType == null || subsystemType.trim().isEmpty()
                || relatedDeviceId == null || relatedDeviceId.trim().isEmpty()) {
            return false;
        }

        LambdaQueryWrapper<TDeviceRelationRecords> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(TDeviceRelationRecords::getSubsystemType, subsystemType)
                .eq(TDeviceRelationRecords::getRelatedDeviceId, relatedDeviceId);
        deviceRelationRecordsService.remove(deleteWrapper);

        Long userId = getCurrentUserId();
        java.util.Date now = new java.util.Date();

        if (dto.getPresetIds() != null) {
            for (String presetId : dto.getPresetIds()) {
                if (presetId == null || presetId.trim().isEmpty()) {
                    continue;
                }
                TDeviceRelationRecords record = buildBaseRecord(subsystemType, relatedDeviceId, userId, now);
                record.setPresetId(presetId);
                deviceRelationRecordsService.save(record);
            }
        }

        if (dto.getAccessControlDeviceIds() != null) {
            for (String acId : dto.getAccessControlDeviceIds()) {
                if (acId == null || acId.trim().isEmpty()) {
                    continue;
                }
                TDeviceRelationRecords record = buildBaseRecord(subsystemType, relatedDeviceId, userId, now);
                record.setAccessControlDeviceId(acId);
                deviceRelationRecordsService.save(record);
            }
        }

        if (dto.getEmergencyBroadcastIds() != null) {
            for (String ebId : dto.getEmergencyBroadcastIds()) {
                if (ebId == null || ebId.trim().isEmpty()) {
                    continue;
                }
                TDeviceRelationRecords record = buildBaseRecord(subsystemType, relatedDeviceId, userId, now);
                record.setEmergencyBroadcastId(ebId);
                deviceRelationRecordsService.save(record);
            }
        }

        return true;
    }

    private TDeviceRelationRecords buildBaseRecord(String subsystemType,
                                                   String relatedDeviceId,
                                                   Long userId,
                                                   java.util.Date now) {
        TDeviceRelationRecords record = new TDeviceRelationRecords();
        record.setRelationId(java.util.UUID.randomUUID().toString());
        record.setSubsystemType(subsystemType);
        record.setRelatedDeviceId(relatedDeviceId);
        record.setCreateUser(userId);
        record.setCreateTime(now);
        return record;
    }

    private Long getCurrentUserId() {
        try {
            cn.stylefeng.roses.kernel.auth.api.pojo.login.LoginUser loginUser =
                    cn.stylefeng.roses.kernel.auth.api.context.LoginContext.me().getLoginUserNullable();
            if (loginUser != null) {
                return loginUser.getUserId();
            }
        } catch (Exception ignored) {
        }
        return 1L;
    }
}

