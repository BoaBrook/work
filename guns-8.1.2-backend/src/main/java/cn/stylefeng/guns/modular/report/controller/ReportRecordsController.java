package cn.stylefeng.guns.modular.report.controller;

import cn.stylefeng.guns.modular.report.entity.ReportRecords;
import cn.stylefeng.guns.modular.report.request.ReportRecordsListRequest;
import cn.stylefeng.guns.modular.report.service.ReportRecordsService;
import cn.stylefeng.roses.kernel.rule.enums.ResBizTypeEnum;
import cn.stylefeng.roses.kernel.rule.pojo.response.ResponseData;
import cn.stylefeng.roses.kernel.rule.pojo.response.SuccessResponseData;
import cn.stylefeng.roses.kernel.scanner.api.annotation.ApiResource;
import cn.stylefeng.roses.kernel.scanner.api.annotation.GetResource;
import cn.stylefeng.roses.kernel.scanner.api.annotation.PostResource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@ApiResource(name = "消息上报", path = "/reportRecords", resBizType = ResBizTypeEnum.BUSINESS)
public class ReportRecordsController {

    @Resource
    private ReportRecordsService reportRecordsService;

    @GetResource(name = "消息上报-报备记录列表", path = "/list")
    public ResponseData<?> list(ReportRecordsListRequest request) {
        return new SuccessResponseData<>(reportRecordsService.list(request));
    }

    @PostResource(name = "消息上报-保存报备记录", path = "/save")
    public ResponseData<?> save(@RequestBody ReportRecords reportRecords) {
        return new SuccessResponseData<>(reportRecordsService.reportSave(reportRecords));
    }

    @PostResource(name = "消息上报-批量删除报备记录", path = "/delete")
    public ResponseData<?> delete(@RequestBody List<String> ids) {
        return new SuccessResponseData<>(reportRecordsService.deleteBatch(ids));
    }

    @PostResource(name = "消息上报-批量上报报备记录", path = "/submit")
    public ResponseData<?> submit(@RequestBody List<String> ids) {
        return new SuccessResponseData<>(reportRecordsService.submitBatch(ids));
    }

}
