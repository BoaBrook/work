package cn.stylefeng.guns.modular.nvr.service;

import cn.stylefeng.guns.database.entity.TNvrBaseInfo;
import cn.stylefeng.guns.modular.nvr.entity.NvrWithStationInfo;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;

import java.util.Map;

/**
 * 硬盘录像机设备管理Service接口
 *
 * @author system
 * @date 2026-01-30
 */
public interface NvrManagementService {

    /**
     * 获取硬盘录像机设备列表
     *
     * @param params 查询参数
     * @return 分页结果
     */
    PageResult<NvrWithStationInfo> list(Map<String, Object> params);

    /**
     * 获取硬盘录像机设备详情
     *
     * @param deviceId 设备ID
     * @return 设备详情
     */
    NvrWithStationInfo getId(String deviceId);

    /**
     * 新增硬盘录像机设备
     *
     * @param nvrBaseInfo 设备信息
     * @return 是否成功
     */
    boolean add(TNvrBaseInfo nvrBaseInfo);

    /**
     * 编辑硬盘录像机设备
     *
     * @param nvrBaseInfo 设备信息
     * @return 是否成功
     */
    boolean update(TNvrBaseInfo nvrBaseInfo);

    /**
     * 删除硬盘录像机设备
     *
     * @param deviceId 设备ID
     * @return 是否成功
     */
    boolean delete(String deviceId);
}
