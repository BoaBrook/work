package cn.stylefeng.guns.modular.broadcast.remoteClient.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * TTS引擎信息数组
 */
@Data
public class TTSEngineInfoArray {
    /**
     * TTS引擎信息数组
     */
    @JsonProperty("TTSEngineInfo")
    private List<TTSEngineInfo> TTSEngineInfo;

    @Data
    public static class TTSEngineInfo {
        /**
         * 引擎ID
         */
        @JsonProperty("EngineIndex")
        private Integer EngineIndex;

        /**
         * 引擎名称
         */
        @JsonProperty("EngineName")
        private String EngineName;
    }
}