package cn.stylefeng.guns.modular.alarmrecord.controller;

import cn.stylefeng.guns.database.entity.TAlarmResultRecords;
import cn.stylefeng.guns.modular.alarmrecord.dto.AlarmRecordBatchDisposalRequest;
import cn.stylefeng.guns.modular.alarmrecord.dto.AlarmRecordDisposalRequest;
import cn.stylefeng.guns.modular.alarmrecord.dto.AlarmRecordQueryRequest;
import cn.stylefeng.guns.modular.alarmrecord.dto.IndustrialTvWithVideoDTO;
import cn.stylefeng.guns.modular.alarmrecord.service.AlarmRecordService;
import cn.stylefeng.roses.kernel.rule.enums.ResBizTypeEnum;
import cn.stylefeng.roses.kernel.rule.pojo.response.ResponseData;
import cn.stylefeng.roses.kernel.rule.pojo.response.SuccessResponseData;
import cn.stylefeng.roses.kernel.scanner.api.annotation.ApiResource;
import cn.stylefeng.roses.kernel.scanner.api.annotation.GetResource;
import cn.stylefeng.roses.kernel.scanner.api.annotation.PostResource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 报警记录模块
 *
 * 提供报警记录的分页查询、单条处置及批量处置接口
 *
 * @author system
 */
@RestController
@ApiResource(name = "报警记录管理", resBizType = ResBizTypeEnum.BUSINESS)
public class AlarmRecordController {

    @Resource
    private AlarmRecordService alarmRecordService;

    /**
     * 分页查询报警记录
     * 查询条件：报警ID、报警类型、处置状态、报警开始时间、报警结束时间
     */
    @GetResource(name = "报警记录分页查询", path = "/alarmRecord/page")
    public ResponseData<?> page(AlarmRecordQueryRequest request) {
        return new SuccessResponseData<>(alarmRecordService.page(request));
    }

    /**
     * 报警记录详情
     *
     * @param alarmId 报警ID
     * @return 报警记录详情
     */
    @GetResource(name = "报警记录详情", path = "/alarmRecord/detail")
    public ResponseData<TAlarmResultRecords> detail(@RequestParam("alarmId") String alarmId) {
        return new SuccessResponseData<>(alarmRecordService.detail(alarmId));
    }

    /**
     * 单条报警处置
     */
    @PostResource(name = "报警处置", path = "/alarmRecord/dispose")
    public ResponseData<?> dispose(@RequestBody @Validated(AlarmRecordDisposalRequest.dispose.class) AlarmRecordDisposalRequest request) {
        alarmRecordService.dispose(request);
        return new SuccessResponseData<>();
    }

    /**
     * 批量报警处置
     */
    @PostResource(name = "报警批量处置", path = "/alarmRecord/batchDispose")
    public ResponseData<?> batchDispose(@RequestBody @Validated(AlarmRecordBatchDisposalRequest.batchDispose.class) AlarmRecordBatchDisposalRequest request) {
        return new SuccessResponseData<>(alarmRecordService.batchDispose(request));
    }

    /**
     * 根据报警记录ID查询关联的工业电视摄像头列表（包含对应的录像信息）
     *
     * 实现逻辑：
     * 1. 如果报警设备本身就是工业电视（subsystem_type=GYDS），直接返回该工业电视
     * 2. 否则，通过设备关联关系表查询关联的预设位，再通过预设位找到对应的工业电视
     * 3. 根据报警时间查询对应的录像文件
     *
     * @param alarmId 报警记录ID
     * @return 关联的工业电视列表（包含录像信息）
     */
    @GetResource(name = "查询报警关联工业电视", path = "/alarmRecord/relatedIndustrialTv")
    public ResponseData<List<IndustrialTvWithVideoDTO>> getRelatedIndustrialTv(@RequestParam("alarmId") String alarmId) {
        return new SuccessResponseData<>(alarmRecordService.getRelatedIndustrialTv(alarmId));
    }



}

