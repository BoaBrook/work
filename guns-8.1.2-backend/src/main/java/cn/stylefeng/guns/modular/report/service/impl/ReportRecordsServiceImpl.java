package cn.stylefeng.guns.modular.report.service.impl;

import cn.stylefeng.guns.database.entity.TStationBaseInfo;
import cn.stylefeng.guns.database.service.TStationBaseInfoService;
import cn.stylefeng.guns.modular.nodeSystem.dto.JobPlanDTO;
import cn.stylefeng.guns.modular.nodeSystem.service.NodeSystemService;
import cn.stylefeng.guns.modular.report.entity.ReportRecords;
import cn.stylefeng.guns.modular.report.entity.ReportRecords.ApprovalRecord;
import cn.stylefeng.guns.modular.report.mapper.ReportRecordsMapper;
import cn.stylefeng.guns.modular.report.request.ReportRecordsListRequest;
import cn.stylefeng.guns.modular.report.service.ReportRecordsService;
import cn.stylefeng.roses.kernel.db.api.factory.PageResultFactory;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReportRecordsServiceImpl extends ServiceImpl<ReportRecordsMapper, ReportRecords> implements ReportRecordsService {

    /**
     * 报备记录状态：待上报
     */
    private static final Integer REPORT_STATUS_PENDING = 0;

    /**
     * 报备记录状态：审核中
     */
    private static final Integer REPORT_STATUS_REVIEWING = 1;

    /**
     * 报备记录状态：已通过
     */
    private static final Integer REPORT_STATUS_APPROVED = 2;

    /**
     * 报备记录状态：已驳回
     */
    private static final Integer REPORT_STATUS_REJECTED = 3;

    /**
     * 作业计划反馈状态：已确认
     */
    private static final Integer JOB_PLAN_STATUS_CONFIRMED = 1;

    /**
     * 作业计划反馈状态：驳回
     */
    private static final Integer JOB_PLAN_STATUS_REJECTED = 2;

    @Resource
    private ReportRecordsMapper reportRecordsMapper;

    @Resource
    private ReportRecordsService selfService;

    @Resource
    private TStationBaseInfoService stationBaseInfoService;

    @Resource
    private NodeSystemService nodeSystemService;

    @Override
    public PageResult<ReportRecords> list(ReportRecordsListRequest request) {
        Page<ReportRecords> page = Page.of(request.getPageNo(), request.getPageSize());
        LambdaQueryWrapper<ReportRecords> queryWrapper = new LambdaQueryWrapper<>();

        if (request.getStatus() != null) {
            queryWrapper.eq(ReportRecords::getStatus, request.getStatus());
        }
        if (StringUtils.isNotEmpty(request.getReportContent())) {
            queryWrapper.like(ReportRecords::getReportContent, request.getReportContent());
        }
        if (request.getStartTime() != null) {
            queryWrapper.ge(ReportRecords::getExpectedRectificationStartTime, request.getStartTime());
        }
        if (request.getEndTime() != null) {
            queryWrapper.le(ReportRecords::getExpectedRectificationStartTime, request.getEndTime());
        }

        queryWrapper.orderByDesc(ReportRecords::getCreateTime);
        Page<ReportRecords> result = selfService.page(page, queryWrapper);

        List<ReportRecords> records = result.getRecords();
        if (CollectionUtils.isNotEmpty(records)) {
            for (ReportRecords record : records) {
                setLatestApprovalRecord(record);
            }
        }

        return PageResultFactory.createPageResult(result);
    }

    @Override
    public boolean reportSave(ReportRecords reportRecords) {
        return selfService.saveOrUpdate(reportRecords);
    }

    @Override
    public boolean deleteBatch(List<String> ids) {
        return selfService.removeBatchByIds(ids);
    }

    @Override
    public boolean submitBatch(List<String> ids) {
        if (ObjectUtils.isEmpty(ids)) {
            return false;
        }
        LambdaQueryWrapper<ReportRecords> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ReportRecords::getReportId, ids);
        queryWrapper.eq(ReportRecords::getStatus, REPORT_STATUS_PENDING);
        List<ReportRecords> reportRecordsList = selfService.list(queryWrapper);
        if (CollectionUtils.isEmpty(reportRecordsList)) {
            return false;
        }

        Date currentTime = new Date();
        sendJobPlansBatch(reportRecordsList, currentTime);

        List<String> reportIds = reportRecordsList.stream()
            .map(ReportRecords::getReportId)
            .collect(Collectors.toList());
        LambdaUpdateWrapper<ReportRecords> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(ReportRecords::getReportId, reportIds);
        updateWrapper.set(ReportRecords::getStatus, REPORT_STATUS_REVIEWING);
        updateWrapper.set(ReportRecords::getReportTime, currentTime);
        return selfService.update(updateWrapper);
    }

    public void handleJobPlanFeedback(String jobId, Integer status, String rejectReason, String checkerName, String commandTime) {
        try {
            if (StringUtils.isEmpty(jobId)) {
                log.warn("Job ID is empty, skip handling job plan feedback");
                return;
            }
            ReportRecords reportRecord = selfService.getById(jobId);
            if (reportRecord == null) {
                log.warn("Report record not found, jobId: {}", jobId);
                return;
            }

            ApprovalRecord approvalRecord = new ApprovalRecord();
            approvalRecord.setCheckerName(checkerName);
            approvalRecord.setRejectReason(rejectReason);
            approvalRecord.setReviewTime(commandTime);
            approvalRecord.setStatus(status);

            List<ApprovalRecord> approvalRecords = new ArrayList<>();
            if (StringUtils.isNotEmpty(reportRecord.getFlowContent())) {
                try {
                    approvalRecords = JSON.parseArray(reportRecord.getFlowContent(), ReportRecords.ApprovalRecord.class);
                } catch (Exception e) {
                    log.warn("Failed to parse existing approval records, will create new list, jobId: {}", jobId, e);
                    approvalRecords = new ArrayList<>();
                }
            }
            approvalRecords.add(approvalRecord);
            String flowContentJson = JSON.toJSONString(approvalRecords);

            Integer mappedStatus = null;
            if (status != null) {
                if (JOB_PLAN_STATUS_CONFIRMED.equals(status)) {
                    mappedStatus = REPORT_STATUS_APPROVED;
                } else if (JOB_PLAN_STATUS_REJECTED.equals(status)) {
                    mappedStatus = REPORT_STATUS_REJECTED;
                }
            }

            LambdaUpdateWrapper<ReportRecords> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(ReportRecords::getReportId, jobId);
            updateWrapper.set(ReportRecords::getFlowContent, flowContentJson);
            if (mappedStatus != null) {
                updateWrapper.set(ReportRecords::getStatus, mappedStatus);
            }
            selfService.update(updateWrapper);

        } catch (Exception e) {
            log.error("Error handling job plan feedback, jobId: {}", jobId, e);
        }
    }

    private void setLatestApprovalRecord(ReportRecords record) {
        if (ObjectUtils.isEmpty(record.getFlowContent())) {
            return;
        }
        try {
            List<ApprovalRecord> approvalRecords = JSON.parseArray(record.getFlowContent(), ApprovalRecord.class);
            if (ObjectUtils.isEmpty(approvalRecords)) {
                return;
            }
            ApprovalRecord latestRecord = approvalRecords.stream()
                .filter(ar -> ObjectUtils.isNotEmpty(ar.getReviewTime()))
                .max(Comparator.comparing(ApprovalRecord::getReviewTime))
                .orElse(null);
            record.setApprovalRecord(latestRecord);
        } catch (Exception e) {
            log.warn("Failed to parse approval records for reportId: {}", record.getReportId(), e);
        }
    }

    private void sendJobPlansBatch(List<ReportRecords> reportRecordsList, Date reportTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        List<String> stationIds = reportRecordsList.stream()
            .map(ReportRecords::getStationId)
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toList());
        List<TStationBaseInfo> stationInfoList = stationBaseInfoService.listByIds(stationIds);
        Map<String, TStationBaseInfo> stationInfoMap = stationInfoList.stream()
            .collect(Collectors.toMap(TStationBaseInfo::getStationId, station -> station, (k1, k2) -> k1));

        for (ReportRecords reportRecord : reportRecordsList) {
            String stationId = reportRecord.getStationId();
            TStationBaseInfo stationInfo = stationInfoMap.get(stationId);
            if (stationInfo == null) {
                log.warn("Station info not found, stationId: {}, reportId: {}", reportRecord.getStationId(), reportRecord.getReportId());
                throw new RuntimeException("上报失败-缺少站点信息: " + stationId);
            }
            JobPlanDTO jobPlan = new JobPlanDTO();
            jobPlan.setJobId(reportRecord.getReportId());
            jobPlan.setNodeCode(nodeSystemService.getNodeCode());
            // TODO 待确认获取编码
            jobPlan.setPipelineCode(stationInfo.getBelongPipeline());
            jobPlan.setWorkAreaCode(stationInfo.getBelongOperationArea());
            jobPlan.setStationCode(stationInfo.getStationCode());
            jobPlan.setReporter(reportRecord.getContactPerson());
            jobPlan.setReportTime(dateFormat.format(reportTime));
            jobPlan.setReportContent(reportRecord.getReportContent());
            jobPlan.setOperationStartTime(dateFormat.format(reportRecord.getExpectedRectificationStartTime()));
            jobPlan.setOperationEndTime(dateFormat.format(reportRecord.getExpectedRectificationEndTime()));
            boolean success = nodeSystemService.sendJobPlan(jobPlan);
            if (!success) {
                log.error("Failed to send job plan, reportId: {}", reportRecord.getReportId());
                throw new RuntimeException("上报失败");
            }
        }
    }

}

