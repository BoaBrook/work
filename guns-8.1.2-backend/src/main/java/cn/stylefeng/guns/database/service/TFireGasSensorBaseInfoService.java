package cn.stylefeng.guns.database.service;

import cn.stylefeng.guns.database.entity.TFireGasSensorBaseInfo;
import cn.stylefeng.guns.modular.firegas.dto.TFireGasSensorBaseInfoQueryRequest;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 火气系统传感器设备基础信息表 Service接口
 *
 * @author system
 * @date 2026-01-14
 */
public interface TFireGasSensorBaseInfoService extends IService<TFireGasSensorBaseInfo> {

    /**
     * 分页查询火气系统传感器设备列表
     *
     * @param request 查询条件（包含分页参数）
     * @return 分页结果
     */
    PageResult<TFireGasSensorBaseInfo> pageList(TFireGasSensorBaseInfoQueryRequest request);

    /**
     * 新增火气系统传感器设备
     *
     * @param sensorInfo 传感器设备信息
     * @return 是否成功
     */
    boolean add(TFireGasSensorBaseInfo sensorInfo);

    /**
     * 编辑火气系统传感器设备
     *
     * @param sensorInfo 传感器设备信息
     * @return 是否成功
     */
    boolean update(TFireGasSensorBaseInfo sensorInfo);

    /**
     * 批量删除火气系统传感器设备
     *
     * @param deviceIds 设备ID列表
     * @return 是否成功
     */
    boolean batchDelete(List<String> deviceIds);

}