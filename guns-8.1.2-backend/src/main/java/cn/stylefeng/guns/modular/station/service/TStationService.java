package cn.stylefeng.guns.modular.station.service;

import cn.stylefeng.guns.database.entity.TStationBaseInfo;
import cn.stylefeng.guns.modular.station.dto.OperationAreaOptionResponse;
import cn.stylefeng.guns.modular.station.dto.StationListRequest;
import cn.stylefeng.guns.modular.station.dto.StationListResponse;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 站点Service接口
 *
 * @author system
 * @date 2026-01-21
 */
public interface TStationService extends IService<TStationBaseInfo> {

    /**
     * 获取所有站点
     * @return 站点列表
     */
    List<TStationBaseInfo> getAllStations();

    /**
     * 按组织规则查询站场列表
     */
    PageResult<StationListResponse> listByOrganizationRule(StationListRequest request);

    /**
     * 编辑站场信息
     */
    boolean editStation(TStationBaseInfo station);

    /**
     * 作业区下拉
     */
    List<OperationAreaOptionResponse> operationAreaOptions();
}
