package cn.stylefeng.guns.modular.broadcast.remoteClient.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 音乐信息
 */
@Data
public class MusicInfo {
    /**
     * 音乐信息对象
     */
    @JsonProperty("MusicInfo")
    private MusicInfoData MusicInfo;

    @Data
    public static class MusicInfoData {
        /**
         * 音乐目录数组
         */
        @JsonProperty("DirArray")
        private List<Dir> DirArray;

        /**
         * 音乐数组
         */
        @JsonProperty("MusicArray")
        private List<Music> MusicArray;
    }

    @Data
    public static class Dir {
        /**
         * 目录总时长（秒）
         */
        @JsonProperty("DirAudioTime")
        private Integer DirAudioTime;

        /**
         * 音乐目录ID
         */
        @JsonProperty("DirID")
        private Integer DirID;

        /**
         * 目录名称
         */
        @JsonProperty("DirName")
        private String DirName;
    }

    @Data
    public static class Music {
        /**
         * 音频ID
         */
        @JsonProperty("AudioId")
        private Integer AudioId;

        /**
         * 音乐名称
         */
        @JsonProperty("AudioName")
        private String AudioName;

        /**
         * 目录ID
         */
        @JsonProperty("DirID")
        private Integer DirID;

        /**
         * 音乐时长（秒）
         */
        @JsonProperty("Duration")
        private Integer Duration;
    }
}