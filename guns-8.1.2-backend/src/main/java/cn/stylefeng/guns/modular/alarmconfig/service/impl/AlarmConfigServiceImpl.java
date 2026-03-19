package cn.stylefeng.guns.modular.alarmconfig.service.impl;

import cn.stylefeng.guns.database.entity.TAlarmConfig;
import cn.stylefeng.guns.database.service.TAlarmConfigService;
import cn.stylefeng.guns.modular.alarmconfig.request.AlarmConfigRequest;
import cn.stylefeng.guns.modular.alarmconfig.service.AlarmConfigService;
import cn.stylefeng.roses.kernel.db.api.factory.PageFactory;
import cn.stylefeng.roses.kernel.db.api.factory.PageResultFactory;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 报警配置Service实现
 *
 * @author system
 * @date 2026-01-14
 */
@Service
public class AlarmConfigServiceImpl implements AlarmConfigService {

    @Autowired
    private TAlarmConfigService tAlarmConfigService;

    @Override
    public PageResult<TAlarmConfig> page(AlarmConfigRequest request) {
        LambdaQueryWrapper<TAlarmConfig> wrapper = buildQueryWrapper(request);
        Page<TAlarmConfig> page = PageFactory.defaultPage(request);
        Page<TAlarmConfig> resultPage = tAlarmConfigService.page(page, wrapper);
        return PageResultFactory.createPageResult(resultPage);
    }

    /**
     * 构建查询条件
     *
     * @param request 查询请求
     * @return 查询条件包装器
     */
    private LambdaQueryWrapper<TAlarmConfig> buildQueryWrapper(AlarmConfigRequest request) {
        LambdaQueryWrapper<TAlarmConfig> wrapper = new LambdaQueryWrapper<>();

        // 根据名称模糊查询
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            wrapper.like(TAlarmConfig::getName, request.getName());
        }

        wrapper.orderByDesc(TAlarmConfig::getCreateTime);

        return wrapper;
    }

    @Override
    public TAlarmConfig getById(AlarmConfigRequest request) {
        return tAlarmConfigService.getById(request.getConfigId());
    }

    @Override
    public boolean save(AlarmConfigRequest request) {
        TAlarmConfig alarmConfig = convertToEntity(request);
        return tAlarmConfigService.save(alarmConfig);
    }

    @Override
    public boolean updateById(AlarmConfigRequest request) {
        TAlarmConfig alarmConfig = convertToEntity(request);
        return tAlarmConfigService.updateById(alarmConfig);
    }

    @Override
    public boolean removeById(AlarmConfigRequest request) {
        return tAlarmConfigService.removeById(request.getConfigId());
    }

    @Override
    public boolean removeByIds(AlarmConfigRequest request) {
        return tAlarmConfigService.removeByIds(request.getConfigIds());
    }

    /**
     * 将Request转换为Entity
     *
     * @param request 请求对象
     * @return 实体对象
     */
    private TAlarmConfig convertToEntity(AlarmConfigRequest request) {
        TAlarmConfig entity = new TAlarmConfig();
        BeanUtils.copyProperties(request, entity);
        return entity;
    }
}
