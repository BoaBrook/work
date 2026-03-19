package cn.stylefeng.guns.modular.safeday.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.stylefeng.guns.core.utils.PageToPageResultUtils;
import cn.stylefeng.guns.database.entity.TStationAreaBaseInfo;
import cn.stylefeng.guns.database.entity.TStationBaseInfo;
import cn.stylefeng.guns.database.entity.TValveChamberBaseInfo;
import cn.stylefeng.guns.database.service.TStationAreaBaseInfoService;
import cn.stylefeng.guns.database.service.TStationBaseInfoService;
import cn.stylefeng.guns.database.service.TValveChamberBaseInfoService;
import cn.stylefeng.guns.modular.safeday.entity.SafeDayConfigRow;
import cn.stylefeng.guns.modular.safeday.entity.SafeDayPageRequest;
import cn.stylefeng.guns.modular.safeday.entity.SafeDaysParam;
import cn.stylefeng.guns.modular.safeday.entity.TSafeOperationDays;
import cn.stylefeng.guns.modular.safeday.mapper.TSafeOperationDaysMapper;
import cn.stylefeng.guns.modular.safeday.service.TSafeOperationDaysService;
import cn.stylefeng.roses.kernel.auth.api.context.LoginContext;
import cn.stylefeng.roses.kernel.auth.api.pojo.login.LoginUser;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;
import cn.stylefeng.roses.kernel.sys.modular.org.entity.HrOrganization;
import cn.stylefeng.roses.kernel.sys.modular.org.service.HrOrganizationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 安全运行天数 Service实现
 *
 * @author system
 * @date 2026-01-19
 */
@Service
@Slf4j
public class TSafeOperationDaysServiceImpl extends ServiceImpl<TSafeOperationDaysMapper, TSafeOperationDays> implements TSafeOperationDaysService {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Resource
    private TStationBaseInfoService stationBaseInfoService;
    @Resource
    private HrOrganizationService sysHrOrganizationService;
    @Resource
    private TStationAreaBaseInfoService stationAreaBaseInfoService;
    @Resource
    private TValveChamberBaseInfoService valveChamberBaseInfoService;

    /**
     * 根据站点ID获取当前安全运行天数
     * @param stationId 站点ID
     * @return 当前安全运行天数
     */
    @Override
    public Map<String, Object> getCurrentSafeDaysByStationId(String stationId) {
        Map<String, Object> result = new HashMap<>();
        if (StrUtil.isBlank(stationId)) {
            result.put("days", 0);
            result.put("definition", null);
            return result;
        }
        LambdaQueryWrapper<TSafeOperationDays> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TSafeOperationDays::getStationId, stationId)
                .orderByDesc(TSafeOperationDays::getModifyTime)
                .last("limit 1");
        TSafeOperationDays safeDays = this.getOne(queryWrapper);

        if (safeDays != null && safeDays.getSafetyOperationStartDate() != null) {
            long diff = new Date().getTime() - safeDays.getSafetyOperationStartDate().getTime();
            int days = (int) (diff / (1000 * 60 * 60 * 24)) + 1;
            result.put("days", days);
            result.put("definition", safeDays.getDefinition());
        } else {
            result.put("days", 0);
            result.put("definition", null);
        }

        return result;
    }

    @Override
    public PageResult<SafeDayConfigRow> pageConfigList(SafeDayPageRequest request) {
        int pageNo = request.getPageNo() != null ? request.getPageNo() : 1;
        int pageSize = request.getPageSize() != null ? request.getPageSize() : 10;

        Page<TStationBaseInfo> page = stationBaseInfoService.page(
                new Page<>(pageNo, pageSize),
                new LambdaQueryWrapper<TStationBaseInfo>().orderByAsc(TStationBaseInfo::getStationId)
        );
        List<TStationBaseInfo> stations = page.getRecords();
        if (stations == null || stations.isEmpty()) {
            Page<SafeDayConfigRow> empty = new Page<>(pageNo, pageSize, page.getTotal());
            empty.setRecords(Collections.emptyList());
            return PageToPageResultUtils.pageToPageResult(empty);
        }

        List<String> stationIds = stations.stream().map(TStationBaseInfo::getStationId).collect(Collectors.toList());

        List<String> orgIdStrs = stations.stream()
                .map(TStationBaseInfo::getBelongOperationArea)
                .filter(StrUtil::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
        List<Long> orgIds = orgIdStrs.stream()
                .map(TSafeOperationDaysServiceImpl::parseLongOrNull)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        final Map<String, String> orgIdToName = orgIds.isEmpty() ? new HashMap<>()
                : sysHrOrganizationService.listByIds(orgIds).stream()
                        .collect(Collectors.toMap(org -> String.valueOf(org.getOrgId()), HrOrganization::getOrgName, (a, b) -> a));

        List<TSafeOperationDays> safeDaysList = this.list(new LambdaQueryWrapper<TSafeOperationDays>()
                .in(TSafeOperationDays::getStationId, stationIds)
                .orderByDesc(TSafeOperationDays::getModifyTime));
        Map<String, TSafeOperationDays> stationIdToSafeDays = safeDaysList.stream()
                .collect(Collectors.toMap(TSafeOperationDays::getStationId, d -> d, (a, b) -> a));

        List<TStationAreaBaseInfo> areas = stationAreaBaseInfoService.list(
                new LambdaQueryWrapper<TStationAreaBaseInfo>().in(TStationAreaBaseInfo::getBelongStationId, stationIds));
        Map<String, List<String>> stationIdToAreaIds = areas.stream()
                .filter(a -> a.getBelongStationId() != null && a.getAreaId() != null)
                .collect(Collectors.groupingBy(TStationAreaBaseInfo::getBelongStationId,
                        Collectors.mapping(TStationAreaBaseInfo::getAreaId, Collectors.toList())));
        List<String> allAreaIds = areas.stream().map(TStationAreaBaseInfo::getAreaId).filter(Objects::nonNull).distinct().collect(Collectors.toList());

        List<TValveChamberBaseInfo> valveChambers = allAreaIds.isEmpty() ? Collections.emptyList()
                : valveChamberBaseInfoService.list(new LambdaQueryWrapper<TValveChamberBaseInfo>()
                        .select(TValveChamberBaseInfo::getBelongStationAreaId)
                        .in(TValveChamberBaseInfo::getBelongStationAreaId, allAreaIds));
        Map<String, Long> stationIdToValveCount = new HashMap<>();
        for (String sid : stationIds) {
            List<String> areaIds = stationIdToAreaIds.getOrDefault(sid, Collections.emptyList());
            long count = areaIds.isEmpty() ? 0
                    : valveChambers.stream().filter(v -> areaIds.contains(v.getBelongStationAreaId())).count();
            stationIdToValveCount.put(sid, count);
        }

        List<SafeDayConfigRow> rows = stations.stream().map(station -> {
            SafeDayConfigRow row = new SafeDayConfigRow();
            row.setStationId(station.getStationId());
            row.setStationName(station.getStationName());
            row.setOrgName(orgIdToName.get(station.getBelongOperationArea()));
            row.setValveChamberCount(stationIdToValveCount.getOrDefault(station.getStationId(), 0L).intValue());
            TSafeOperationDays safeDays = stationIdToSafeDays.get(station.getStationId());
            if (safeDays != null) {
                row.setSafetyOperationStartDate(safeDays.getSafetyOperationStartDate());
                row.setDefinition(safeDays.getDefinition());
            }
            return row;
        }).collect(Collectors.toList());

        Page<SafeDayConfigRow> resultPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        resultPage.setRecords(rows);
        return PageToPageResultUtils.pageToPageResult(resultPage);
    }

    private static Long parseLongOrNull(String s) {
        if (s == null || s.isEmpty()) return null;
        try {
            return Long.parseLong(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveConfig(SafeDaysParam params) {
        try {
            String stationId = params.getStationId();
            String initialDateStr = params.getInitialDate();

            if (StrUtil.isBlank(stationId)) {
                log.error("站点ID不能为空");
                return false;
            }
            if (StrUtil.isBlank(initialDateStr)) {
                log.error("安全运行开始日期不能为空");
                return false;
            }

            Date initialDate = sdf.parse(initialDateStr);
            if (initialDate.after(new Date())) {
                log.error("安全运行开始日期不能晚于当前日期");
                return false;
            }

            String modifyUser = "";
            try {
                LoginUser loginUser = LoginContext.me().getLoginUserNullable();
                if (loginUser != null && StrUtil.isNotBlank(loginUser.getAccount())) {
                    modifyUser = loginUser.getAccount();
                }
            } catch (Exception ignored) {
            }

            TSafeOperationDays existingRecord = this.getOne(
                    new LambdaQueryWrapper<TSafeOperationDays>().eq(TSafeOperationDays::getStationId, stationId));

            if (existingRecord != null) {
                existingRecord.setSafetyOperationStartDate(initialDate);
                existingRecord.setModifyTime(new Date());
                existingRecord.setModifyUser(modifyUser);
                existingRecord.setDefinition(params.getDefinition());
                return this.updateById(existingRecord);
            } else {
                TSafeOperationDays newSafeDays = new TSafeOperationDays();
                newSafeDays.setStationId(stationId);
                newSafeDays.setSafetyOperationStartDate(initialDate);
                newSafeDays.setModifyTime(new Date());
                newSafeDays.setModifyUser(modifyUser);
                newSafeDays.setDefinition(params.getDefinition());
                return this.save(newSafeDays);
            }
        } catch (Exception e) {
            log.error("保存安全运行天数配置失败", e);
            return false;
        }
    }
}