package cn.stylefeng.guns.modular.broadcast.remotePlayVoice;

import cn.stylefeng.guns.database.entity.TEmergencyBroadcastHostBaseInfo;

import java.io.IOException;
import java.util.List;

public interface BroadcastPlayer {

    String getStationCode();

    boolean play(List<TEmergencyBroadcastHostBaseInfo> broadcastList, String voiceId) throws IOException;

    void stop(String stationId);

    void pause(String stationId);

    void resume(String stationId);

}
