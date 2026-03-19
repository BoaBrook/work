package cn.stylefeng.guns.modular.station.controller;

import cn.stylefeng.guns.database.entity.TStationBaseInfo;
import cn.stylefeng.guns.modular.station.dto.OperationAreaOptionResponse;
import cn.stylefeng.guns.modular.station.dto.StationListRequest;
import cn.stylefeng.guns.modular.station.dto.StationListResponse;
import cn.stylefeng.guns.modular.station.service.TStationService;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;
import cn.stylefeng.roses.kernel.rule.enums.ResBizTypeEnum;
import cn.stylefeng.roses.kernel.rule.pojo.response.ResponseData;
import cn.stylefeng.roses.kernel.rule.pojo.response.SuccessResponseData;
import cn.stylefeng.roses.kernel.scanner.api.annotation.ApiResource;
import cn.stylefeng.roses.kernel.scanner.api.annotation.GetResource;
import cn.stylefeng.roses.kernel.scanner.api.annotation.PostResource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 站点Controller
 *
 * @author system
 * @date 2026-01-21
 */
@RestController
@ApiResource(name = "站点管理", resBizType = ResBizTypeEnum.BUSINESS)
public class StationController {

    private final TStationService stationService;

    public StationController(TStationService stationService) {
        this.stationService = stationService;
    }

    /**
     * 站场列表查询
     */
    @GetResource(name = "站场列表查询", path = "/station/list")
    public ResponseData<PageResult<StationListResponse>> list(StationListRequest request) {
        return new SuccessResponseData<>(stationService.listByOrganizationRule(request));
    }

    /**
     * 作业区下拉
     */
    @GetResource(name = "作业区下拉", path = "/station/operationArea/options")
    public ResponseData<List<OperationAreaOptionResponse>> operationAreaOptions() {
        return new SuccessResponseData<>(stationService.operationAreaOptions());
    }

    /**
     * 编辑站场
     */
    @PostResource(name = "站场编辑", path = "/station/update")
    public ResponseData<Boolean> update(@RequestBody TStationBaseInfo request) {
        return new SuccessResponseData<>(stationService.editStation(request));
    }

    /**
     * 获取所有站点
     *
     * @return 站点列表
     */
    @GetResource(name = "获取所有站点", path = "/station/getAll")
    public ResponseData<?> getAllStations() {
        List<TStationBaseInfo> stations = stationService.getAllStations();
        return new SuccessResponseData<>(stations);
    }
}
