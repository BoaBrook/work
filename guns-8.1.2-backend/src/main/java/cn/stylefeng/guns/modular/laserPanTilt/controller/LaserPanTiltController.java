package cn.stylefeng.guns.modular.laserPanTilt.controller;

import cn.stylefeng.guns.core.dto.request.IdsRequest;
import cn.stylefeng.guns.database.entity.TLaserPanTiltBaseInfo;
import cn.stylefeng.guns.database.entity.TThresholdConfig;
import cn.stylefeng.guns.database.service.TThresholdConfigService;
import cn.stylefeng.guns.modular.laserPanTilt.request.LaserPanTiltRequest;
import cn.stylefeng.guns.modular.laserPanTilt.service.LaserPanTiltService;
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
 * 激光云台设备管理
 *
 * @author system
 * @date 2026-02-02
 */
@RestController
@ApiResource(name = "激光云台设备管理", resBizType = ResBizTypeEnum.BUSINESS)
public class LaserPanTiltController {

    @Resource
    private LaserPanTiltService laserPanTiltService;

    @Resource
    private TThresholdConfigService tThresholdConfigService;

    /**
     * 获取激光云台设备列表
     *
     * @param params 查询参数
     * @return 分页结果
     */
    @GetResource(name = "获取激光云台设备列表", path = "/laserPanTilt/list")
    public ResponseData<?> list(@RequestParam Map<String, Object> params) {
        return new SuccessResponseData<>(laserPanTiltService.list(params));
    }

    /**
     * 分页查询激光云台设备（返回作业区和管线名称）
     *
     * @param request 查询请求参数
     * @return 分页结果
     */
    @GetResource(name = "分页查询激光云台设备", path = "/laserPanTilt/pageList")
    public ResponseData<PageResult<TLaserPanTiltBaseInfo>> pageList(LaserPanTiltRequest request) {
        return new SuccessResponseData<>(laserPanTiltService.pageList(request));
    }

    /**
     * 保存阈值配置
     *
     * @param thresholdConfig 阈值配置信息
     * @return 是否成功
     */
    @PostResource(name = "保存阈值配置", path = "/laserPanTilt/save")
    public ResponseData<?> saveThreshold(@RequestBody TThresholdConfig thresholdConfig) {
        return new SuccessResponseData<>(laserPanTiltService.saveThreshold(thresholdConfig));
    }

    /**
     * 更新激光云台设备（新增/修改）
     *
     * @param request 设备信息
     * @return 是否成功
     */
    @PostResource(name = "更新激光云台设备", path = "/laserPanTilt/update")
    public ResponseData<?> updateLaserPanTilt(@RequestBody TLaserPanTiltBaseInfo request) {
        return new SuccessResponseData<>(laserPanTiltService.updateLaserPanTilt(request));
    }

    /**
     * 批量删除激光云台设备
     *
     * @param request ID列表请求
     * @return 是否成功
     */
    @PostResource(name = "批量删除激光云台设备", path = "/laserPanTilt/batchDelete")
    public ResponseData<?> batchDeleteLaserPanTilt(@RequestBody IdsRequest request) {
        return new SuccessResponseData<>(laserPanTiltService.batchDeleteLaserPanTilt(request));
    }

}