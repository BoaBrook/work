package cn.stylefeng.guns.modular.safeday.controller;

import cn.stylefeng.guns.modular.safeday.entity.SafeDayConfigRow;
import cn.stylefeng.guns.modular.safeday.entity.SafeDayPageRequest;
import cn.stylefeng.guns.modular.safeday.entity.SafeDaysParam;
import cn.stylefeng.guns.modular.safeday.service.TSafeOperationDaysService;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;
import cn.stylefeng.roses.kernel.rule.enums.ResBizTypeEnum;
import cn.stylefeng.roses.kernel.rule.pojo.response.ResponseData;
import cn.stylefeng.roses.kernel.rule.pojo.response.SuccessResponseData;
import cn.stylefeng.roses.kernel.scanner.api.annotation.ApiResource;
import cn.stylefeng.roses.kernel.scanner.api.annotation.GetResource;
import cn.stylefeng.roses.kernel.scanner.api.annotation.PostResource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 安全运行天数（页面内容配置）控制器
 *
 * @author system
 * @date 2026-01-20
 */
@RestController
@ApiResource(name = "安全运行天数管理", resBizType = ResBizTypeEnum.BUSINESS)
public class SafeDayController {

    @Resource
    private TSafeOperationDaysService safeOperationDaysService;

    /**
     * 获取页面内容配置列表
     */
    @GetResource(name = "页面内容配置分页列表", path = "/safeOperationDays/list")
    public ResponseData<PageResult<SafeDayConfigRow>> pageConfigList(SafeDayPageRequest request) {
        PageResult<SafeDayConfigRow> result = safeOperationDaysService.pageConfigList(request);
        return new SuccessResponseData<>(result);
    }

    /**
     * 根据站点ID获取安全运行天数和定义（用于编辑弹窗回显）
     *
     * @param stationId 站点ID
     * @return 包含 days、definition 等
     */
    @GetResource(name = "根据站点ID获取安全运行天数和定义", path = "/safeOperationDays/get")
    public ResponseData<?> getSafeOperationDaysByStationId(@RequestParam("stationId") String stationId) {
        Map<String, Object> result = safeOperationDaysService.getCurrentSafeDaysByStationId(stationId);
        return new SuccessResponseData<>(result);
    }

    /**
     * 保存配置
     * @return 是否成功
     */
    @PostResource(name = "保存安全运行天数配置", path = "/safeOperationDays/save")
    public ResponseData<?> saveConfig(@RequestBody SafeDaysParam params) {
        boolean result = safeOperationDaysService.saveConfig(params);
        return new SuccessResponseData<>(result);
    }
}