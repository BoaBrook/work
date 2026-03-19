package cn.stylefeng.guns.modular.alarmconfig.controller;

import cn.stylefeng.guns.database.entity.TAlarmConfig;
import cn.stylefeng.guns.modular.alarmconfig.request.AlarmConfigRequest;
import cn.stylefeng.guns.modular.alarmconfig.service.AlarmConfigService;
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
 * 报警配置Controller
 *
 * @author system
 * @date 2026-01-14
 */
@RestController
@ApiResource(name = "报警配置", resBizType = ResBizTypeEnum.BUSINESS)
public class AlarmConfigController {

    @Resource
    private AlarmConfigService alarmConfigService;

    /**
     * 分页查询报警配置列表
     *
     * @param request 查询请求
     * @return 报警配置列表
     */
    @GetResource(name = "分页查询报警配置列表", path = "/alarmConfig/page")
    public ResponseData<?> page(@Validated(BaseRequest.page.class) AlarmConfigRequest request) {
        return new SuccessResponseData<>(alarmConfigService.page(request));
    }

    /**
     * 根据ID查询报警配置
     *
     * @param request 查询请求
     * @return 报警配置
     */
    @GetResource(name = "根据ID查询报警配置", path = "/alarmConfig/detail")
    public ResponseData<?> detail(@Validated(BaseRequest.detail.class) AlarmConfigRequest request) {
        TAlarmConfig config = alarmConfigService.getById(request);
        return new SuccessResponseData<>(config);
    }

    /**
     * 新增报警配置
     *
     * @param request 新增请求
     * @return 操作结果
     */
    @PostResource(name = "新增报警配置", path = "/alarmConfig/add")
    public ResponseData<?> add(@RequestBody @Validated(BaseRequest.add.class) AlarmConfigRequest request) {
        boolean success = alarmConfigService.save(request);
        return new SuccessResponseData<>(success);
    }

    /**
     * 修改报警配置
     *
     * @param request 修改请求
     * @return 操作结果
     */
    @PostResource(name = "修改报警配置", path = "/alarmConfig/edit")
    public ResponseData<?> edit(@RequestBody @Validated(BaseRequest.edit.class) AlarmConfigRequest request) {
        boolean success = alarmConfigService.updateById(request);
        return new SuccessResponseData<>(success);
    }

    /**
     * 删除报警配置
     *
     * @param request 删除请求
     * @return 操作结果
     */
    @PostResource(name = "删除报警配置", path = "/alarmConfig/delete")
    public ResponseData<?> delete(@RequestBody @Validated(BaseRequest.delete.class) AlarmConfigRequest request) {
        boolean success = alarmConfigService.removeById(request);
        return new SuccessResponseData<>(success);
    }

    /**
     * 批量删除报警配置
     *
     * @param request 批量删除请求
     * @return 操作结果
     */
    @PostResource(name = "批量删除报警配置", path = "/alarmConfig/deleteBatch")
    public ResponseData<?> deleteBatch(@RequestBody @Validated(BaseRequest.batchDelete.class) AlarmConfigRequest request) {
        boolean success = alarmConfigService.removeByIds(request);
        return new SuccessResponseData<>(success);
    }
}
