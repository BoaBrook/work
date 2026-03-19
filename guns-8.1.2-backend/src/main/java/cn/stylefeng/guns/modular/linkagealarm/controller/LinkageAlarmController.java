package cn.stylefeng.guns.modular.linkagealarm.controller;

import cn.stylefeng.guns.database.entity.TLinkageAlarmConfig;
import cn.stylefeng.guns.modular.linkagealarm.request.LinkageAlarmRequest;
import cn.stylefeng.guns.modular.linkagealarm.service.LinkageAlarmService;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;
import cn.stylefeng.roses.kernel.rule.enums.ResBizTypeEnum;
import cn.stylefeng.roses.kernel.rule.pojo.request.BaseRequest;
import cn.stylefeng.roses.kernel.rule.pojo.response.ResponseData;
import cn.stylefeng.roses.kernel.rule.pojo.response.SuccessResponseData;
import cn.stylefeng.roses.kernel.scanner.api.annotation.ApiResource;
import cn.stylefeng.roses.kernel.scanner.api.annotation.GetResource;
import cn.stylefeng.roses.kernel.scanner.api.annotation.PostResource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 联动报警配置Controller
 *
 * @author system
 * @date 2026-01-14
 */
@RestController
@ApiResource(name = "联动报警配置", resBizType = ResBizTypeEnum.BUSINESS)
public class LinkageAlarmController {

    @Resource
    private LinkageAlarmService linkageAlarmService;

    /**
     * 分页查询联动报警配置列表
     *
     * @param request 查询请求
     * @return 联动报警配置列表
     */
    @GetResource(name = "分页查询联动报警配置列表", path = "/linkageAlarm/page")
    public ResponseData<PageResult<TLinkageAlarmConfig>> page(@Validated(BaseRequest.page.class) LinkageAlarmRequest request) {
        PageResult<TLinkageAlarmConfig> pageResult = linkageAlarmService.page(request);
        return new SuccessResponseData<>(pageResult);
    }

    /**
     * 查询联动报警配置列表
     *
     * @param request 查询请求
     * @return 联动报警配置列表
     */
    @GetResource(name = "查询联动报警配置列表", path = "/linkageAlarm/list")
    public ResponseData<List<TLinkageAlarmConfig>> list(@Validated(BaseRequest.list.class) LinkageAlarmRequest request) {
        List<TLinkageAlarmConfig> list = linkageAlarmService.list(request);
        return new SuccessResponseData<>(list);
    }

    /**
     * 根据ID查询联动报警配置
     *
     * @param request 查询请求
     * @return 联动报警配置
     */
    @GetResource(name = "根据ID查询联动报警配置", path = "/linkageAlarm/detail")
    public ResponseData<TLinkageAlarmConfig> detail(@Validated(BaseRequest.detail.class) LinkageAlarmRequest request) {
        TLinkageAlarmConfig config = linkageAlarmService.getById(request);
        return new SuccessResponseData<>(config);
    }

    /**
     * 新增联动报警配置
     *
     * @param request 新增请求
     * @return 操作结果
     */
    @PostResource(name = "新增联动报警配置", path = "/linkageAlarm/add")
    public ResponseData<Boolean> add(@RequestBody @Validated(BaseRequest.add.class) LinkageAlarmRequest request) {
        boolean success = linkageAlarmService.save(request);
        return new SuccessResponseData<>(success);
    }

    /**
     * 修改联动报警配置
     *
     * @param request 修改请求
     * @return 操作结果
     */
    @PostResource(name = "修改联动报警配置", path = "/linkageAlarm/edit")
    public ResponseData<Boolean> edit(@RequestBody @Validated(BaseRequest.edit.class) LinkageAlarmRequest request) {
        boolean success = linkageAlarmService.updateById(request);
        return new SuccessResponseData<>(success);
    }

    /**
     * 删除联动报警配置
     *
     * @param request 删除请求
     * @return 操作结果
     */
    @PostResource(name = "删除联动报警配置", path = "/linkageAlarm/delete")
    public ResponseData<Boolean> delete(@RequestBody @Validated(BaseRequest.delete.class) LinkageAlarmRequest request) {
        boolean success = linkageAlarmService.removeById(request);
        return new SuccessResponseData<>(success);
    }

    /**
     * 批量删除联动报警配置
     *
     * @param request 批量删除请求
     * @return 操作结果
     */
    @PostResource(name = "批量删除联动报警配置", path = "/linkageAlarm/deleteBatch")
    public ResponseData<Boolean> deleteBatch(@RequestBody @Validated(BaseRequest.batchDelete.class) LinkageAlarmRequest request) {
        boolean success = linkageAlarmService.removeByIds(request);
        return new SuccessResponseData<>(success);
    }

    /**
     * 更新状态（开启/关闭）
     *
     * @param request 状态更新请求（包含linkageAlarmId和status）
     * @return 操作结果
     */
    @PostResource(name = "更新联动报警配置状态", path = "/linkageAlarm/updateStatus")
    public ResponseData<Boolean> updateStatus(@RequestBody @Validated(LinkageAlarmRequest.UpdateStatus.class) LinkageAlarmRequest request) {
        boolean success = linkageAlarmService.updateStatus(request.getLinkageAlarmId(), request.getStatus());
        return new SuccessResponseData<>(success);
    }
}
