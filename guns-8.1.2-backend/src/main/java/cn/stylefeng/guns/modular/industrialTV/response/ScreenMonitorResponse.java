package cn.stylefeng.guns.modular.industrialTV.response;

import cn.stylefeng.guns.database.entity.TIndustrialTvBaseInfo;
import cn.stylefeng.guns.database.entity.TStationAreaBaseInfo;
import cn.stylefeng.guns.database.entity.TStationBaseInfo;
import lombok.Data;

import java.util.List;

@Data
public class ScreenMonitorResponse extends TStationBaseInfo {

    private List<StationAreaInfo> stationAreaInfoList;

    @Data
    public static class StationAreaInfo extends TStationAreaBaseInfo {

        private List<TIndustrialTvBaseInfo> industrialTvBaseInfoList;

    }

}
