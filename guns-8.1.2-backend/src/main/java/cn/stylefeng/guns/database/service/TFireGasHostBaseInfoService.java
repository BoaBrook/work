package cn.stylefeng.guns.database.service;

import cn.stylefeng.guns.database.entity.TFireGasHostBaseInfo;
import cn.stylefeng.guns.modular.firegas.dto.TFireGasHostBaseInfoQueryRequest;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 火气系统主机设备基础信息表 Service接口
 *
 * @author system
 * @date 2026-01-14
 */
public interface TFireGasHostBaseInfoService extends IService<TFireGasHostBaseInfo> {

    /**
     * 分页查询火气系统主机设备列表
     *
     * @param request 查询条件（包含分页参数）
     * @return 分页结果
     */
    PageResult<TFireGasHostBaseInfo> pageList(TFireGasHostBaseInfoQueryRequest request);

    /**
     * 新增火气系统主机设备
     *
     * @param hostInfo 主机设备信息
     * @return 是否成功
     */
    boolean add(TFireGasHostBaseInfo hostInfo);

    /**
     * 编辑火气系统主机设备
     *
     * @param hostInfo 主机设备信息
     * @return 是否成功
     */
    boolean update(TFireGasHostBaseInfo hostInfo);

    /**
     * 删除火气系统主机设备
     *
     * @param deviceId 设备ID
     * @return 是否成功
     */
    boolean delete(String deviceId);

}