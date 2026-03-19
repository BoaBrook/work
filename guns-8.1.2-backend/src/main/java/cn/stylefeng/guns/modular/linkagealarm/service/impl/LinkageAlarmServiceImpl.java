package cn.stylefeng.guns.modular.linkagealarm.service.impl;

import cn.stylefeng.guns.database.entity.TLinkageAlarmConfig;
import cn.stylefeng.guns.database.service.TLinkageAlarmConfigService;
import cn.stylefeng.guns.modular.linkagealarm.request.LinkageAlarmRequest;
import cn.stylefeng.guns.modular.linkagealarm.service.LinkageAlarmService;
import cn.stylefeng.roses.kernel.db.api.factory.PageFactory;
import cn.stylefeng.roses.kernel.db.api.factory.PageResultFactory;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;
import cn.stylefeng.roses.kernel.sys.api.SysUserServiceApi;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 联动报警配置Service实现
 *
 * @author system
 * @date 2026-01-14
 */
@Service
public class LinkageAlarmServiceImpl implements LinkageAlarmService {

    @Autowired
    private TLinkageAlarmConfigService tLinkageAlarmConfigService;
    @Autowired
    private SysUserServiceApi sysUserServiceApi;

    @Override
    public List<TLinkageAlarmConfig> list(LinkageAlarmRequest request) {
        LambdaQueryWrapper<TLinkageAlarmConfig> wrapper = buildQueryWrapper(request);
        return tLinkageAlarmConfigService.list(wrapper);
    }

    @Override
    public PageResult<TLinkageAlarmConfig> page(LinkageAlarmRequest request) {
        LambdaQueryWrapper<TLinkageAlarmConfig> wrapper = buildQueryWrapper(request);
        Page<TLinkageAlarmConfig> page = PageFactory.defaultPage(request);
        Page<TLinkageAlarmConfig> resultPage = tLinkageAlarmConfigService.page(page, wrapper);

        processData(resultPage.getRecords());
        return PageResultFactory.createPageResult(resultPage);
    }

    private void processData(List<TLinkageAlarmConfig> records) {
        if (CollectionUtils.isEmpty(records)) {
            return;
        }

        records.forEach(config -> config.setCreateUserName(sysUserServiceApi.getUserRealName(config.getCreateUser())));
    }

    /**
     * 构建查询条件
     *
     * @param request 查询请求
     * @return 查询条件包装器
     */
    private LambdaQueryWrapper<TLinkageAlarmConfig> buildQueryWrapper(LinkageAlarmRequest request) {
        LambdaQueryWrapper<TLinkageAlarmConfig> wrapper = new LambdaQueryWrapper<>();
        
        // 根据所属站场ID查询
        if (request.getBelongStationId() != null && !request.getBelongStationId().trim().isEmpty()) {
            wrapper.eq(TLinkageAlarmConfig::getBelongStationId, request.getBelongStationId());
        }
        
        // 根据子系统类型查询
        if (request.getSubsystemType() != null && !request.getSubsystemType().trim().isEmpty()) {
            wrapper.eq(TLinkageAlarmConfig::getSubsystemType, request.getSubsystemType());
        }
        
        // 根据报警类型查询
        if (request.getAlarmType() != null && !request.getAlarmType().trim().isEmpty()) {
            wrapper.eq(TLinkageAlarmConfig::getAlarmType, request.getAlarmType());
        }
        
        // 根据状态查询
        if (request.getStatus() != null && !request.getStatus().trim().isEmpty()) {
            wrapper.eq(TLinkageAlarmConfig::getStatus, request.getStatus());
        }
        
        // 根据名称模糊查询
        if (request.getLinkageAlarmName() != null && !request.getLinkageAlarmName().trim().isEmpty()) {
            wrapper.like(TLinkageAlarmConfig::getLinkageAlarmName, request.getLinkageAlarmName());
        }
        
        wrapper.orderByDesc(TLinkageAlarmConfig::getCreateTime);
        
        return wrapper;
    }

    @Override
    public TLinkageAlarmConfig getById(LinkageAlarmRequest request) {
        return tLinkageAlarmConfigService.getById(request.getLinkageAlarmId());
    }

    @Override
    public boolean save(LinkageAlarmRequest request) {
        TLinkageAlarmConfig entity = convertToEntity(request);
        // 新增时如果状态为空，默认为关闭状态（0）
        if (entity.getStatus() == null || entity.getStatus().trim().isEmpty()) {
            entity.setStatus("0");
        }
        return tLinkageAlarmConfigService.save(entity);
    }

    @Override
    public boolean updateById(LinkageAlarmRequest request) {
        TLinkageAlarmConfig entity = convertToEntity(request);
        return tLinkageAlarmConfigService.updateById(entity);
    }

    @Override
    public boolean removeById(LinkageAlarmRequest request) {
        return tLinkageAlarmConfigService.removeById(request.getLinkageAlarmId());
    }

    @Override
    public boolean removeByIds(LinkageAlarmRequest request) {
        return tLinkageAlarmConfigService.removeByIds(request.getLinkageAlarmIds());
    }

    @Override
    public boolean updateStatus(String linkageAlarmId, String status) {
        if (linkageAlarmId == null || linkageAlarmId.trim().isEmpty()) {
            throw new RuntimeException("联动报警ID不能为空");
        }
        if (status == null || (!"0".equals(status) && !"1".equals(status))) {
            throw new RuntimeException("状态值无效，必须为0（关闭）或1（开启）");
        }
        
        TLinkageAlarmConfig config = tLinkageAlarmConfigService.getById(linkageAlarmId);
        if (config == null) {
            throw new RuntimeException("未找到对应的联动报警配置");
        }
        
        config.setStatus(status);
        return tLinkageAlarmConfigService.updateById(config);
    }

    /**
     * 将Request转换为Entity
     *
     * @param request 请求对象
     * @return 实体对象
     */
    private TLinkageAlarmConfig convertToEntity(LinkageAlarmRequest request) {
        TLinkageAlarmConfig entity = new TLinkageAlarmConfig();
        BeanUtils.copyProperties(request, entity);
        return entity;
    }
}
