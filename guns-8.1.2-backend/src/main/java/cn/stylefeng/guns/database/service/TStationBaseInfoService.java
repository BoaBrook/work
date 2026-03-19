package cn.stylefeng.guns.database.service;

import cn.stylefeng.guns.database.entity.TStationBaseInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 站场基础信息表 Service接口
 *
 * @author system
 * @date 2026-01-14
 */
public interface TStationBaseInfoService extends IService<TStationBaseInfo> {

    String getBelongOperationAreaCode(TStationBaseInfo  stationBaseInfo);

    String getBelongOperationAreaName(TStationBaseInfo  stationBaseInfo);

    String getBelongPipelineCode(TStationBaseInfo  stationBaseInfo);

    String getBelongPipelineName(TStationBaseInfo  stationBaseInfo);

}