package cn.stylefeng.guns.modular.station.sync;

import cn.stylefeng.guns.core.utils.TransactionUtils;
import cn.stylefeng.guns.database.entity.TStationBaseInfo;
import cn.stylefeng.guns.database.service.TStationBaseInfoService;
import cn.stylefeng.roses.kernel.sys.modular.org.entity.HrOrganization;
import cn.stylefeng.roses.kernel.sys.modular.org.pojo.request.HrOrganizationRequest;
import cn.stylefeng.roses.kernel.sys.modular.org.service.HrOrganizationService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 组织机构编辑后同步站场表。
 */
@Slf4j
@Aspect
@Component
public class StationOrgEditSyncAspect {

    @Resource
    private HrOrganizationService sysHrOrganizationService;

    @Resource
    private TStationBaseInfoService tStationBaseInfoService;

    @Resource
    private TransactionUtils transactionUtils;

    @Around("execution(* cn.stylefeng.roses.kernel.sys.modular.org.service.HrOrganizationService.edit(..)) && args(request)")
    public Object syncStationAfterOrgEdit(ProceedingJoinPoint proceedingJoinPoint, HrOrganizationRequest request) throws Throwable {
        return transactionUtils.executeInTransaction(() -> {
            try{
                if (request == null || request.getOrgId() == null) {
                    return proceedingJoinPoint.proceed();
                }

                Long orgId = request.getOrgId();
                HrOrganization oldOrgTemp = sysHrOrganizationService.getById(orgId);
                HrOrganization oldOrg = new HrOrganization();
                BeanUtils.copyProperties(oldOrgTemp, oldOrg);

                Object result = proceedingJoinPoint.proceed();

                HrOrganization newOrg = sysHrOrganizationService.getById(orgId);

                boolean oldIsStation = isSecondaryStationOrg(oldOrg);
                boolean newIsStation = isSecondaryStationOrg(newOrg);

                if (oldIsStation && !newIsStation) {
                    String stationId = String.valueOf(orgId);
                    tStationBaseInfoService.removeById(stationId);
                    log.info("组织编辑取消站场标识，同步删除站场记录 orgId={}, stationId={}", orgId, stationId);
                    return result;
                }

                if (!newIsStation) {
                    return result;
                }

                boolean orgNameChanged = !Objects.equals(oldOrg == null ? null : oldOrg.getOrgName(), newOrg.getOrgName());
                boolean orgParentChanged = !Objects.equals(oldOrg == null ? null : oldOrg.getOrgParentId(), newOrg.getOrgParentId());
                boolean orgCodeChanged = !Objects.equals(oldOrg == null ? null : oldOrg.getOrgCode(), newOrg.getOrgCode());
                if (!orgNameChanged && !orgParentChanged && !orgCodeChanged) {
                    return result;
                }

                String stationId = String.valueOf(orgId);
                TStationBaseInfo station = tStationBaseInfoService.getById(stationId);
                if (station == null) {
                    return result;
                }

                tStationBaseInfoService.update(
                        Wrappers.<TStationBaseInfo>lambdaUpdate()
                                .eq(TStationBaseInfo::getStationId, stationId)
                                .set(TStationBaseInfo::getStationName, newOrg.getOrgName())
                                .set(TStationBaseInfo::getBelongOperationArea,
                                        newOrg.getOrgParentId() == null ? null : String.valueOf(newOrg.getOrgParentId()))
                                .set(TStationBaseInfo::getStationCode, newOrg.getOrgCode())
                );

                log.info("组织机构编辑同步站场成功，orgId={}, stationId={}", orgId, stationId);
                return result;
            } catch (Throwable e) {
                log.error("组织机构编辑同步站场异常", e);
                throw new RuntimeException(e);
            }
        });
    }

    private boolean isSecondaryStationOrg(HrOrganization org) {
        return org != null && Integer.valueOf(1).equals(org.getIsSecondaryOrg()) && !"Y".equalsIgnoreCase(org.getDelFlag());
    }
}

