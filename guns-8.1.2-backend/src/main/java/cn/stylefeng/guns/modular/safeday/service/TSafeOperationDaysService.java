package cn.stylefeng.guns.modular.safeday.service;

import cn.stylefeng.guns.modular.safeday.entity.SafeDayConfigRow;
import cn.stylefeng.guns.modular.safeday.entity.SafeDayPageRequest;
import cn.stylefeng.guns.modular.safeday.entity.SafeDaysParam;
import cn.stylefeng.guns.modular.safeday.entity.TSafeOperationDays;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.Map;

/**
 * 安全运行天数 Service接口
 *
 * @author system
 * @date 2026-01-19
 */
public interface TSafeOperationDaysService extends IService<TSafeOperationDays> {

    /**
     * 根据站点ID获取当前安全运行天数和定义
     * @param stationId 站点ID
     * @return 包含安全运行天数和定义的Map
     */
    Map<String, Object> getCurrentSafeDaysByStationId(String stationId);

    /**
     * 分页获取页面内容配置列表
     * @param request 分页参数
     * @return 分页结果
     */
    PageResult<SafeDayConfigRow> pageConfigList(SafeDayPageRequest request);

    /**
     * 保存安全运行开始日期配置
     * @return 是否成功
     */
    boolean saveConfig(SafeDaysParam params);

}