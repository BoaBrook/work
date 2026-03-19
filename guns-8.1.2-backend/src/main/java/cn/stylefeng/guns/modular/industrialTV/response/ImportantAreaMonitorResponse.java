package cn.stylefeng.guns.modular.industrialTV.response;

import cn.stylefeng.guns.database.entity.TIndustrialTvBaseInfo;
import cn.stylefeng.guns.database.entity.TIndustrialTvRollPoling;
import lombok.Data;

import java.util.List;

@Data
public class ImportantAreaMonitorResponse extends TIndustrialTvRollPoling {

    private List<TIndustrialTvBaseInfo> industrialTvBaseInfoList;

}
