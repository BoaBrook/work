package cn.stylefeng.guns.modular.workarea.sync;

import cn.stylefeng.guns.database.service.TWorkareaBaseInfoService;
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
 * 组织机构删除时同步删除作业区数据。
 */
@Slf4j
@Component
public class WorkareaOrgDeleteSyncCallback implements RemoveOrgCallbackApi {

    @Resource
    private HrOrganizationService sysHrOrganizationService;

    @Resource
    private TWorkareaBaseInfoService tWorkareaBaseInfoService;

    @Override
    public void validateHaveOrgBind(Set<Long> beRemovedOrgIdList) {
    }

    @Override
    public void removeOrgAction(Set<Long> beRemovedOrgIdList) {
        if (beRemovedOrgIdList == null || beRemovedOrgIdList.isEmpty()) {
            return;
        }

        Set<String> workareaIds = sysHrOrganizationService.listByIds(beRemovedOrgIdList).stream()
                .filter(Objects::nonNull)
                .filter(org -> Integer.valueOf(2).equals(org.getIsSecondaryOrg()))
                .map(HrOrganization::getOrgId)
                .map(String::valueOf)
                .collect(Collectors.toSet());

        if (workareaIds.isEmpty()) {
            return;
        }

        tWorkareaBaseInfoService.removeByIds(workareaIds);
        log.info("组织机构删除同步作业区完成，workareaIds={}", workareaIds);
    }
}

