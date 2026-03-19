package cn.stylefeng.guns.modular.pipeline.service;

import cn.stylefeng.guns.database.entity.TPipelineBaseInfo;
import cn.stylefeng.guns.database.entity.TStationBaseInfo;
import cn.stylefeng.guns.modular.pipeline.dto.PipelineWithStations;
import cn.stylefeng.guns.modular.pipeline.dto.StationOption;
import cn.stylefeng.guns.modular.pipeline.request.PipelineListRequest;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;

import java.util.List;

public interface PipelineService {

    /**
     * 管线列表查询（分页）
     * 支持根据管线名称和关联站场查询
     */
    PageResult<PipelineWithStations> getPipelineList(PipelineListRequest request);

    /**
     * 新增管线
     * 关联站场由站场管理模块设置，此处不处理
     */
    boolean savePipeline(TPipelineBaseInfo pipelineBaseInfo);

    /**
     * 编辑管线
     * 关联站场由站场管理模块设置，此处不处理
     */
    boolean updatePipeline(TPipelineBaseInfo pipelineBaseInfo);

    /**
     * 批量删除管线
     * @param pipelineIds 管线ID数组
     */
    boolean deletePipeline(List<String> pipelineIds);

    /**
     * 根据管线ID查询站场列表
     */
    List<TStationBaseInfo> getStationsByPipeline(String pipelineId);

    /**
     * 获取所有站场列表（用于下拉选择）
     * 只返回站场ID和名称
     */
    List<StationOption> getAllStationsForOption();

    /**
     * 根据管线ID获取管线详情
     * 用于编辑功能
     */
    PipelineWithStations getPipelineById(String pipelineId);

    /**
     * 获取所有管线列表
     */
    List<TPipelineBaseInfo> getAllPipelines();

}
