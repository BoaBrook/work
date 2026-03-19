package cn.stylefeng.guns.modular.nodeSystem.constants.dict;

import lombok.Getter;

/**
 * 处置类型枚举
 */
@Getter
public enum DisposalTypeEnum {

    FALSE_ALARM("01", "为误报"),
    MANUAL_PROCESSING("02", "人工处理"),
    NO_PROCESSING_REQUIRED("03", "无需处理"),
    OTHER("04", "其它");

    private final String code;
    private final String description;

    DisposalTypeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static DisposalTypeEnum getByCode(String code) {
        for (DisposalTypeEnum disposalType : values()) {
            if (disposalType.code.equals(code)) {
                return disposalType;
            }
        }
        return null;
    }

    public static String getDescriptionByCode(String code) {
        for (DisposalTypeEnum disposalType : values()) {
            if (disposalType.code.equals(code)) {
                return disposalType.description;
            }
        }
        return null;
    }

}
