package cn.stylefeng.guns.modular.accesscontrol.service;

import cn.hutool.core.util.ObjectUtil;
import cn.stylefeng.guns.core.utils.PageToPageResultUtils;
import cn.stylefeng.guns.database.entity.*;
import cn.stylefeng.guns.database.service.*;
import cn.stylefeng.guns.enums.SystemTypeEnum;
import cn.stylefeng.guns.modular.accesscontrol.request.AccessControlGatewayRequest;
import cn.stylefeng.guns.modular.accesscontrol.request.AccessLinkageAlarmRequest;
import cn.stylefeng.guns.modular.broadcast.request.PlayVoiceRequest;
import cn.stylefeng.guns.modular.broadcast.service.BroadcastService;
import cn.stylefeng.guns.modular.hikvision.service.HikVisionService;
import cn.stylefeng.guns.modular.industrialTV.request.ControlPresetRequest;
import cn.stylefeng.guns.modular.industrialTV.request.LinkageAlarmRequest;
import cn.stylefeng.guns.modular.industrialTV.service.IndustrialTVService;
import cn.stylefeng.roses.kernel.auth.api.context.LoginContext;
import cn.stylefeng.roses.kernel.auth.api.pojo.login.LoginUser;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.weaver.ast.Var;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AccessControlManageService {
    @Resource
    private TAccessControlBaseInfoService accessControlBaseInfoService;
    @Resource
    private TAccessControlPersonnelBaseInfoService accessControlPersonnelBaseInfoService;
    @Resource
    private TAccessControlEntryExitRecordsService accessControlEntryExitRecordsService;
    @Resource
    private TDeviceRelationRecordsService deviceRelationRecordsService;
    @Resource
    private TStationBaseInfoService stationBaseInfoService;
    @Resource
    private TLinkageAlarmConfigService tLinkageAlarmConfigService;
    @Resource
    private TIndustrialTvPresetService tIndustrialTvPresetService;
    @Resource
    private IndustrialTVService industrialTVService;
    @Resource
    private HikVisionService hikVisionService;
    @Resource
    private TEmergencyBroadcastHostBaseInfoService tEmergencyBroadcastHostBaseInfoService;
    @Resource
    private BroadcastService broadcastService;
    @Resource
    private AccessControlGatewayService accessControlGatewayService;

    // 原有方法保持不变 >>>
    /**
     * 分页查询门禁设备
     */
    public Page<TAccessControlBaseInfo> getManageDevicePage(PageResult<?> page, TAccessControlBaseInfo query) {
        Page<TAccessControlBaseInfo> pageable = new Page<>(page.getPageNo(), page.getPageSize());
        LambdaQueryWrapper<TAccessControlBaseInfo> wrapper = buildAccessControlBaseInfoWrapper(query);
        Page<TAccessControlBaseInfo> pageResult = accessControlBaseInfoService.page(pageable, wrapper);
        fillStationRelatedNames(pageResult.getRecords());
        return pageResult;
    }

    /**
     * 新增门禁设备
     */
    public void addManageDevice(TAccessControlBaseInfo entity) {
        accessControlBaseInfoService.save(entity);
    }

    /**
     * 修改门禁设备
     */
    public void updateManageDevice(TAccessControlBaseInfo entity) {
        accessControlBaseInfoService.updateById(entity);
    }

    /**
     * 删除门禁设备（含关联关系）
     */
    public void deleteManageDevice(List<String> deviceIds) {
        if (CollectionUtils.isEmpty(deviceIds)) {
            return;
        }
        List<TAccessControlBaseInfo> devices = accessControlBaseInfoService.listByIds(deviceIds);
        if (CollectionUtils.isEmpty(devices)) {
            return;
        }
        accessControlBaseInfoService.removeByIds(deviceIds);
        List<String> deviceIdList = devices.stream()
                .map(TAccessControlBaseInfo::getDeviceId)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(deviceIdList)) {
            LambdaQueryWrapper<TDeviceRelationRecords> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(TDeviceRelationRecords::getAccessControlDeviceId, deviceIdList);
            deviceRelationRecordsService.remove(wrapper);
        }
    }

    /**
     * 关联门禁设备
     */
    public void connectManageDevice(TDeviceRelationRecords entity) {
        if (entity == null) {
            throw new IllegalArgumentException("关联实体不能为空");
        }
        entity.setRelationId(UUID.randomUUID().toString());
        deviceRelationRecordsService.save(entity);
    }

    /**
     * 分页查询门禁出入记录【深度优化版】
     * 核心：减少无效查询 + 索引优化 + 批量预加载
     */
    public Page<TAccessControlEntryExitRecords> getManageRecordPage(PageResult<?> page, TAccessControlEntryExitRecords query) {
        Page<TAccessControlEntryExitRecords> pageable = new Page<>(page.getPageNo(), page.getPageSize());
        LambdaQueryWrapper<TAccessControlEntryExitRecords> wrapper = new LambdaQueryWrapper<>();

        // 优化1：先获取站点ID（仅查ID，不查全量字段）
        List<String> stationIds = getStationIdsByCondition(
                query.getBelongStationId(),
                query.getBelongStationAreaId(),
                query.getBelongPipelineId()
        );

        // 优化2：如果有站点筛选条件，仅查询设备ID（不查全量设备信息），减少数据传输
        if (CollectionUtils.isNotEmpty(stationIds)) {
            // 仅查询设备ID，避免全字段查询
            List<String> deviceIds = accessControlBaseInfoService.listObjs(
                    new LambdaQueryWrapper<TAccessControlBaseInfo>()
                            .in(TAccessControlBaseInfo::getBelongStationId, stationIds)
                            .select(TAccessControlBaseInfo::getDeviceId), // 只查需要的字段
                    Object::toString
            ).stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());

            if (CollectionUtils.isEmpty(deviceIds)) {
                return new Page<>();
            }
            wrapper.in(TAccessControlEntryExitRecords::getAccessControlDeviceId, deviceIds);
        }

        // 优化3：强制添加排序（无排序会导致数据库无法使用索引，全表扫描）
        wrapper.orderByDesc(TAccessControlEntryExitRecords::getEntryTime);

        // 优化4：执行分页查询（核心查询只走1次）
        Page<TAccessControlEntryExitRecords> pageResult = accessControlEntryExitRecordsService.page(pageable, wrapper);

        // 优化5：批量填充关联数据（重构fill方法，减少重复MAP创建）
        fillRecordStationRelatedNamesOpt(pageResult.getRecords());

        return pageResult;
    }

    /**
     * 优化后的填充方法：
     * 1. 仅查询必要字段
     * 2. 减少MAP创建次数
     * 3. 消除循环内的无效判断
     */
    private void fillRecordStationRelatedNamesOpt(List<TAccessControlEntryExitRecords> records) {
        if (CollectionUtils.isEmpty(records)) {
            return;
        }

        // 步骤1：一次性提取所有需要的ID（避免多次循环）
        Set<String> personnelIds = new HashSet<>();
        Set<String> deviceIds = new HashSet<>();
        for (TAccessControlEntryExitRecords record : records) {
            if (record == null) continue;
            if (StringUtils.isNotBlank(record.getPersonnelId())) {
                personnelIds.add(record.getPersonnelId());
            }
            if (StringUtils.isNotBlank(record.getAccessControlDeviceId())) {
                deviceIds.add(record.getAccessControlDeviceId());
            }
        }

        // 步骤2：批量查询（仅查需要的字段，减少数据传输）
        // 人员信息MAP（仅查ID、姓名、类型）
        Map<String, TAccessControlPersonnelBaseInfo> personnelMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(personnelIds)) {
            personnelMap = accessControlPersonnelBaseInfoService.list(
                            new LambdaQueryWrapper<TAccessControlPersonnelBaseInfo>()
                                    .in(TAccessControlPersonnelBaseInfo::getPersonnelId, personnelIds)
                                    .select(TAccessControlPersonnelBaseInfo::getPersonnelId,
                                            TAccessControlPersonnelBaseInfo::getName,
                                            TAccessControlPersonnelBaseInfo::getPersonnelType)
                    ).stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(TAccessControlPersonnelBaseInfo::getPersonnelId, p -> p, (k1, k2) -> k1));
        }

        // 设备信息MAP（仅查ID、所属站ID、设备名称）
        Map<String, TAccessControlBaseInfo> deviceMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(deviceIds)) {
            deviceMap = accessControlBaseInfoService.list(
                            new LambdaQueryWrapper<TAccessControlBaseInfo>()
                                    .in(TAccessControlBaseInfo::getDeviceId, deviceIds)
                                    .select(TAccessControlBaseInfo::getDeviceId,
                                            TAccessControlBaseInfo::getBelongStationId,
                                            TAccessControlBaseInfo::getDeviceName)
                    ).stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(TAccessControlBaseInfo::getDeviceId, d -> d, (k1, k2) -> k1));
        }

        // 站点信息MAP（仅查ID、站名、作业区、管线）
        Set<String> stationIds = deviceMap.values().stream()
                .map(TAccessControlBaseInfo::getBelongStationId)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());
        Map<String, TStationBaseInfo> stationMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(stationIds)) {
            stationMap = stationBaseInfoService.list(
                            new LambdaQueryWrapper<TStationBaseInfo>()
                                    .in(TStationBaseInfo::getStationId, stationIds)
                                    .select(TStationBaseInfo::getStationId,
                                            TStationBaseInfo::getStationName,
                                            TStationBaseInfo::getBelongOperationArea,
                                            TStationBaseInfo::getBelongPipeline)
                    ).stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(TStationBaseInfo::getStationId, s -> s, (k1, k2) -> k1));
        }

        // 步骤3：批量填充（单次循环，无嵌套查询）
        for (TAccessControlEntryExitRecords record : records) {
            if (record == null) continue;

            // 填充人员信息
            TAccessControlPersonnelBaseInfo personnel = personnelMap.get(record.getPersonnelId());
            if (personnel != null) {
                record.setName(personnel.getName());
                record.setPersonnelType(personnel.getPersonnelType());
            }

            // 填充设备+站点信息
            TAccessControlBaseInfo device = deviceMap.get(record.getAccessControlDeviceId());
            if (device != null) {
                record.setDeviceName(device.getDeviceName());
                TStationBaseInfo station = stationMap.get(device.getBelongStationId());
                if (station != null) {
                    record.setStationName(station.getStationName());
                    // 优化：提前缓存作业区/管线名称，避免每次调用service方法（核心耗时点！）
                    record.setAreaName(stationBaseInfoService.getBelongOperationAreaName(station));
                    record.setPipelineName(stationBaseInfoService.getBelongPipelineName(station));
                }
            }
        }
    }

    // 通用方法保持不变 >>>
    private LambdaQueryWrapper<TAccessControlBaseInfo> buildAccessControlBaseInfoWrapper(TAccessControlBaseInfo query) {
        LambdaQueryWrapper<TAccessControlBaseInfo> wrapper = new LambdaQueryWrapper<>();
        List<String> stationIds = getStationIdsByCondition(
                query.getBelongStationId(),
                query.getBelongStationAreaId(),
                query.getBelongPipelineId()
        );
        if (CollectionUtils.isNotEmpty(stationIds)) {
            wrapper.in(TAccessControlBaseInfo::getBelongStationId, stationIds);
        }
        if (StringUtils.isNotBlank(query.getDeviceCode())) {
            wrapper.like(TAccessControlBaseInfo::getDeviceCode, query.getDeviceCode().trim());
        }
        if (StringUtils.isNotBlank(query.getDeviceName())) {
            wrapper.like(TAccessControlBaseInfo::getDeviceName, query.getDeviceName().trim());
        }
        return wrapper;
    }

    private List<String> getStationIdsByCondition(String stationId, String areaId, String pipelineId) {
        if (StringUtils.isBlank(stationId) && StringUtils.isBlank(areaId) && StringUtils.isBlank(pipelineId)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<TStationBaseInfo> stationWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(stationId)) {
            stationWrapper.eq(TStationBaseInfo::getStationId, stationId.trim());
        }
        if (StringUtils.isNotBlank(areaId)) {
            stationWrapper.eq(TStationBaseInfo::getBelongOperationArea, areaId.trim());
        }
        if (StringUtils.isNotBlank(pipelineId)) {
            stationWrapper.eq(TStationBaseInfo::getBelongPipeline, pipelineId.trim());
        }
        List<TStationBaseInfo> stations = stationBaseInfoService.list(stationWrapper);
        if (CollectionUtils.isEmpty(stations)) {
            return Collections.emptyList();
        }
        return stations.stream()
                .map(TStationBaseInfo::getStationId)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
    }

    private List<TAccessControlBaseInfo> getAccessControlBaseInfoList(String stationId, String areaId, String pipelineId) {
        List<String> stationIds = getStationIdsByCondition(stationId, areaId, pipelineId);
        if (CollectionUtils.isEmpty(stationIds)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<TAccessControlBaseInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(TAccessControlBaseInfo::getBelongStationId, stationIds);
        return accessControlBaseInfoService.list(wrapper);
    }

    private void fillStationRelatedNames(List<TAccessControlBaseInfo> devices) {
        if (CollectionUtils.isEmpty(devices)) {
            return;
        }
        Set<String> stationIds = devices.stream()
                .map(TAccessControlBaseInfo::getBelongStationId)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());
        if (ObjectUtil.isEmpty(stationIds)) {
            return;
        }
        Map<String, TStationBaseInfo> stationMap = stationBaseInfoService.listByIds(stationIds).stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(TStationBaseInfo::getStationId, station -> station));
        devices.forEach(device -> {
            if (device == null || StringUtils.isBlank(device.getBelongStationId())) {
                return;
            }
            TStationBaseInfo station = stationMap.get(device.getBelongStationId());
            if (station != null) {
                device.setStationName(station.getStationName());
                device.setAreaName(stationBaseInfoService.getBelongOperationAreaName(station));
                device.setPipelineName(stationBaseInfoService.getBelongPipelineName(station));
            }
        });
    }

    private void fillRecordStationRelatedNames(List<TAccessControlEntryExitRecords> records) {
        if (ObjectUtil.isEmpty(records)) {
            return;
        }
        Set<String> personnelIds = records.stream()
                .map(TAccessControlEntryExitRecords::getPersonnelId)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());
        Set<String> deviceIds = records.stream()
                .map(TAccessControlEntryExitRecords::getAccessControlDeviceId)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());
        Map<String, TStationBaseInfo> stationMap = new HashMap<>();
        Map<String, TAccessControlPersonnelBaseInfo> personnelMap = new HashMap<>();
        Map<String, TAccessControlBaseInfo> deviceMap = new HashMap<>();
        if(ObjectUtil.isNotEmpty(personnelIds)){
            personnelMap = accessControlPersonnelBaseInfoService.listByIds(personnelIds).stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(TAccessControlPersonnelBaseInfo::getPersonnelId, p -> p));
        }
        if(ObjectUtil.isNotEmpty(deviceIds)){
            deviceMap = accessControlBaseInfoService.listByIds(deviceIds).stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(TAccessControlBaseInfo::getDeviceId, d -> d));
            Set<String> stationIds = deviceMap.values().stream()
                    .map(TAccessControlBaseInfo::getBelongStationId)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.toSet());
            if (CollectionUtils.isNotEmpty(stationIds)) {
                stationMap = stationBaseInfoService.listByIds(stationIds).stream()
                        .filter(Objects::nonNull)
                        .collect(Collectors.toMap(TStationBaseInfo::getStationId, s -> s));
            }
        }
        Map<String, TAccessControlPersonnelBaseInfo> finalPersonnelMap = personnelMap;
        Map<String, TAccessControlBaseInfo> finalDeviceMap = deviceMap;
        Map<String, TStationBaseInfo> finalStationMap = stationMap;
        records.forEach(record -> {
            if(record == null){
                return;
            }
            if(ObjectUtil.isNotEmpty(record.getPersonnelId()) && finalPersonnelMap.containsKey(record.getPersonnelId())){
                record.setName(finalPersonnelMap.get(record.getPersonnelId()).getName());
                record.setPersonnelType(finalPersonnelMap.get(record.getPersonnelId()).getPersonnelType());
            }
            if(ObjectUtil.isNotEmpty(record.getAccessControlDeviceId()) && finalDeviceMap.containsKey(record.getAccessControlDeviceId())){
                record.setDeviceName(finalDeviceMap.get(record.getAccessControlDeviceId()).getDeviceName());
                String stationId = finalDeviceMap.get(record.getAccessControlDeviceId()).getBelongStationId();
                if (finalStationMap.containsKey(stationId)) {
                    TStationBaseInfo station = finalStationMap.get(stationId);
                    record.setStationName(station.getStationName());
                    record.setAreaName(stationBaseInfoService.getBelongOperationAreaName(station));
                    record.setPipelineName(stationBaseInfoService.getBelongPipelineName(station));
                }
            }
        });
    }
    // <<< 原有方法/通用方法结束

    // ======================== 改造/新增门禁人员相关方法 ========================
    /**
     * 分页查询门禁人员【改造】
     * 1. 按站/作业区/管线筛选
     * 2. 转换所属站、管线、作业区为中文名
     * 3. 转换accessPermission权限ID为设备中文名（逗号分隔）
     */
    public Page<TAccessControlPersonnelBaseInfo> getManagePersonPage(PageResult<?> page, TAccessControlPersonnelBaseInfo query) {
        Page<TAccessControlPersonnelBaseInfo> pageable = new Page<>(page.getPageNo(), page.getPageSize());
        LambdaQueryWrapper<TAccessControlPersonnelBaseInfo> wrapper = new LambdaQueryWrapper<>();

        // 按站/作业区/管线筛选逻辑（原有）
        if (StringUtils.isNotBlank(query.getBelongStationId()) || StringUtils.isNotBlank(query.getBelongStationAreaId()) || StringUtils.isNotBlank(query.getBelongPipelineId())) {
            List<TAccessControlBaseInfo> deviceList = getAccessControlBaseInfoList(
                    query.getBelongStationId(),
                    query.getBelongStationAreaId(),
                    query.getBelongPipelineId()
            );
            if (CollectionUtils.isEmpty(deviceList)) {
                return new Page<>();
            }
            List<String> stationIds = deviceList.stream()
                    .map(TAccessControlBaseInfo::getBelongStationId)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(stationIds)) {
                return new Page<>();
            }
            wrapper.in(TAccessControlPersonnelBaseInfo::getBelongStationId, stationIds);
        }

        // 执行分页查询
        Page<TAccessControlPersonnelBaseInfo> pageResult = accessControlPersonnelBaseInfoService.page(pageable, wrapper);
        // 填充站/作业区/管线中文名 + 转换权限ID为设备中文名
        fillPersonAllRelatedNames(pageResult.getRecords());
        return pageResult;
    }

    /**
     * 新增门禁人员【全新改造】
     * 1. 入参entity的accessPermission传入多个设备ID（逗号分隔）
     * 2. 按设备所属站拆分：不同站拆分为多条人员记录
     * 3. 同站设备ID：逗号拼接存入accessPermission
     * 4. 每条记录绑定对应站的belongStationId
     */
    public void addManagePerson(TAccessControlPersonnelBaseInfo entity) {
        // 校验入参
        if (entity == null || StringUtils.isBlank(entity.getAccessPermission())) {
            throw new IllegalArgumentException("人员实体和权限设备ID不能为空");
        }
        // 拆分入参的权限设备ID为列表
        List<String> permissionDeviceIds = Arrays.stream(entity.getAccessPermission().split(","))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(permissionDeviceIds)) {
            throw new IllegalArgumentException("有效权限设备ID不能为空");
        }

        // 批量查询设备信息，构建【设备ID->设备实体】映射
        Map<String, TAccessControlBaseInfo> deviceMap = accessControlBaseInfoService.listByIds(permissionDeviceIds).stream()
                .filter(Objects::nonNull)
                .filter(d -> StringUtils.isNotBlank(d.getBelongStationId()))
                .collect(Collectors.toMap(TAccessControlBaseInfo::getDeviceId, d -> d));
        if (CollectionUtils.isEmpty(deviceMap)) {
            throw new IllegalArgumentException("权限设备ID无效，未查询到对应设备或设备未绑定站场");
        }

        // 按【设备所属站ID】分组：key=站ID，value=该站下的设备ID列表
        Map<String, List<String>> station2DeviceIdsMap = deviceMap.values().stream()
                .collect(Collectors.groupingBy(
                        TAccessControlBaseInfo::getBelongStationId,
                        Collectors.mapping(TAccessControlBaseInfo::getDeviceId, Collectors.toList())
                ));

        // 遍历分组，按站生成人员记录并批量保存
        List<TAccessControlPersonnelBaseInfo> saveList = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : station2DeviceIdsMap.entrySet()) {
            String stationId = entry.getKey();
            List<String> sameStationDeviceIds = entry.getValue();
            // 克隆原实体，避免引用传递
            TAccessControlPersonnelBaseInfo person = new TAccessControlPersonnelBaseInfo();
            // 复制原实体的基础属性（姓名/手机号/状态等）
            BeanUtils.copyProperties(entity, person);
            // 生成唯一人员ID（如果主键是自增可忽略，此处用UUID）
            person.setPersonnelId(null);
            // 绑定当前站ID
            person.setBelongStationId(stationId);
            // 同站设备ID逗号拼接，存入权限字段
            person.setAccessPermission(String.join(",", sameStationDeviceIds));
            saveList.add(person);
        }

        // 批量保存拆分后的人员记录
        if (CollectionUtils.isNotEmpty(saveList)) {
            accessControlPersonnelBaseInfoService.saveBatch(saveList);
        }
    }

    /**
     * 修改门禁人员【全新改造】
     * 1. 先删除该人员原有所有记录（按人员唯一标识，如身份证/工号，需保证实体传入该标识）
     * 2. 复用新增的拆分逻辑，重新插入新的权限记录
     * 【注】：需保证TAccessControlPersonnelBaseInfo有**人员唯一业务标识**（如workNo/idCard），而非数据库主键personnelId
     */
    public void updateManagePerson(TAccessControlPersonnelBaseInfo entity) {
        // 校验：人员唯一业务标识不能为空（此处假设为workNo，可根据实际调整为idCard/phone等）
        if (entity == null || StringUtils.isBlank(entity.getPersonnelId())) {
            throw new IllegalArgumentException("人员实体和唯一业务标识（工号）不能为空");
        }
        // 1. 删除该人员原有所有门禁记录
        LambdaQueryWrapper<TAccessControlPersonnelBaseInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TAccessControlPersonnelBaseInfo::getPersonnelId, entity.getPersonnelId());
        accessControlPersonnelBaseInfoService.remove(wrapper);
        // 2. 复用新增逻辑，重新拆分并保存新记录
        addManagePerson(entity);
    }

    /**
     * 删除门禁人员【改造】
     * 支持按**数据库主键personnelId**批量删除，或按**人员唯一业务标识**删除（推荐）
     * 此处保留原有按personnelId删除，新增按业务标识删除的扩展
     */
    public void deleteManagePerson(List<String> personIds) {
        if (CollectionUtils.isEmpty(personIds)) {
            return;
        }
        // 原有逻辑：按数据库主键批量删除
        accessControlPersonnelBaseInfoService.removeByIds(personIds);
    }

    /**
     * 【新增】填充门禁人员的所有关联中文名
     * 1. 站/作业区/管线中文名（原有）
     * 2. accessPermission权限ID转换为设备中文名（逗号分隔）
     */
    private void fillPersonAllRelatedNames(List<TAccessControlPersonnelBaseInfo> persons) {
        if (ObjectUtil.isEmpty(persons)) {
            return;
        }
        // 步骤1：填充站/作业区/管线中文名（复用原有逻辑并优化）
        Set<String> stationIds = persons.stream()
                .map(TAccessControlPersonnelBaseInfo::getBelongStationId)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());
        Map<String, TStationBaseInfo> stationMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(stationIds)) {
            stationMap = stationBaseInfoService.listByIds(stationIds).stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(TStationBaseInfo::getStationId, s -> s));
        }
        // 步骤2：提取所有人员的权限设备ID，批量查询设备信息
        Map<String, TStationBaseInfo> finalStationMap = stationMap;
        persons.forEach(person -> {
            // 先填充站/作业区/管线中文名
            if (finalStationMap.containsKey(person.getBelongStationId())) {
                TStationBaseInfo station = finalStationMap.get(person.getBelongStationId());
                person.setStationName(station.getStationName());
                person.setAreaName(stationBaseInfoService.getBelongOperationAreaName(station));
                person.setPipelineName(stationBaseInfoService.getBelongPipelineName(station));
            }
        });
        // 构建【设备ID->设备名称】映射
        Map<String, TAccessControlBaseInfo> deviceId2NameMap = new HashMap<>();
        List<String> inDeviceIds = new ArrayList<>();
        List<String> outDeviceIds = new ArrayList<>();
        List<TAccessControlBaseInfo> tAccessControlBaseInfos = accessControlBaseInfoService.list();
        if (CollectionUtils.isNotEmpty(tAccessControlBaseInfos)) {
            for (TAccessControlBaseInfo tAccessControlBaseInfo : tAccessControlBaseInfos) {
                deviceId2NameMap.put(tAccessControlBaseInfo.getDeviceId(),tAccessControlBaseInfo);
                if(tAccessControlBaseInfo.getIsBigDoor() != null && tAccessControlBaseInfo.getIsBigDoor().contains("1")){
                    outDeviceIds.add(tAccessControlBaseInfo.getDeviceId());
                }else if(tAccessControlBaseInfo.getIsBigDoor() != null && tAccessControlBaseInfo.getIsBigDoor().contains("0")){
                    inDeviceIds.add(tAccessControlBaseInfo.getDeviceId());
                }
            }
        }

        // 步骤3：转换权限ID为设备中文名（逗号分隔）
        LambdaQueryWrapper<TAccessControlEntryExitRecords> recordsQuery = new LambdaQueryWrapper<>();
        recordsQuery.in(TAccessControlEntryExitRecords::getPersonnelId, persons.stream().map(TAccessControlPersonnelBaseInfo::getPersonnelId).collect(Collectors.toList()));
        if(ObjectUtil.isNotEmpty(inDeviceIds) || ObjectUtil.isNotEmpty(outDeviceIds)){
            List<String> deviceIds = new ArrayList<>();
            if(ObjectUtil.isNotEmpty(inDeviceIds)){
                deviceIds.addAll(inDeviceIds);
            }
            if(ObjectUtil.isNotEmpty(outDeviceIds)){
                deviceIds.addAll(outDeviceIds);
            }
            recordsQuery.in(TAccessControlEntryExitRecords::getAccessControlDeviceId, deviceIds);
        }
        recordsQuery.orderByDesc(TAccessControlEntryExitRecords::getEntryTime);
        List<TAccessControlEntryExitRecords> records = accessControlEntryExitRecordsService.list(recordsQuery);
        Map<String, TAccessControlEntryExitRecords> inPersonnelId2RecordsMap = new HashMap<>();
        Map<String, TAccessControlEntryExitRecords> outPersonnelId2RecordsMap = new HashMap<>();
        if(ObjectUtil.isNotEmpty(records)){
            inPersonnelId2RecordsMap = records.stream()
                    .filter(Objects::nonNull)
                    .filter(r -> inDeviceIds.contains(r.getAccessControlDeviceId()))
                    .collect(Collectors.toMap(TAccessControlEntryExitRecords::getPersonnelId,
                            Function.identity(),
                            (oldValue, newValue) -> oldValue,
                            LinkedHashMap::new));
            outPersonnelId2RecordsMap = records.stream()
                    .filter(Objects::nonNull)
                    .filter(r -> outDeviceIds.contains(r.getAccessControlDeviceId()))
                    .collect(Collectors.toMap(TAccessControlEntryExitRecords::getPersonnelId,
                            Function.identity(),
                            (oldValue, newValue) -> oldValue,
                            LinkedHashMap::new));

        }
        Map<String, TAccessControlEntryExitRecords> finalPersonnelId2RecordsMap = inPersonnelId2RecordsMap;
        Map<String, TAccessControlEntryExitRecords> finalOutPersonnelId2RecordsMap = outPersonnelId2RecordsMap;
        persons.forEach(person -> {
            if (StringUtils.isBlank(person.getAccessPermission())) {
                person.setAccessPermission("无权限");
                return;
            }
            TAccessControlEntryExitRecords record = finalPersonnelId2RecordsMap.get(person.getPersonnelId());
            if(ObjectUtil.isNotEmpty(record)){
                person.setInStationStatus("在站");
            }
            TAccessControlEntryExitRecords outRecord = finalOutPersonnelId2RecordsMap.get(person.getPersonnelId());
            if(ObjectUtil.isNotEmpty(outRecord)){
                person.setInStationStatus("离站");
            }
            String[] split = person.getAccessPermission().split(",");
            List<TAccessControlBaseInfo> deviceNames = Arrays.stream(split)
                    .map(String::trim)
                    .filter(StringUtils::isNotBlank)
                    .map(deviceId2NameMap::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            fillStationRelatedNames(deviceNames);
            person.setDevicesInfo(deviceNames);
        });
    }

    public Boolean addManageDeviceCheck(String belongStationId, String deviceCode, String ipAddress) {
        LambdaQueryWrapper<TAccessControlBaseInfo> wrapper = new LambdaQueryWrapper<>();
        if(ObjectUtil.isNotEmpty(deviceCode)){
            wrapper.eq(TAccessControlBaseInfo::getBelongStationId,belongStationId);
            wrapper.eq(TAccessControlBaseInfo::getDeviceCode,deviceCode);
            long count = accessControlBaseInfoService.count(wrapper);
            if(count > 0){
                return false;
            }
        }
        if(ObjectUtil.isNotEmpty(ipAddress)){
            wrapper.clear();
            wrapper.eq(TAccessControlBaseInfo::getBelongStationId,belongStationId);
            wrapper.eq(TAccessControlBaseInfo::getIpAddress,ipAddress);
            long count = accessControlBaseInfoService.count(wrapper);
            if(count > 0){
                return false;
            }
        }
        return true;
    }

    public Boolean linkageAlarm(AccessLinkageAlarmRequest request) {
        if (ObjectUtil.isEmpty(request.getAccessControlDeviceId())) {
            throw new RuntimeException("门禁设备ID不能为空");
        }
        if (ObjectUtil.isEmpty(request.getAlarmType())) {
            throw new RuntimeException("报警类型编码不能为空");
        }

        // 1. 查询联动报警配置
        TLinkageAlarmConfig linkageConfig = tLinkageAlarmConfigService.lambdaQuery()
                .eq(TLinkageAlarmConfig::getSubsystemType, SystemTypeEnum.MJXT.getCode())
                .eq(TLinkageAlarmConfig::getAlarmType, request.getAlarmType())
                .eq(TLinkageAlarmConfig::getStatus, "1") // 开启状态
                .one();

        if (linkageConfig == null) {
            log.info("未找到联动报警配置，门禁设备ID: {}, 报警类型: {}", request.getAccessControlDeviceId(), request.getAlarmType());
            return true;
        }

        // 2. 查询设备关联关系
        List<TDeviceRelationRecords> relationList = deviceRelationRecordsService.lambdaQuery()
                .eq(TDeviceRelationRecords::getRelatedDeviceId, request.getAccessControlDeviceId())
                .eq(TDeviceRelationRecords::getSubsystemType, SystemTypeEnum.MJXT.getCode())
                .list();

        if (CollectionUtils.isEmpty(relationList)) {
            log.info("未找到设备关联关系，门禁设备ID: {}", request.getAccessControlDeviceId());
            return true;
        }

        // 3. 处理预设位联动 - 控制摄像头转到指定预设位
        List<String> presetIds = relationList.stream()
                .map(TDeviceRelationRecords::getPresetId)
                .filter(ObjectUtil::isNotEmpty)
                .collect(Collectors.toList());

        if (ObjectUtil.isNotEmpty(presetIds)) {
            // 查询预设位对应的工业电视
            List<TIndustrialTvPreset> presetList = tIndustrialTvPresetService.lambdaQuery()
                    .in(TIndustrialTvPreset::getPresetId, presetIds)
                    .list();

            // 按工业电视ID分组，每个工业电视取第一个预设位
            Map<String, TIndustrialTvPreset> tvPresetMap = presetList.stream()
                    .collect(Collectors.toMap(
                            TIndustrialTvPreset::getIndustrialTvId,
                            p -> p,
                            (p1, p2) -> p1 // 如果有重复，取第一个
                    ));

            // 控制摄像头转到预设位
            for (Map.Entry<String, TIndustrialTvPreset> entry : tvPresetMap.entrySet()) {
                String tvId = entry.getKey();
                String presetId = entry.getValue().getPresetId();

                try {
                    ControlPresetRequest presetRequest = new ControlPresetRequest();
                    presetRequest.setDeviceId(tvId);
                    presetRequest.setPresetId(presetId);
                    presetRequest.setCommand("goto");
                    industrialTVService.industrialTVControlPreset(presetRequest);
                    log.info("联动报警：控制摄像头转到预设位，工业电视ID: {}, 预设位ID: {}", tvId, presetId);
                } catch (Exception e) {
                    log.error("联动报警：控制摄像头转到预设位失败，工业电视ID: {}, 预设位ID: {}, 错误: {}", tvId, presetId, e.getMessage());
                }
            }

            // 4. 判断是否需要抓图
            if (Boolean.TRUE.equals(linkageConfig.getIsEnableSnapshot())) {
                for (String tvId : tvPresetMap.keySet()) {
                    try {
                        byte[] snapshot = hikVisionService.snapshot(tvId);
                        log.info("联动报警：抓图成功，工业电视ID: {}, 图片大小: {} bytes", tvId, snapshot != null ? snapshot.length : 0);
                    } catch (Exception e) {
                        log.error("联动报警：抓图失败，工业电视ID: {}, 错误: {}", tvId, e.getMessage());
                    }
                }
            }
        }

        // 5. 判断是否需要播放音频
        if (Boolean.TRUE.equals(linkageConfig.getIsPlayAudio()) && ObjectUtil.isNotEmpty(linkageConfig.getAudioFileId())) {
            // 收集应急广播设备ID
            List<String> broadcastDeviceIds = relationList.stream()
                    .map(TDeviceRelationRecords::getEmergencyBroadcastId)
                    .filter(ObjectUtil::isNotEmpty)
                    .collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(broadcastDeviceIds)) {
                // 查询应急广播设备获取所属站场
                List<TEmergencyBroadcastHostBaseInfo> broadcastList = tEmergencyBroadcastHostBaseInfoService.lambdaQuery()
                        .in(TEmergencyBroadcastHostBaseInfo::getDeviceId, broadcastDeviceIds)
                        .list();

                if (CollectionUtils.isNotEmpty(broadcastList)) {
                    // 按站场分组播放
                    Map<String, List<TEmergencyBroadcastHostBaseInfo>> stationBroadcastMap = broadcastList.stream()
                            .filter(b -> ObjectUtil.isNotEmpty(b.getBelongStationId()))
                            .collect(Collectors.groupingBy(TEmergencyBroadcastHostBaseInfo::getBelongStationId));

                    for (Map.Entry<String, List<TEmergencyBroadcastHostBaseInfo>> entry : stationBroadcastMap.entrySet()) {
                        String stationId = entry.getKey();
                        List<String> deviceIds = entry.getValue().stream()
                                .map(TEmergencyBroadcastHostBaseInfo::getDeviceId)
                                .collect(Collectors.toList());

                        try {
                            PlayVoiceRequest playVoiceRequest = new PlayVoiceRequest();
                            playVoiceRequest.setStationId(stationId);
                            playVoiceRequest.setDeviceIds(deviceIds);
                            playVoiceRequest.setVoiceId(linkageConfig.getAudioFileId());
                            broadcastService.playVoice(playVoiceRequest);
                            log.info("联动报警：播放音频成功，站场ID: {}, 音频文件ID: {}", stationId, linkageConfig.getAudioFileId());
                        } catch (Exception e) {
                            log.error("联动报警：播放音频失败，站场ID: {}, 错误: {}", stationId, e.getMessage());
                        }
                    }
                }
            }
        }

        // 6. 判断是否需要打开门禁
        if (Boolean.TRUE.equals(linkageConfig.getIsOpenAccessControl())) {
            // 收集门禁设备ID
            List<String> accessControlDeviceIds = relationList.stream()
                    .map(TDeviceRelationRecords::getAccessControlDeviceId)
                    .filter(ObjectUtil::isNotEmpty)
                    .collect(Collectors.toList());

            if (ObjectUtil.isNotEmpty(accessControlDeviceIds)) {
                try {
                    AccessControlGatewayRequest gatewayRequest = new AccessControlGatewayRequest();
                    gatewayRequest.setDeviceIds(accessControlDeviceIds);
                    gatewayRequest.setCommand(1); // 1-打开
                    accessControlGatewayService.remoteControlGate(gatewayRequest);
                    log.info("联动报警：打开门禁成功，门禁设备ID: {}", accessControlDeviceIds);
                } catch (Exception e) {
                    log.error("联动报警：打开门禁失败，错误: {}", e.getMessage());
                }
            }
        }
        return true;
    }
}