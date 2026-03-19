package cn.stylefeng.guns.modular.nvr.controller;

import cn.stylefeng.guns.database.entity.TNvrBaseInfo;
import cn.stylefeng.guns.modular.nvr.service.NvrManagementService;
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
 * 硬盘录像机设备管理Controller
 *
 * @author system
 * @date 2026-01-30
 */
@RestController
@ApiResource(name = "硬盘录像机设备管理", resBizType = ResBizTypeEnum.BUSINESS)
public class NvrManagementController {

    @Resource
    private NvrManagementService nvrManagementService;

    /**
     * 获取硬盘录像机设备列表
     *
     * @param params 查询参数
     * @return 分页结果
     */
    @GetResource(name = "获取硬盘录像机设备列表", path = "/nvrManagement/list")
    public ResponseData<?> list(@RequestParam Map<String, Object> params) {
        return new SuccessResponseData<>(nvrManagementService.list(params));
    }

    /**
     * 获取硬盘录像机设备详情
     *
     * @param deviceId 设备ID
     * @return 设备详情
     */
    @GetResource(name = "获取硬盘录像机设备详情", path = "/nvrManagement/getId")
    public ResponseData<?> getId(@RequestParam("deviceId") String deviceId) {
        return new SuccessResponseData<>(nvrManagementService.getId(deviceId));
    }

    /**
     * 新增硬盘录像机设备
     *
     * @param nvrBaseInfo 设备信息
     * @return 是否成功
     */
    @PostResource(name = "新增硬盘录像机设备", path = "/nvrManagement/add")
    public ResponseData<?> add(@RequestBody TNvrBaseInfo nvrBaseInfo) {
        return new SuccessResponseData<>(nvrManagementService.add(nvrBaseInfo));
    }

    /**
     * 编辑硬盘录像机设备
     *
     * @param nvrBaseInfo 设备信息
     * @return 是否成功
     */
    @PostResource(name = "编辑硬盘录像机设备", path = "/nvrManagement/update")
    public ResponseData<?> update(@RequestBody TNvrBaseInfo nvrBaseInfo) {
        return new SuccessResponseData<>(nvrManagementService.update(nvrBaseInfo));
    }

    /**
     * 删除硬盘录像机设备
     *
     * @param deviceId 设备ID
     * @return 是否成功
     */
    @PostResource(name = "删除硬盘录像机设备", path = "/nvrManagement/delete")
    public ResponseData<?> delete(@RequestParam("deviceId") String deviceId) {
        return new SuccessResponseData<>(nvrManagementService.delete(deviceId));
    }
}
