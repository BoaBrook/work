package cn.stylefeng.guns.modular.station.sync;

import cn.stylefeng.guns.database.service.TStationBaseInfoService;
import cn.stylefeng.roses.kernel.sys.api.callback.RemoveOrgCallbackApi;
import cn.stylefeng.roses.kernel.sys.modular.org.entity.HrOrganization;
import cn.stylefeng.roses.kernel.sys.modular.org.service.HrOrganizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 组织机构删除时同步删除站场数据。
 */
@Slf4j
@Component
public class StationOrgDeleteSyncCallback implements RemoveOrgCallbackApi {

    @Resource
    private HrOrganizationService sysHrOrganizationService;

    @Resource
    private TStationBaseInfoService tStationBaseInfoService;

    @Override
    public void validateHaveOrgBind(Set<Long> beRemovedOrgIdList) {
    }

    @Override
    public void removeOrgAction(Set<Long> beRemovedOrgIdList) {
        if (beRemovedOrgIdList == null || beRemovedOrgIdList.isEmpty()) {
            return;
        }

        Set<String> stationIds = sysHrOrganizationService.listByIds(beRemovedOrgIdList).stream()
                .filter(Objects::nonNull)
                .filter(org -> Integer.valueOf(1).equals(org.getIsSecondaryOrg()))
                .map(HrOrganization::getOrgId)
                .map(String::valueOf)
                .collect(Collectors.toSet());

        if (stationIds.isEmpty()) {
            return;
        }

        tStationBaseInfoService.removeByIds(stationIds);
        log.info("组织机构删除同步站场完成，stationIds={}", stationIds);
    }
}

