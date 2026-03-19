package cn.stylefeng.guns.modular.firegas.controller;

import cn.stylefeng.guns.database.entity.TFireGasHostBaseInfo;
import cn.stylefeng.guns.database.service.TFireGasHostBaseInfoService;
import cn.stylefeng.guns.modular.firegas.dto.TFireGasHostBaseInfoQueryRequest;
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

/**
 * 火气系统主机设备基础信息管理控制器
 *
 * @author system
 * @date 2026-01-14
 */
@RestController
@ApiResource(name = "火气系统主机设备管理", resBizType = ResBizTypeEnum.BUSINESS)
public class TFireGasHostBaseInfoController {

    @Resource
    private TFireGasHostBaseInfoService tFireGasHostBaseInfoService;

    /**
     * 分页查询火气系统主机设备列表
     *
     * @param request 查询请求参数
     * @return 分页结果
     */
    @GetResource(name = "分页查询火气系统主机设备列表", path = "/fireGas/host/page")
    public ResponseData<PageResult<TFireGasHostBaseInfo>> page(TFireGasHostBaseInfoQueryRequest request) {
        return new SuccessResponseData<>(tFireGasHostBaseInfoService.pageList(request));
    }

    /**
     * 新增火气系统主机设备
     *
     * @param hostInfo 主机设备信息
     * @return 是否成功
     */
    @PostResource(name = "新增火气系统主机设备", path = "/fireGas/host/add", requiredLogin = false)
    public ResponseData<Boolean> add(@RequestBody TFireGasHostBaseInfo hostInfo) {
        return new SuccessResponseData<>(tFireGasHostBaseInfoService.add(hostInfo));
    }

    /**
     * 编辑火气系统主机设备
     *
     * @param hostInfo 主机设备信息
     * @return 是否成功
     */
    @PostResource(name = "编辑火气系统主机设备", path = "/fireGas/host/update", requiredLogin = false)
    public ResponseData<Boolean> update(@RequestBody TFireGasHostBaseInfo hostInfo) {
        return new SuccessResponseData<>(tFireGasHostBaseInfoService.update(hostInfo));
    }

    /**
     * 删除火气系统主机设备
     *
     * @param deviceId 设备ID
     * @return 是否成功
     */
    @PostResource(name = "删除火气系统主机设备", path = "/fireGas/host/delete", requiredLogin = false)
    public ResponseData<Boolean> delete(@RequestParam("deviceId") String deviceId) {
        return new SuccessResponseData<>(tFireGasHostBaseInfoService.delete(deviceId));
    }

}
