package cn.stylefeng.guns.modular.alarmconfig.service;

import cn.stylefeng.guns.database.entity.TAlarmConfig;
import cn.stylefeng.guns.modular.alarmconfig.request.AlarmConfigRequest;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;

import java.util.List;

/**
 * 报警配置Service接口
 *
 * @author system
 * @date 2026-01-14
 */
public interface AlarmConfigService {

    /**
     * 查询列表
     *
     * @param request 查询请求
     * @return 报警配置列表
     */
    PageResult<TAlarmConfig> page(AlarmConfigRequest request);

    /**
     * 根据ID查询
     *
     * @param request 查询请求
     * @return 报警配置
     */
    TAlarmConfig getById(AlarmConfigRequest request);

    /**
     * 新增
     *
     * @param request 新增请求
     * @return 是否成功
     */
    boolean save(AlarmConfigRequest request);

    /**
     * 更新
     *
     * @param request 更新请求
     * @return 是否成功
     */
    boolean updateById(AlarmConfigRequest request);

    /**
     * 删除
     *
     * @param request 删除请求
     * @return 是否成功
     */
    boolean removeById(AlarmConfigRequest request);

    /**
     * 批量删除
     *
     * @param request 批量删除请求
     * @return 是否成功
     */
    boolean removeByIds(AlarmConfigRequest request);
}
