package cn.stylefeng.guns.modular.nodeSystem.constants.dict;

import lombok.Getter;

/**
 * 管线编码枚举
 */
@Getter
public enum PipelineCodeEnum {

    JINING_LINE("JLX", "冀宁线"),
    ZHONGCANG_CONNECTION_LINE("ZCLJX", "中沧连接线"),
    CHINA_RUSSIA_EASTERN_LINE("ZEDX", "中俄东线"),
    PINGTAI_LINE("PTX", "平泰线"),
    YUJI_LINE("YJX", "榆济线"),
    TAIQINGWEI("TQW", "泰青威"),
    RIPULUO_LINE("RPLX", "日濮洛线"),
    RIDONG_LINE("RDX", "日东线"),
    RIYI_LINE("RYX", "日仪线"),
    SHANDONG_LNG("SDLNG", "山东LNG"),
    ANJI_LINE("AJX", "安济线"),
    GANGZAO_LINE("GZX", "港枣线"),
    LUWAN_PHASE_TWO("LWEQ", "鲁皖二期"),
    LUWAN_PHASE_ONE("LWYQ", "鲁皖一期"),
    TIANJIN_LNG("TJLNG", "天津LNG管线"),
    LINJI_DUPLEX_LINE("LJFX", "临济复线"),
    QINGNING_LINE("QNX", "青宁线"),
    JIQING_PHASE_TWO("JQEX", "济青二线"),
    DONGLIN_DUPLEX_LINE("DLFX", "东临复线"),
    DONGDONG_LINE("DDX", "董东线");

    private final String code;
    private final String description;

    PipelineCodeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static PipelineCodeEnum getByCode(String code) {
        for (PipelineCodeEnum pipelineCode : values()) {
            if (pipelineCode.code.equals(code)) {
                return pipelineCode;
            }
        }
        return null;
    }

    public static String getDescriptionByCode(String code) {
        for (PipelineCodeEnum pipelineCode : values()) {
            if (pipelineCode.code.equals(code)) {
                return pipelineCode.description;
            }
        }
        return null;
    }

}
