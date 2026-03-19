package cn.stylefeng.guns.modular.broadcast.remotePlayVoice;

import cn.stylefeng.guns.database.entity.TEmergencyBroadcastHostBaseInfo;
import cn.stylefeng.guns.database.entity.TVoiceBroadcastMaterialBaseInfo;
import cn.stylefeng.guns.database.service.TVoiceBroadcastMaterialBaseInfoService;
import cn.stylefeng.guns.modular.broadcast.VoiceFileManage.FileStorageService;
import cn.stylefeng.guns.modular.broadcast.remoteClient.client.BroadcastClient;
import cn.stylefeng.guns.modular.broadcast.remoteClient.dto.BroadcastResponse;
import cn.stylefeng.guns.modular.broadcast.remoteClient.dto.MediaGroupInfo;
import cn.stylefeng.guns.modular.broadcast.remoteClient.dto.MusicInfo;
import cn.stylefeng.guns.modular.broadcast.remoteClient.dto.TerminalInfo;
import cn.stylefeng.guns.modular.broadcast.remoteClient.factory.BroadcastClientFactory;
import cn.stylefeng.roses.kernel.file.api.pojo.response.SysFileInfoResponse;
import cn.stylefeng.roses.kernel.file.modular.service.SysFileInfoService;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class LyBroadcastPlayer implements BroadcastPlayer {

    private static final Integer VOLUME = 50;

    private static final Integer PRIORITY = 70;

    @Autowired
    private BroadcastClientFactory broadcastClientFactory;

    @Autowired
    private TVoiceBroadcastMaterialBaseInfoService tVoiceBroadcastMaterialBaseInfoService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private SysFileInfoService sysFileInfoService;

    @Value("${broadcast.host}")
    private String broadcastHost;

    @Value("${broadcast.port}")
    private Integer broadcastPort;

    @Value("${broadcast.username}")
    private String broadcastUsername;

    @Value("${broadcast.password}")
    private String broadcastPassword;

    @Override
    public String getStationCode() {
        return null;
    }

    @Override
    public boolean play(List<TEmergencyBroadcastHostBaseInfo> broadcastList, String voiceId) throws IOException {
        TVoiceBroadcastMaterialBaseInfo voice = tVoiceBroadcastMaterialBaseInfoService.getById(voiceId);
        for (TEmergencyBroadcastHostBaseInfo broadcast : broadcastList) {
            BroadcastClient broadcastClient = broadcastClientFactory.createClient(broadcastHost, broadcastPort, broadcastUsername, broadcastPassword);
            List<TerminalInfo> terminal = broadcastClient.getTerminalsById(Collections.singletonList(Integer.valueOf(broadcast.getDeviceCode())));
            if(CollectionUtils.isEmpty(terminal)) return false;
            BroadcastResponse<MusicInfo> serverMusicList = broadcastClient.getServerMusicList();
            MusicInfo.Music musicInfo = serverMusicList.getData().getMusicInfo().getMusicArray().stream().filter(music -> music.getAudioName().equals(voice.getVoiceName())).findFirst().orElse(null);
            // 当服务器上不存在该音频时，需要上传
            if (musicInfo == null) {
                // 首先检查有没有对应的媒体库
                boolean hasMediaGroup = false;
                if(CollectionUtils.isNotEmpty(serverMusicList.getData().getMusicInfo().getDirArray())){
                    long alarmDirCount = serverMusicList.getData().getMusicInfo().getDirArray().stream().filter(dir -> dir.getDirName().equals("告警音频")).count();
                    if(alarmDirCount > 0){
                        hasMediaGroup = true;
                    }
                }
                Integer mediaGroupId = null;
                if(!hasMediaGroup){
                    BroadcastResponse<MediaGroupInfo> alarmMediaGroupResponse = broadcastClient.createMediaGroup("告警音频", 0);
                    mediaGroupId = alarmMediaGroupResponse.getData().getId();
                }else{
                    MusicInfo.Dir alarmDir = serverMusicList.getData().getMusicInfo().getDirArray().stream().filter(dir -> dir.getDirName().equals("告警音频")).findFirst().orElse(null);
                    mediaGroupId = alarmDir.getDirID();
                }
                // 上传音频
                SysFileInfoResponse fileInfoResult = sysFileInfoService.getFileInfoResult(voice.getAudioFileId());
                broadcastClient.uploadMP3(mediaGroupId, fileInfoResult.getFileBytes(), voice.getVoiceName());
                serverMusicList = broadcastClient.getServerMusicList();
                musicInfo = serverMusicList.getData().getMusicInfo().getMusicArray().stream().filter(music -> music.getAudioName().equals(voice.getVoiceName())).findFirst().orElse(null);
            }
            broadcastClient.playServerMusic(Collections.singletonList(terminal.get(0).getEndpointID()),
                    new ArrayList<>(),
                    Collections.singletonList(musicInfo.getAudioId()),
                    new ArrayList<>(),
                    VOLUME,
                    "broadcast_type",
                    PRIORITY);
        }
        return true;
    }

    @Override
    public void stop(String stationId) {

    }

    @Override
    public void pause(String stationId) {

    }

    @Override
    public void resume(String stationId) {

    }

}
