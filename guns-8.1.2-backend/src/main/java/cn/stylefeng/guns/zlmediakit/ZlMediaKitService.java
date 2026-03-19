package cn.stylefeng.guns.zlmediakit;

import cn.stylefeng.guns.core.utils.StringUtils;
import cn.stylefeng.guns.zlmediakit.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ZlMediaKitService{

    private static final String ADD_STREAM_PROXY_URL = "/index/api/addStreamProxy";
    private static final String GET_MEDIA_LIST_URL = "/index/api/getMediaList?secret={secret}";
    private static final String IS_MEDIA_ONLINE_URL = "/index/api/isMediaOnline";
    private static final String START_RECORD_URL = "/index/api/startRecord";
    private static final String STOP_RECORD_URL = "/index/api/stopRecord";
    private static final String SNAP_URL = "/index/api/getSnap";
    private static final String DEL_STREAM_PROXY_URL = "/index/api/delStreamPusherProxy";

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ZlMediaKitConfig zlMediaKitConfig;

    private final ConcurrentHashMap<String, Object> RTSP_LOCKS = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ZlMediaCacheDTO> RTSP_RESULT_MAP = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> RECORD_RESULT_MAP = new ConcurrentHashMap<>();

    public ZlMediaCacheDTO getPlayUrl(String rtspUrl){
        Object lock = RTSP_LOCKS.computeIfAbsent(rtspUrl, k -> new Object());
        synchronized (lock){
            try{
                ZlMediaCacheDTO cache ;
                List<MediaListResponseDTO.MediaData> mediaDataList = getMediaMap().get(rtspUrl);
                if(StringUtils.isEmpty(mediaDataList)){//zlmediakit重启
                    cache = play(rtspUrl);
                }else{
                    cache = RTSP_RESULT_MAP.get(rtspUrl);
                    if(StringUtils.isEmpty(cache)){//scada重启
                        MediaListResponseDTO.MediaData mediaData = mediaDataList.get(0);
                        cache = updateCache(rtspUrl, mediaData.getStream());
                    }
                }
                return cache;
            }catch (Exception e){
                log.error("getPlayUrl error,rtspUrl={}", rtspUrl, e);
                throw new RuntimeException("getPlayUrl error : "+ e.getMessage());
            }finally {
                RTSP_LOCKS.remove(rtspUrl);
            }
        }
    }

    public ZlMediaCacheDTO getRecordPlayUrl(String rtspUrl, Date startTime, Date endTime) {
        String key = RECORD_RESULT_MAP.get(rtspUrl);
        if(!StringUtils.isEmpty(key)){
            String delUrl = zlMediaKitConfig.getServer() + DEL_STREAM_PROXY_URL + "?secret=" + zlMediaKitConfig.getSecret() + "&key=" + key;
            restTemplate.getForObject(delUrl, Object.class);
        }
        ZonedDateTime utcTimeStart = startTime.toInstant().atZone(ZoneOffset.UTC);
        ZonedDateTime utcTimeEnd = endTime.toInstant().atZone(ZoneOffset.UTC);
        String rtspUrlFinal = rtspUrl+"?starttime="+utcTimeStart.format(formatter)+"&endtime="+utcTimeEnd.format(formatter);
        try {
            String stream = generateShortHash(rtspUrl);
            ZlMediaStreamRequestDTO requestDTO = new ZlMediaStreamRequestDTO();
            BeanUtils.copyProperties(zlMediaKitConfig, requestDTO);
            requestDTO.setUrl(rtspUrlFinal);
            requestDTO.setStream(stream);
            String url = zlMediaKitConfig.getServer() + ADD_STREAM_PROXY_URL;
            ZlMediaStreamResponseDTO responseDTO = restTemplate.postForObject(url, requestDTO, ZlMediaStreamResponseDTO.class);
            if(responseDTO.getCode() == -1){
                log.error("add stream to zlmediakit error: {}", responseDTO.getMsg());
                throw new RuntimeException("getRecordPlayUrl error, msg:"+ responseDTO.getMsg());
            }
            RECORD_RESULT_MAP.put(rtspUrl, responseDTO.getData().getKey());
            return new ZlMediaCacheDTO(getPlayServerPath(zlMediaKitConfig.getServer()), zlMediaKitConfig.getApp(), stream);
        }catch (Exception e){
            log.error("getRecordPlayUrl error,rtspUrl={}", rtspUrl, e);
            throw new RuntimeException("getRecordPlayUrl error : "+ e.getMessage());
        }
    }

    private ZlMediaCacheDTO play(String rtspUrl) throws Exception {
        String stream = generateShortHash(rtspUrl);
        ZlMediaStreamRequestDTO requestDTO = new ZlMediaStreamRequestDTO();
        BeanUtils.copyProperties(zlMediaKitConfig, requestDTO);
        requestDTO.setUrl(rtspUrl);
        requestDTO.setStream(stream);
        String url = zlMediaKitConfig.getServer() + ADD_STREAM_PROXY_URL;
        ZlMediaStreamResponseDTO responseDTO = restTemplate.postForObject(url, requestDTO, ZlMediaStreamResponseDTO.class);
        if(responseDTO.getCode() == -1){
            log.error("add stream to zlmediakit error: {}", responseDTO.getMsg());
            throw new RuntimeException("play video error, msg:"+ responseDTO.getMsg());
        }
        return updateCache(rtspUrl, stream);
    }

    private ZlMediaCacheDTO updateCache(String rtspUrl, String stream) throws Exception {
        ZlMediaCacheDTO cache = new ZlMediaCacheDTO(getPlayServerPath(zlMediaKitConfig.getServer()), zlMediaKitConfig.getApp(), stream);
        RTSP_RESULT_MAP.put(rtspUrl, cache);
        return cache;
    }

    private Map<String, List<MediaListResponseDTO.MediaData>> getMediaMap(){
        String url = zlMediaKitConfig.getServer() + GET_MEDIA_LIST_URL;
        Map<String, Object> param = new HashMap<>();
        param.put("secret", zlMediaKitConfig.getSecret());
        MediaListResponseDTO responseDTO = restTemplate.getForObject(url, MediaListResponseDTO.class, param);
        log.debug("zlmedia getMediaList, results:{}", responseDTO);
        if(StringUtils.isEmpty(responseDTO.getData())){
            return new HashMap<>();
        }
        return responseDTO.getData().stream().collect(Collectors.groupingBy(MediaListResponseDTO.MediaData::getOriginUrl));
    }

    private String getPlayServerPath(String server) throws Exception {
        URL hostUrl = new URL(server);
        String host = hostUrl.getHost();
        int port = hostUrl.getPort();
        if(port == -1){
            port = hostUrl.getDefaultPort();
        }
        return host+":"+port;
    }

    public String generateShortHash(String input) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-512");
        byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));

        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        // 截取前 8 位
        return hexString.toString().substring(0, 8);
    }

    // 判断是否在线
    public Boolean isMediaOnlie(String rtspUrl) {
        try{
            String stream = generateShortHash(rtspUrl);
            MediaOnlineRequestDTO requestDTO = new MediaOnlineRequestDTO();
            BeanUtils.copyProperties(zlMediaKitConfig, requestDTO);
            requestDTO.setStream(stream);
            String url = zlMediaKitConfig.getServer() + IS_MEDIA_ONLINE_URL+
                    "?secret="+zlMediaKitConfig.getSecret()+"&vhost="+zlMediaKitConfig.getVhost()+
                    "&app="+zlMediaKitConfig.getApp()+"&stream="+stream+"&schema="+requestDTO.getScheme();
            MediaOnlineDTO responseDTO = restTemplate.getForObject(url,  MediaOnlineDTO.class,requestDTO);
            return responseDTO.getOnline();
        }catch (Exception e){
            return false;
        }
    }

    // 开始录像
    public Boolean startRecord(String rtspUrl) throws Exception {
        if(!isMediaOnlie(rtspUrl)){
            play(rtspUrl);
        }
        String stream = generateShortHash(rtspUrl);
        RecordRequestDTO requestDTO = new RecordRequestDTO();
        BeanUtils.copyProperties(zlMediaKitConfig, requestDTO);
        requestDTO.setCustomized_path(zlMediaKitConfig.getVideoSavePath());
        requestDTO.setStream(stream);
        String url = zlMediaKitConfig.getServer() + START_RECORD_URL;
        RecordResponseDTO responseDTO = restTemplate.postForObject(url, requestDTO, RecordResponseDTO.class);
        return responseDTO.getCode() == 0;
    }

    // 停止录像
    public Boolean stopRecord(String rtspUrl) throws Exception {
        if(!isMediaOnlie(rtspUrl)){
            return false;
        }
        String stream = generateShortHash(rtspUrl);
        RecordRequestDTO requestDTO = new RecordRequestDTO();
        BeanUtils.copyProperties(zlMediaKitConfig, requestDTO);
        requestDTO.setStream(stream);
        String url = zlMediaKitConfig.getServer() + STOP_RECORD_URL;
        RecordResponseDTO responseDTO = restTemplate.postForObject(url, requestDTO, RecordResponseDTO.class);
        return responseDTO.getCode() == 0;
    }

    public byte[] getSnap(String rtspUrl) {
        SnapRequestDTO requestDTO = new SnapRequestDTO();
        BeanUtils.copyProperties(zlMediaKitConfig, requestDTO);
        requestDTO.setUrl(rtspUrl);
        String url = zlMediaKitConfig.getServer() + SNAP_URL;
        return restTemplate.postForObject(url, requestDTO, byte[].class);
    }

}
