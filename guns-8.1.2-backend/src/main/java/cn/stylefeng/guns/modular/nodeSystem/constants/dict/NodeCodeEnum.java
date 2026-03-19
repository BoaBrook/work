package cn.stylefeng.guns.modular.nodeSystem.constants.dict;

import lombok.Getter;

/**
 * 节点编码枚举
 */
@Getter
public enum NodeCodeEnum {

    DEZHOU("DZJD", "德州节点"),
    JINAN("JNJD", "济南节点"),
    TAIAN("TAJD", "泰安节点"),
    JINAN_WEST("JNXJD", "济南西节点"),
    QINGDAO("QDJD", "青岛节点"),
    RIZHAO("RZJD", "日照节点"),
    WEIFANG("WFJD", "潍坊节点");

    private final String code;
    private final String description;

    NodeCodeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static NodeCodeEnum getByCode(String code) {
        for (NodeCodeEnum nodeCode : values()) {
            if (nodeCode.code.equals(code)) {
                return nodeCode;
            }
        }
        return null;
    }

    public static String getDescriptionByCode(String code) {
        for (NodeCodeEnum nodeCode : values()) {
            if (nodeCode.code.equals(code)) {
                return nodeCode.description;
            }
        }
        return null;
    }

}
