package cn.stylefeng.guns.modular.accesscontrol.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.stylefeng.guns.database.entity.TAccessControlBaseInfo;
import cn.stylefeng.guns.database.entity.TAccessControlEntryExitRecords;
import cn.stylefeng.guns.database.entity.TAccessControlPersonnelBaseInfo;
import cn.stylefeng.guns.database.service.TAccessControlBaseInfoService;
import cn.stylefeng.guns.database.service.TAccessControlEntryExitRecordsService;
import cn.stylefeng.guns.database.service.TAccessControlPersonnelBaseInfoService;
import cn.stylefeng.guns.modular.accesscontrol.request.PersonalManageRequest;
import cn.stylefeng.guns.modular.accesscontrol.response.InStationStaffCountResponse;
import cn.stylefeng.guns.modular.accesscontrol.response.OnlineStateResponse;
import cn.stylefeng.guns.modular.accesscontrol.response.StaffCountResponse;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;
import cn.stylefeng.roses.kernel.rule.pojo.response.ErrorResponseData;
import cn.stylefeng.roses.kernel.rule.pojo.response.ResponseData;
import cn.stylefeng.roses.kernel.rule.pojo.response.SuccessResponseData;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AccessControlService {

    @Resource
    private TAccessControlBaseInfoService tAccessControlBaseInfoService;
    @Resource
    private TAccessControlEntryExitRecordsService tAccessControlEntryExitRecordsService;
    @Resource
    private TAccessControlPersonnelBaseInfoService tAccessControlPersonnelBaseInfoService;

    public OnlineStateResponse getScreenAccessControl(String belongStationId) {
        List<TAccessControlBaseInfo> list = getAccessControlBaseInfo(belongStationId);
        if(ObjectUtil.isEmpty(list)){
            return new OnlineStateResponse();
        }
        int sum = list.stream().mapToInt(it -> it.getState() == 1 ? 1 : 0).sum();
        return new OnlineStateResponse(list.size(), sum);
    }

    private List<TAccessControlBaseInfo> getAccessControlBaseInfo(String belongStationId) {
        LambdaQueryWrapper<TAccessControlBaseInfo> wrapper = new LambdaQueryWrapper<>();
        if(ObjectUtil.isNotEmpty(belongStationId)){
            wrapper.eq(TAccessControlBaseInfo::getBelongStationId, belongStationId);
        }
        return tAccessControlBaseInfoService.list(wrapper);
    }

    public List<TAccessControlEntryExitRecords> getRecordsOfAccess(String belongStationId) {
        // 1. 获取门禁基础信息
        List<TAccessControlBaseInfo> accessControlBaseInfo = getAccessControlBaseInfo(belongStationId);
        Date now = new Date();
        Date startTime = DateUtil.beginOfDay(now);
        Date endTime = DateUtil.endOfDay(now);

        // 2. 构建门禁进出记录查询条件
        LambdaQueryWrapper<TAccessControlEntryExitRecords> wrapper = new LambdaQueryWrapper<>();
        wrapper.between(TAccessControlEntryExitRecords::getEntryTime, startTime, endTime);

        // 如果门禁基础信息为空，返回空列表而非null（更符合集合返回规范）
        if (ObjectUtil.isEmpty(accessControlBaseInfo)) {
            return new ArrayList<>();
        }

        List<String> deviceIdList = accessControlBaseInfo.stream()
                .map(TAccessControlBaseInfo::getDeviceId)
                .collect(Collectors.toList());
        wrapper.in(TAccessControlEntryExitRecords::getAccessControlDeviceId, deviceIdList);
        wrapper.orderByDesc(TAccessControlEntryExitRecords::getEntryTime);

        // 3. 查询人员基础信息并构建映射关系（deviceId+personnelId -> name）
        LambdaQueryWrapper<TAccessControlPersonnelBaseInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(TAccessControlPersonnelBaseInfo::getAccessControlDeviceId, deviceIdList);
        List<TAccessControlPersonnelBaseInfo> personnelList = tAccessControlPersonnelBaseInfoService.list(queryWrapper);

        // 构建复合键映射：deviceId+personnelId 作为唯一标识，值为姓名
        Map<String, String> personnelNameMap = personnelList.stream()
                .collect(Collectors.toMap(
                        // 复合键：deviceId + "_" + personnelId（避免不同设备下personnelId重复）
                        p -> p.getAccessControlDeviceId() + "_" + p.getPersonnelId(),
                        TAccessControlPersonnelBaseInfo::getName,
                        // 解决重复键冲突（保留第一个）
                        (existing, replacement) -> existing
                ));

        // 4. 查询门禁进出记录并填充姓名
        List<TAccessControlEntryExitRecords> exitRecords = tAccessControlEntryExitRecordsService.list(wrapper);

        // 遍历记录，根据复合键匹配姓名并填充
        for (TAccessControlEntryExitRecords record : exitRecords) {
            // 构建当前记录的复合键
            String key = record.getAccessControlDeviceId() + "_" + record.getPersonnelId();
            // 获取姓名（无匹配则设为null或默认值）
            String personnelName = personnelNameMap.getOrDefault(key, null);
            // 填充姓名（需确保TAccessControlEntryExitRecords有setName方法）
            record.setName(personnelName);
        }

        return exitRecords;
    }

    public StaffCountResponse getScreenStaffCount(String belongStationId) {
        StaffCountResponse staffCountResponse = new StaffCountResponse();
        List<TAccessControlEntryExitRecords> recordsOfAccess = getRecordsOfAccess(belongStationId);
        if(recordsOfAccess == null){
            return null;
        }
        List<String> personalIdList = recordsOfAccess.stream().map(TAccessControlEntryExitRecords::getPersonnelId)
                .distinct().collect(Collectors.toList());
        LambdaQueryWrapper<TAccessControlPersonnelBaseInfo> queryWrapper = new LambdaQueryWrapper<>();
        if(ObjectUtil.isNotEmpty(personalIdList)){
            queryWrapper.in(TAccessControlPersonnelBaseInfo::getPersonnelId, personalIdList);
        }
        List<TAccessControlPersonnelBaseInfo> list = tAccessControlPersonnelBaseInfoService.list(queryWrapper);
        if(ObjectUtil.isEmpty(list) || list == null){
            return staffCountResponse;
        }
        Map<String, Long> groupedByPersonnelType = list.stream().filter(info -> info.getPersonnelType() != null)
                .collect(Collectors.groupingBy(TAccessControlPersonnelBaseInfo::getPersonnelType, Collectors.counting()));
        staffCountResponse.setInnerCount(groupedByPersonnelType.getOrDefault("站内人员",0L));
        staffCountResponse.setInnerVisitorCount(groupedByPersonnelType.getOrDefault("内部访客",0L));
        staffCountResponse.setOuterCount(groupedByPersonnelType.getOrDefault("外部访客",0L));
        staffCountResponse.setBuildCount(groupedByPersonnelType.getOrDefault("施工人员",0L));
        return staffCountResponse;
    }


    public Page<TAccessControlBaseInfo> getScreenDevicePage(String belongStationId, PageResult<TAccessControlBaseInfo> page) {
        LambdaQueryWrapper<TAccessControlBaseInfo> wrapper = new LambdaQueryWrapper<>();
        if(ObjectUtil.isNotEmpty(belongStationId)){
            wrapper.eq(TAccessControlBaseInfo::getBelongStationId, belongStationId);
        }
        Page<TAccessControlBaseInfo> pageable = Page.of(page.getPageNo(), page.getPageSize());
        return tAccessControlBaseInfoService.page(pageable,wrapper);
    }

    public InStationStaffCountResponse getStationStaffCount(String belongStationId) {
        InStationStaffCountResponse inStationStaffCountResponse = new InStationStaffCountResponse();
        List<TAccessControlEntryExitRecords> records = getRecordsOfAccess(belongStationId);
        if(ObjectUtil.isEmpty(records)){
            return inStationStaffCountResponse;
        }
        long totalCount = records.stream().filter(it -> ObjectUtil.isNotEmpty(it.getPersonnelId()))
                .map(TAccessControlEntryExitRecords::getPersonnelId).distinct().count();
        long outerCount = records.stream().filter(it -> "离站".equals(it.getInStationStatus())).
                map(TAccessControlEntryExitRecords::getPersonnelId).distinct().count();
        inStationStaffCountResponse.setOuterCount(outerCount);
        inStationStaffCountResponse.setTotalCount(totalCount);
        inStationStaffCountResponse.setInnerCount(totalCount - outerCount);
        List<String> personalIdList = records.stream().map(TAccessControlEntryExitRecords::getPersonnelId).distinct()
                .collect(Collectors.toList());
        LambdaQueryWrapper<TAccessControlPersonnelBaseInfo> queryWrapper = new LambdaQueryWrapper<>();
        if(ObjectUtil.isNotEmpty(personalIdList)){
            queryWrapper.in(TAccessControlPersonnelBaseInfo::getPersonnelId, personalIdList);
        }
        List<TAccessControlPersonnelBaseInfo> list = tAccessControlPersonnelBaseInfoService.list(queryWrapper);
        List<TAccessControlPersonnelBaseInfo> inner = list.stream().filter(it -> "站内人员".equals(it.getPersonnelType())).collect(Collectors.toList());
        List<TAccessControlPersonnelBaseInfo> innerVisitor = list.stream().filter(it -> "内部访客".equals(it.getPersonnelType())).collect(Collectors.toList());
        List<TAccessControlPersonnelBaseInfo> outer = list.stream().filter(it -> "外部访客".equals(it.getPersonnelType())).collect(Collectors.toList());
        List<TAccessControlPersonnelBaseInfo> build = list.stream().filter(it -> "施工人员".equals(it.getPersonnelType())).collect(Collectors.toList());
        inStationStaffCountResponse.setInner(inner);
        inStationStaffCountResponse.setInnerVisitor(innerVisitor);
        inStationStaffCountResponse.setOuter(outer);
        inStationStaffCountResponse.setBuild(build);
        return inStationStaffCountResponse;
    }

    public Page<TAccessControlPersonnelBaseInfo> getPersonnelPage(String belongStationId, String name, String personnelType, PageResult<TAccessControlPersonnelBaseInfo> page) {
        LambdaQueryWrapper<TAccessControlPersonnelBaseInfo> wrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotEmpty(belongStationId)) {
            wrapper.eq(TAccessControlPersonnelBaseInfo::getBelongStationId, belongStationId);
        }
        if (ObjectUtil.isNotEmpty(name)) {
            wrapper.like(TAccessControlPersonnelBaseInfo::getName, name);
        }
        if (ObjectUtil.isNotEmpty(personnelType)) {
            wrapper.eq(TAccessControlPersonnelBaseInfo::getPersonnelType, personnelType);
        }
        Page<TAccessControlPersonnelBaseInfo> pageable = Page.of(page.getPageNo(), page.getPageSize());
        Page<TAccessControlPersonnelBaseInfo> pageResult = tAccessControlPersonnelBaseInfoService.page(pageable, wrapper);
        if(ObjectUtil.isNotEmpty(pageResult) && ObjectUtil.isNotEmpty(pageResult.getRecords())){
            LambdaQueryWrapper<TAccessControlEntryExitRecords> recordsQuery = new LambdaQueryWrapper<>();
            for (TAccessControlPersonnelBaseInfo personnel : pageResult.getRecords()) {
                recordsQuery.clear();
                recordsQuery.eq(TAccessControlEntryExitRecords::getPersonnelId, personnel.getPersonnelId());
                recordsQuery.orderByDesc(TAccessControlEntryExitRecords::getEntryTime);
                recordsQuery.last(" LIMIT 1");
                TAccessControlEntryExitRecords records = tAccessControlEntryExitRecordsService.getOne(recordsQuery);
                if(ObjectUtil.isNotEmpty(records)){
                     personnel.setInStationStatus(records.getInStationStatus());
                }
            }
        }
        return pageResult;
    }

    public ResponseData<?> updatePersonnelPage(PersonalManageRequest request) {
        List<TAccessControlBaseInfo> list = getAccessControlBaseInfo(request.getBelongStationId());
        if(ObjectUtil.isEmpty(list)){
            return new SuccessResponseData<>();
        }
        TAccessControlPersonnelBaseInfo byId = tAccessControlPersonnelBaseInfoService.getById(request.getPersonnelId());
        List<String> inDevice;
        List<String> outDevice;
        TAccessControlEntryExitRecords entryExitRecords = new TAccessControlEntryExitRecords();
        entryExitRecords.setPersonnelId(request.getPersonnelId());
        entryExitRecords.setInStationStatus(request.getInStationStatus());
        entryExitRecords.setCreateTime(new Date());
        entryExitRecords.setEntryTime(new Date());
        entryExitRecords.setEntryExitType("在站".equals(request.getInStationStatus()) ? "进" : "出");
        entryExitRecords.setEntryMethod("人工设置");
        entryExitRecords.setImageAddress(byId.getFaceData());
        if("在站".equals(request.getInStationStatus())){
            inDevice = list.stream()
                    .filter(it -> ObjectUtil.isNotEmpty(it.getIsBigDoor()) && it.getIsBigDoor().contains("0"))
                    .map(TAccessControlBaseInfo::getDeviceId).
                    collect(Collectors.toList());
            if(ObjectUtil.isEmpty(inDevice)){
                return new ErrorResponseData<>("error", "进站大门设备未设置");
            }
            entryExitRecords.setAccessControlDeviceId(inDevice.get(0));
        }else{
            outDevice = list.stream()
                    .filter(it -> ObjectUtil.isNotEmpty(it.getIsBigDoor()) && it.getIsBigDoor().contains("1"))
                    .map(TAccessControlBaseInfo::getDeviceId).
                    collect(Collectors.toList());
            if(ObjectUtil.isEmpty(outDevice)){
                return new ErrorResponseData<>("error", "出站大门设备未设置");
            }
            entryExitRecords.setAccessControlDeviceId(outDevice.get(0));
        }
        tAccessControlEntryExitRecordsService.save(entryExitRecords);
        return new SuccessResponseData<>();
    }
}
