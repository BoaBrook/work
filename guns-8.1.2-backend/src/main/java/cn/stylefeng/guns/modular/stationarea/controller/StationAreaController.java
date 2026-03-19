package cn.stylefeng.guns.modular.stationarea.controller;

import cn.stylefeng.guns.database.entity.TStationAreaBaseInfo;
import cn.stylefeng.guns.modular.stationarea.dto.StationAreaQueryRequest;
import cn.stylefeng.guns.modular.stationarea.service.StationAreaService;
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
 * 站场区域管理控制器
 *
 *
 * @author system
 */
@RestController
@ApiResource(name = "站场区域管理", resBizType = ResBizTypeEnum.BUSINESS)
public class StationAreaController {

    @Resource
    private StationAreaService stationAreaService;

    /**
     * 获取站场区域列表（分页）
     *
     * 支持按区域名称、所属站场
     */
    @GetResource(name = "获取站场区域列表", path = "/stationArea/list")
    public ResponseData<PageResult<TStationAreaBaseInfo>> list(StationAreaQueryRequest request) {
        return new SuccessResponseData<>(stationAreaService.pageList(request));
    }

    /**
     * 获取站场下拉列表
     */
    @GetResource(name = "获取站场下拉列表", path = "/stationOptions/all")
    public ResponseData<?> stationOptions() {
        return new SuccessResponseData<>(stationAreaService.stationOptions());
    }

    /**
     * 新增站场区域
     *
     * @param areaInfo 区域信息
     */
    @PostResource(name = "新增站场区域", path = "/stationArea/add")
    public ResponseData<Boolean> add(@RequestBody TStationAreaBaseInfo areaInfo) {
        return new SuccessResponseData<>(stationAreaService.add(areaInfo));
    }

    /**
     * 编辑站场区域
     *
     * @param areaInfo 区域信息
     */
    @PostResource(name = "编辑站场区域", path = "/stationArea/update")
    public ResponseData<Boolean> update(@RequestBody TStationAreaBaseInfo areaInfo) {
        return new SuccessResponseData<>(stationAreaService.update(areaInfo));
    }

    /**
     * 删除站场区域
     *
     * @param areaId 区域ID
     */
    @PostResource(name = "删除站场区域", path = "/stationArea/delete")
    public ResponseData<Boolean> delete(@RequestParam("areaId") String areaId) {
        return new SuccessResponseData<>(stationAreaService.delete(areaId));
    }
}

