package cn.stylefeng.guns.modular.linkagealarm.service;

import cn.stylefeng.guns.database.entity.TLinkageAlarmConfig;
import cn.stylefeng.guns.modular.linkagealarm.request.LinkageAlarmRequest;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;

import java.util.List;

/**
 * 联动报警配置Service接口
 *
 * @author system
 * @date 2026-01-14
 */
public interface LinkageAlarmService {

    /**
     * 查询列表
     *
     * @param request 查询请求
     * @return 联动报警配置列表
     */
    List<TLinkageAlarmConfig> list(LinkageAlarmRequest request);

    /**
     * 分页查询列表
     *
     * @param request 查询请求
     * @return 分页结果
     */
    PageResult<TLinkageAlarmConfig> page(LinkageAlarmRequest request);

    /**
     * 根据ID查询
     *
     * @param request 查询请求
     * @return 联动报警配置
     */
    TLinkageAlarmConfig getById(LinkageAlarmRequest request);

    /**
     * 新增
     *
     * @param request 新增请求
     * @return 是否成功
     */
    boolean save(LinkageAlarmRequest request);

    /**
     * 更新
     *
     * @param request 更新请求
     * @return 是否成功
     */
    boolean updateById(LinkageAlarmRequest request);

    /**
     * 删除
     *
     * @param request 删除请求
     * @return 是否成功
     */
    boolean removeById(LinkageAlarmRequest request);

    /**
     * 批量删除
     *
     * @param request 批量删除请求
     * @return 是否成功
     */
    boolean removeByIds(LinkageAlarmRequest request);

    /**
     * 更新状态（开启/关闭）
     *
     * @param linkageAlarmId 联动报警ID
     * @param status 状态（0-关闭，1-开启）
     * @return 是否成功
     */
    boolean updateStatus(String linkageAlarmId, String status);
}
