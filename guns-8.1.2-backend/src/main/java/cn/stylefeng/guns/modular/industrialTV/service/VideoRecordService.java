package cn.stylefeng.guns.modular.industrialTV.service;

import cn.stylefeng.guns.database.entity.TIndustrialTvBaseInfo;
import cn.stylefeng.guns.database.service.TIndustrialTvBaseInfoService;
import cn.stylefeng.guns.modular.videoStreamMedia.client.VideoStreamMediaClient;
import cn.stylefeng.guns.modular.videoStreamMedia.dto.VideoStreamMediaResponse;
import cn.stylefeng.guns.zlmediakit.ZlMediaKitService;
import cn.stylefeng.guns.zlmediakit.dto.ZlMediaCacheDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class VideoRecordService {

    @Autowired
    private VideoStreamMediaClient videoStreamMediaClient;

    @Autowired
    private TIndustrialTvBaseInfoService tIndustrialTvBaseInfoService;

    @Autowired
    private ZlMediaKitService zlMediaKitService;

    public List<String> getCameraDates(String deviceId) throws Exception {
        TIndustrialTvBaseInfo tv = tIndustrialTvBaseInfoService.getById(deviceId);
        String cameraHash = zlMediaKitService.generateShortHash(tv.getStreamAddress());
        VideoStreamMediaResponse<List<String>> cameraDates = videoStreamMediaClient.getCameraDates(cameraHash);
        return cameraDates.getData();
    }

    public ZlMediaCacheDTO getVideoRecordStream(String deviceId, Date startTime, Date endTime) {
        TIndustrialTvBaseInfo tv = tIndustrialTvBaseInfoService.getById(deviceId);
        String recordRtsp = tv.getStreamAddress().replace("Channels", "tracks");
        return zlMediaKitService.getRecordPlayUrl(recordRtsp, startTime, endTime);
    }

}
