package cn.stylefeng.guns.modular.stationarea.service;

import cn.stylefeng.guns.database.entity.TStationAreaBaseInfo;
import cn.stylefeng.guns.modular.pipeline.dto.StationOption;
import cn.stylefeng.guns.modular.stationarea.dto.StationAreaQueryRequest;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 站场区域基础信息 Service
 *
 * @author system
 */
public interface StationAreaService extends IService<TStationAreaBaseInfo> {

    /**
     * 分页查询站场区域列表
     *
     * @param request 查询条件（包含分页参数）
     * @return 分页结果
     */
    PageResult<TStationAreaBaseInfo> pageList(StationAreaQueryRequest request);

    /**
     * 新增站场区域
     *
     * @param areaInfo 区域信息
     * @return 是否成功
     */
    boolean add(TStationAreaBaseInfo areaInfo);

    /**
     * 编辑站场区域
     *
     * @param areaInfo 区域信息
     * @return 是否成功
     */
    boolean update(TStationAreaBaseInfo areaInfo);

    /**
     * 删除站场区域
     *
     * @param areaId 区域ID
     * @return 是否成功
     */
    boolean delete(String areaId);

    /**
     * 获取站场下拉列表
     *
     * @return 站场下拉选项集合
     */
    List<StationOption> stationOptions();
}

