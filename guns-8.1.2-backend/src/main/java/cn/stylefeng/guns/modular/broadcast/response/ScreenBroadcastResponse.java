package cn.stylefeng.guns.modular.broadcast.response;

import cn.stylefeng.guns.database.entity.TEmergencyBroadcastHostBaseInfo;
import cn.stylefeng.guns.database.entity.TStationAreaBaseInfo;
import cn.stylefeng.guns.database.entity.TStationBaseInfo;
import lombok.Data;

import java.util.List;

@Data
public class ScreenBroadcastResponse extends TStationBaseInfo {

    private List<StationAreaInfo> stationAreaInfoList;

    @Data
    public static class StationAreaInfo extends TStationAreaBaseInfo {

        private List<TEmergencyBroadcastHostBaseInfo> emergencyBroadcastHostBaseInfoList;

    }

}
