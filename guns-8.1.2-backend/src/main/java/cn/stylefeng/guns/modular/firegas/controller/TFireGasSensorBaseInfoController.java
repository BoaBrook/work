package cn.stylefeng.guns.modular.firegas.controller;

import cn.stylefeng.guns.database.entity.TFireGasSensorBaseInfo;
import cn.stylefeng.guns.database.service.TFireGasSensorBaseInfoService;
import cn.stylefeng.guns.modular.firegas.dto.TFireGasSensorBaseInfoQueryRequest;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;
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

/**
 * 火气系统传感器设备基础信息管理控制器
 *
 * @author system
 * @date 2026-01-14
 */
@RestController
@ApiResource(name = "火气系统传感器设备管理", resBizType = ResBizTypeEnum.BUSINESS)
public class TFireGasSensorBaseInfoController {

    @Resource
    private TFireGasSensorBaseInfoService tFireGasSensorBaseInfoService;

    /**
     * 分页查询火气系统传感器设备列表
     *
     * @param request 查询请求参数
     * @return 分页结果
     */
    @GetResource(name = "分页查询火气系统传感器设备列表", path = "/fireGas/sensorBase/page")
    public ResponseData<PageResult<TFireGasSensorBaseInfo>> page(TFireGasSensorBaseInfoQueryRequest request) {
        return new SuccessResponseData<>(tFireGasSensorBaseInfoService.pageList(request));
    }

    /**
     * 新增火气系统传感器设备
     *
     * @param sensorInfo 传感器设备信息
     * @return 是否成功
     */
    @PostResource(name = "新增火气系统传感器设备", path = "/fireGas/sensorBase/add", requiredLogin = false)
    public ResponseData<Boolean> add(@RequestBody TFireGasSensorBaseInfo sensorInfo) {
        return new SuccessResponseData<>(tFireGasSensorBaseInfoService.add(sensorInfo));
    }

    /**
     * 编辑火气系统传感器设备
     *
     * @param sensorInfo 传感器设备信息
     * @return 是否成功
     */
    @PostResource(name = "编辑火气系统传感器设备", path = "/fireGas/sensorBase/update", requiredLogin = false)
    public ResponseData<Boolean> update(@RequestBody TFireGasSensorBaseInfo sensorInfo) {
        return new SuccessResponseData<>(tFireGasSensorBaseInfoService.update(sensorInfo));
    }

    /**
     * 批量删除火气系统传感器设备
     *
     * @param deviceIds 设备ID列表
     * @return 是否成功
     */
    @PostResource(name = "批量删除火气系统传感器设备", path = "/fireGas/sensorBase/delete", requiredLogin = false)
    public ResponseData<Boolean> batchDelete(@RequestBody List<String> deviceIds) {
        if (deviceIds == null || deviceIds.isEmpty()) {
            return new SuccessResponseData<>(true);
        }
        return new SuccessResponseData<>(tFireGasSensorBaseInfoService.batchDelete(deviceIds));
    }

}
