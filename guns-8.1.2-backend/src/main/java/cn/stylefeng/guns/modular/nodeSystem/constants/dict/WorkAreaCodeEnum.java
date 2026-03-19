package cn.stylefeng.guns.modular.nodeSystem.constants.dict;

import lombok.Getter;

/**
 * 作业区编码枚举
 */
@Getter
public enum WorkAreaCodeEnum {

    ZIBO("ZB", "淄博作业区"),
    TAIAN("TA", "泰安作业区"),
    JINAN("JNA", "济南作业区"),
    CHANGQING("CQ", "长清作业区"),
    NINGYANG("NY", "宁阳作业区"),
    QIHE("QH", "齐河作业区"),
    DEZHOU("DZ", "德州作业区"),
    LINYI("BLY", "临邑作业区"),
    LIAOCHENG("LC", "聊城作业区"),
    HEZE("HZ", "菏泽作业区"),
    WEIFANG("WF", "潍坊作业区"),
    DONGYING("DY", "东营作业区"),
    BINZHOU("BZ", "滨州作业区"),
    CHANGYI("CY", "昌邑作业区"),
    JIAOZHOU("JZ", "胶州作业区"),
    ZAOZHUANG("ZZ", "枣庄作业区"),
    QUFU("QF", "曲阜作业区"),
    JINING("JNI", "济宁作业区"),
    LINYI_NLY("NLY", "临沂作业区"),
    PINGYI("PY", "平邑作业区"),
    RIZHAO("RZ", "日照作业区"),
    QINGDAO("QD", "青岛作业区"),
    YANTAI("YT", "烟台作业区");

    private final String code;
    private final String description;

    WorkAreaCodeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static WorkAreaCodeEnum getByCode(String code) {
        for (WorkAreaCodeEnum workAreaCode : values()) {
            if (workAreaCode.code.equals(code)) {
                return workAreaCode;
            }
        }
        return null;
    }

    public static String getDescriptionByCode(String code) {
        for (WorkAreaCodeEnum workAreaCode : values()) {
            if (workAreaCode.code.equals(code)) {
                return workAreaCode.description;
            }
        }
        return null;
    }

}
