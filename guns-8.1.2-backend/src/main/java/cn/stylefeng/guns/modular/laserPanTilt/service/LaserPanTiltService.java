package cn.stylefeng.guns.modular.laserPanTilt.service;

import cn.stylefeng.guns.core.dto.request.IdsRequest;
import cn.stylefeng.guns.database.entity.TLaserPanTiltBaseInfo;
import cn.stylefeng.guns.modular.laserPanTilt.request.LaserPanTiltRequest;
import cn.stylefeng.guns.database.entity.TThresholdConfig;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;

import java.util.Map;

/**
 * 激光云台设备服务接口
 *
 * @author system
 * @date 2026-02-02
 */
public interface LaserPanTiltService {

    /**
     * 获取激光云台设备列表
     *
     * @param params 查询参数
     * @return 分页结果
     */
    PageResult<?> list(Map<String, Object> params);

    /**
     * 分页查询激光云台设备（返回作业区和管线名称）
     *
     * @param request 查询请求参数
     * @return 分页结果
     */
    PageResult<TLaserPanTiltBaseInfo> pageList(LaserPanTiltRequest request);

    /**
     * 更新激光云台设备（新增/修改）
     *
     * @param request 设备信息
     * @return 是否成功
     */
    Boolean updateLaserPanTilt(TLaserPanTiltBaseInfo request);

    /**
     * 批量删除激光云台设备
     *
     * @param request ID列表请求
     * @return 是否成功
     */
    Boolean batchDeleteLaserPanTilt(IdsRequest request);

    /**
     * 保存阈值配置
     *
     * @param thresholdConfig 阈值配置信息
     * @return 是否成功
     */
    boolean saveThreshold(TThresholdConfig thresholdConfig);
}