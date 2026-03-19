package cn.stylefeng.guns.modular.workarea.service.impl;

import cn.stylefeng.guns.modular.workarea.entity.TWorkareaConfig;
import cn.stylefeng.guns.modular.workarea.mapper.TWorkareaConfigMapper;
import cn.stylefeng.guns.modular.workarea.service.TWorkareaConfigService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * 作业区配置Service实现类
 *
 * @author system
 * @date 2026-01-20
 */
@Service
@Slf4j
public class TWorkareaConfigServiceImpl extends ServiceImpl<TWorkareaConfigMapper, TWorkareaConfig> implements TWorkareaConfigService {

    @Override
    public String getWorkareaUrl() {
        try {
            TWorkareaConfig workareaConfig = this.getLatestConfig();
            if (workareaConfig != null) {
                return workareaConfig.getWorkareaUrl();
            } else {
                log.warn("未找到作业区配置记录");
                return null;
            }
        } catch (Exception e) {
            log.error("获取作业区URL失败", e);
            return null;
        }
    }
    
    @Override
    public TWorkareaConfig getLatestConfig() {
        try {
            LambdaQueryWrapper<TWorkareaConfig> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.orderByDesc(TWorkareaConfig::getUpdateTime);
            queryWrapper.last("limit 1");
            return this.getOne(queryWrapper, false);
        } catch (Exception e) {
            log.error("获取作业区配置详情失败", e);
            return null;
        }
    }

    @Override
    public boolean saveOrUpdateConfig(TWorkareaConfig workareaConfig) {
        try {
            // 参数验证
            if (workareaConfig == null) {
                log.error("作业区配置对象不能为空");
                return false;
            }
            
            if (StringUtils.isBlank(workareaConfig.getWorkareaUrl())) {
                log.error("作业区URL不能为空");
                return false;
            }

            Date now = new Date();
            workareaConfig.setUpdateTime(now);
            workareaConfig.setCreateTime(now);

            return this.save(workareaConfig);
        } catch (Exception e) {
            log.error("保存或更新作业区配置失败", e);
            return false;
        }
    }
}