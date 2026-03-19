package cn.stylefeng.guns.modular.workarea.controller;

import cn.stylefeng.guns.database.service.TWorkareaBaseInfoService;
import cn.stylefeng.guns.modular.workarea.entity.TWorkareaConfig;
import cn.stylefeng.guns.modular.workarea.service.TWorkareaConfigService;
import cn.stylefeng.roses.kernel.rule.enums.ResBizTypeEnum;
import cn.stylefeng.roses.kernel.rule.pojo.response.ResponseData;
import cn.stylefeng.roses.kernel.rule.pojo.response.SuccessResponseData;
import cn.stylefeng.roses.kernel.scanner.api.annotation.ApiResource;
import cn.stylefeng.roses.kernel.scanner.api.annotation.GetResource;
import cn.stylefeng.roses.kernel.scanner.api.annotation.PostResource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 作业区配置控制器
 *
 * @author system
 * @date 2026-01-20
 */
@RestController
@ApiResource(name = "作业区配置", code = "workarea_config", resBizType = ResBizTypeEnum.BUSINESS)
public class WorkareaController {

    @Resource
    private TWorkareaConfigService workareaConfigService;

    @Resource
    private TWorkareaBaseInfoService tWorkareaBaseInfoService;

    /**
     * 获取作业区URL
     *
     * @return 作业区URL
     */
    @GetResource(name = "获取作业区URL", path = "/workarea/getUrl")
    public ResponseData<?> getWorkareaUrl() {
        String workareaUrl = workareaConfigService.getWorkareaUrl();
        return new SuccessResponseData<>(workareaUrl);
    }

    /**
     * 保存或更新作业区配置
     *
     * @param workareaConfig 作业区配置
     * @return 是否成功
     */
    @PostResource(name = "保存或更新作业区配置", path = "/workarea/saveOrUpdate")
    public ResponseData<?> saveOrUpdateWorkareaConfig(@RequestBody TWorkareaConfig workareaConfig) {
        boolean result = workareaConfigService.saveOrUpdateConfig(workareaConfig);
        return new SuccessResponseData<>(result);
    }

    /**
     * 获取作业区配置详情
     *
     * @return 作业区配置详情
     */
    @GetResource(name = "获取作业区配置详情", path = "/workarea/getConfig")
    public ResponseData<?> getWorkareaConfig() {
        TWorkareaConfig workareaConfig = workareaConfigService.getLatestConfig();
        return new SuccessResponseData<>(workareaConfig);
    }

    /**
     * 获取所有作业区列表
     *
     * @return 作业区列表
     */
    @GetResource(name = "获取所有作业区", path = "/workarea/all")
    public ResponseData<?> getAllWorkareas() {
        return new SuccessResponseData<>(tWorkareaBaseInfoService.list());
    }

//    /**
//     * 获取用户所属作业区和管线
//     *
//     * @return 作业区列表
//     */
//    @GetResource(name = "获取用户所属作业区和管线", path = "/workarea/userWorkareas")
//    public ResponseData<?> getUserWorkareas() {
//        return new SuccessResponseData<>(tWorkareaBaseInfoService.getUserWorkareas());
//    }

}