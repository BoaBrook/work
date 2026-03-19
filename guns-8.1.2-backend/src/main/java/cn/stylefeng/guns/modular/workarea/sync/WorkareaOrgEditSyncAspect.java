package cn.stylefeng.guns.modular.workarea.sync;

import cn.stylefeng.guns.core.utils.TransactionUtils;
import cn.stylefeng.guns.database.entity.TWorkareaBaseInfo;
import cn.stylefeng.guns.database.service.TWorkareaBaseInfoService;
import cn.stylefeng.roses.kernel.sys.modular.org.entity.HrOrganization;
import cn.stylefeng.roses.kernel.sys.modular.org.pojo.request.HrOrganizationRequest;
import cn.stylefeng.roses.kernel.sys.modular.org.service.HrOrganizationService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 组织机构新增、编辑后同步作业区表。
 */
@Slf4j
@Aspect
@Component
public class WorkareaOrgEditSyncAspect {

    @Resource
    private HrOrganizationService hrOrganizationService;

    @Resource
    private TWorkareaBaseInfoService tWorkareaBaseInfoService;

    @Resource
    private TransactionUtils transactionUtils;

    /**
     * 编辑组织时同步作业区信息。
     */
    @Around("execution(* cn.stylefeng.roses.kernel.sys.modular.org.service.HrOrganizationService.edit(..)) && args(request)")
    public Object syncWorkareaAfterOrgEdit(ProceedingJoinPoint joinPoint, HrOrganizationRequest request) throws Throwable {
        return transactionUtils.executeInTransaction(() -> {
            try {
                if (request == null || request.getOrgId() == null) {
                    return joinPoint.proceed();
                }

                Long orgId = request.getOrgId();
                HrOrganization oldOrgTemp = hrOrganizationService.getById(orgId);
                HrOrganization oldOrg = new HrOrganization();
                BeanUtils.copyProperties(oldOrgTemp, oldOrg);

                Object result = joinPoint.proceed();

                HrOrganization newOrg = hrOrganizationService.getById(orgId);

                boolean oldIsWorkarea = isWorkareaOrg(oldOrg);
                boolean newIsWorkarea = isWorkareaOrg(newOrg);

                if (oldIsWorkarea && !newIsWorkarea) {
                    String workareaId = String.valueOf(orgId);
                    tWorkareaBaseInfoService.removeById(workareaId);
                    log.info("组织编辑取消作业区标识，同步删除作业区记录 orgId={}, workareaId={}", orgId, workareaId);
                    return result;
                }

                if (!newIsWorkarea) {
                    return result;
                }

                boolean nameChanged = !Objects.equals(oldOrg.getOrgName(), newOrg.getOrgName());
                boolean codeChanged = !Objects.equals(oldOrg.getOrgCode(), newOrg.getOrgCode());
                if (!nameChanged && !codeChanged && oldIsWorkarea) {
                    return result;
                }

                String workareaId = String.valueOf(orgId);
                TWorkareaBaseInfo workarea = new TWorkareaBaseInfo();
                workarea.setWorkareaId(workareaId);
                workarea.setWorkareaName(newOrg.getOrgName());
                workarea.setWorkareaCode(newOrg.getOrgCode());

                tWorkareaBaseInfoService.saveOrUpdate(workarea);
                log.info("组织编辑同步作业区成功，orgId={}, workareaId={}", orgId, workareaId);
                return result;
            }catch (Throwable e){
                log.error("组织编辑同步作业区异常", e);
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * 新增组织时同步作业区信息。
     */
    @Around("execution(* cn.stylefeng.roses.kernel.sys.modular.org.service.HrOrganizationService.add(..)) && args(request)")
    public Object syncWorkareaAfterOrgAdd(ProceedingJoinPoint joinPoint, HrOrganizationRequest request) throws Throwable {
        return transactionUtils.executeInTransaction(() -> {
            try {
                Object result = joinPoint.proceed();

                if (request == null || !Integer.valueOf(2).equals(request.getIsSecondaryOrg())) {
                    return result;
                }

                HrOrganization newOrg = hrOrganizationService.lambdaQuery()
                        .eq(HrOrganization::getOrgName, request.getOrgName())
                        .eq(HrOrganization::getOrgParentId, request.getOrgParentId())
                        .eq(HrOrganization::getOrgCode, request.getOrgCode())
                        .eq(HrOrganization::getIsSecondaryOrg, 2)
                        .orderByDesc(HrOrganization::getOrgId)
                        .last("limit 1")
                        .one();
                if (!isWorkareaOrg(newOrg)) {
                    return result;
                }

                String workareaId = String.valueOf(newOrg.getOrgId());
                TWorkareaBaseInfo workarea = new TWorkareaBaseInfo();
                workarea.setWorkareaId(workareaId);
                workarea.setWorkareaName(newOrg.getOrgName());
                workarea.setWorkareaCode(newOrg.getOrgCode());

                tWorkareaBaseInfoService.saveOrUpdate(workarea);
                log.info("组织新增同步作业区成功，orgId={}, workareaId={}", newOrg.getOrgId(), workareaId);
                return result;
            }catch (Throwable e){
                log.error("组织新增同步作业区异常", e);
                throw new RuntimeException(e);
            }
        });
    }

    private boolean isWorkareaOrg(HrOrganization org) {
        return org != null
                && Integer.valueOf(2).equals(org.getIsSecondaryOrg())
                && !"Y".equalsIgnoreCase(org.getDelFlag());
    }
}

