package cn.stylefeng.guns.modular.workarea.service;

import cn.stylefeng.guns.modular.workarea.entity.TWorkareaConfig;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 作业区配置Service接口
 *
 * @author system
 * @date 2026-01-20
 */
public interface TWorkareaConfigService extends IService<TWorkareaConfig> {

    /**
     * 获取作业区URL
     * @return 作业区URL
     */
    String getWorkareaUrl();

    /**
     * 保存或更新作业区配置
     * @param workareaConfig 作业区配置
     * @return 是否成功
     */
    boolean saveOrUpdateConfig(TWorkareaConfig workareaConfig);
    
    /**
     * 获取最新的作业区配置详情
     * @return 作业区配置详情
     */
    TWorkareaConfig getLatestConfig();

//    /**
//     * 获取用户所属作业区和管线
//     *
//     * @return 作业区列表
//     */
//    List<TWorkareaConfig> getUserWorkareas();
}