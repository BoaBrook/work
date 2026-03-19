package cn.stylefeng.guns.database.mapper;

import cn.stylefeng.guns.database.entity.TAccessControlBaseInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 门禁设备基础信息表 Mapper接口
 *
 * @author system
 * @date 2026-01-14
 */
@Mapper
public interface TAccessControlBaseInfoMapper extends BaseMapper<TAccessControlBaseInfo> {

    long countByBelongStationId(@Param("belongStationId") String belongStationId,
                                                        @Param("deviceCode") String deviceCode,
                                                        @Param("ipAddress") String ipAddress);
}