package cn.stylefeng.guns.zlmediakit.dto;

import lombok.Data;

import java.util.List;

@Data
public class MediaListResponseDTO {
    private Integer code;
    private List<MediaData> data;

    @Data
    public static class MediaData{
        private String app;
        private String vhost;
        private String stream;
        private String schema;
        private String originUrl; //拉流url
        private Integer readerCount;
        private Integer totalReaderCount;
    }
}
