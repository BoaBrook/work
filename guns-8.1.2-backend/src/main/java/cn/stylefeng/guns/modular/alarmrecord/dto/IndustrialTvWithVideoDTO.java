package cn.stylefeng.guns.modular.alarmrecord.dto;

import cn.stylefeng.guns.database.entity.TIndustrialTvBaseInfo;
import cn.stylefeng.guns.zlmediakit.dto.ZlMediaCacheDTO;
import lombok.Data;

/**
 * 工业电视及其关联录像信息DTO
 *
 * @author system
 */
@Data
public class IndustrialTvWithVideoDTO {

    /**
     * 工业电视设备信息
     */
    private TIndustrialTvBaseInfo industrialTv;

    /**
     * 关联的录像信息（报警时间对应的录像片段）
     */
    private ZlMediaCacheDTO zlMediaCacheDTO;

}
