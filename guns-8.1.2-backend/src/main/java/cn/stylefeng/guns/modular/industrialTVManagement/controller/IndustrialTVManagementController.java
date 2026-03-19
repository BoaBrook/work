package cn.stylefeng.guns.modular.industrialTVManagement.controller;

import cn.stylefeng.guns.database.entity.TIndustrialTvBaseInfo;
import cn.stylefeng.guns.modular.industrialTVManagement.request.IndustrialTVListRequest;
import cn.stylefeng.guns.modular.industrialTVManagement.service.IndustrialTVManagementService;
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
 * 工业电视设备管理Controller
 *
 * @author system
 * @date 2026-01-27
 */
@RestController
@ApiResource(name = "工业电视设备管理", resBizType = ResBizTypeEnum.BUSINESS)
public class IndustrialTVManagementController {

    @Resource
    private IndustrialTVManagementService industrialTVManagementService;

    /**
     * 获取工业电视设备列表
     *
     * @param request 查询参数
     * @return 分页结果
     */
    @GetResource(name = "获取工业电视设备列表", path = "/industrialTVManagement/list")
    public ResponseData<?> list(IndustrialTVListRequest request) {
        return new SuccessResponseData<>(industrialTVManagementService.list(request));
    }

    /**
     * 获取工业电视设备详情
     *
     * @param deviceId 设备ID
     * @return 设备详情
     */
    @GetResource(name = "获取工业电视设备详情", path = "/industrialTVManagement/getId")
    public ResponseData<?> getId(@RequestParam("deviceId") String deviceId) {
        return new SuccessResponseData<>(industrialTVManagementService.getId(deviceId));
    }

    /**
     * 新增工业电视设备
     *
     * @param industrialTvBaseInfo 设备信息
     * @return 是否成功
     */
    @PostResource(name = "新增工业电视设备", path = "/industrialTVManagement/add")
    public ResponseData<?> add(@RequestBody TIndustrialTvBaseInfo industrialTvBaseInfo) {
        return new SuccessResponseData<>(industrialTVManagementService.add(industrialTvBaseInfo));
    }

    /**
     * 编辑工业电视设备
     *
     * @param industrialTvBaseInfo 设备信息
     * @return 是否成功
     */
    @PostResource(name = "编辑工业电视设备", path = "/industrialTVManagement/update")
    public ResponseData<?> update(@RequestBody TIndustrialTvBaseInfo industrialTvBaseInfo) {
        return new SuccessResponseData<>(industrialTVManagementService.update(industrialTvBaseInfo));
    }

    /**
     * 删除工业电视设备
     *
     * @param deviceId 设备ID
     * @return 是否成功
     */
    @PostResource(name = "删除工业电视设备", path = "/industrialTVManagement/delete")
    public ResponseData<?> delete(@RequestParam("deviceId") String deviceId) {
        return new SuccessResponseData<>(industrialTVManagementService.delete(deviceId));
    }

    /**
     * 获取站场相关下拉列表
     * @return 站场，管线，作业区选项列表
     */
    @GetResource(name = "获取站场相关下拉列表", path = "/dropDown/stationOptions")
    public ResponseData<?> listStationOptions() {
        return new SuccessResponseData<>(industrialTVManagementService.listStationOptions());
    }

    /**
     * 获取配置算法字典
     * @return 算法选项列表
     */
    @GetResource(name = "获取配置算法字典", path = "/industrialTVManagement/algorithmOptions")
    public ResponseData<?> listAlgorithmOptions() {
        return new SuccessResponseData<>(industrialTVManagementService.listAlgorithmOptions());
    }

    /**
     * 获取站场区域下拉列表
     * @param stationId 站场ID
     * @return 区域选项列表
     */
    @GetResource(name = "获取站场区域下拉列表", path = "/stationAreaOptions")
    public ResponseData<?> listStationAreaOptions(@RequestParam(value = "stationId", required = false) String stationId) {
        return new SuccessResponseData<>(industrialTVManagementService.listStationAreaOptions(stationId));
    }

    /**
     * 校验设备编码唯一性
     *
     * 用于新增和修改时，校验同一站场下的设备编码是否唯一
     *
     * @param belongStationId 站场ID
     * @param deviceCode 设备编码
     * @param deviceId 设备ID（编辑时传入，用于排除自身；新增时可不传）
     * @return true-唯一（可以使用），false-不唯一（已存在）
     */
    @GetResource(name = "校验设备编码唯一性", path = "/industrialTVManagement/checkDeviceCodeUnique")
    public ResponseData<Boolean> checkDeviceCodeUnique(
            @RequestParam("belongStationId") String belongStationId,
            @RequestParam(value = "deviceCode", required = false) String deviceCode,
            @RequestParam(value = "deviceIp", required = false) String deviceIp,
            @RequestParam(value = "deviceId", required = false) String deviceId) {
        return new SuccessResponseData<>(industrialTVManagementService.checkDeviceCodeUnique(belongStationId, deviceCode, deviceIp, deviceId));
    }
}
