package cn.stylefeng.guns.modular.report.service;

import cn.stylefeng.guns.modular.report.entity.ReportRecords;
import cn.stylefeng.guns.modular.report.request.ReportRecordsListRequest;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ReportRecordsService extends IService<ReportRecords> {

    PageResult<ReportRecords> list(ReportRecordsListRequest request);

    boolean reportSave(ReportRecords reportRecords);

    boolean deleteBatch(List<String> ids);

    boolean submitBatch(List<String> ids);

}
