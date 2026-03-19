package cn.stylefeng.guns.modular.nodeSystem.util;

import cn.hutool.crypto.digest.DigestUtil;

public class SignatureUtil {

    public static String generateSign(String nodeCode, String appId, String appSecretKey, Long reportTime) {
        String signatureString = (nodeCode + appId + appSecretKey + reportTime).toUpperCase();
        return DigestUtil.md5Hex(signatureString);
    }

}
