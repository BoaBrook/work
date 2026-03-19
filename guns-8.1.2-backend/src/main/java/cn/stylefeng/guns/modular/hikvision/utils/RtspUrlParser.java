package cn.stylefeng.guns.modular.hikvision.utils;

import lombok.Data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RtspUrlParser {

    // 定义结果类
    @Data
    public static class RtspInfo {
        private String username;
        private String password;
        private String nvrIp;
        private int port;
        private int channelNo;

        // 构造函数
        public RtspInfo(String username, String password, String nvrIp, int port, int channelNo) {
            this.username = username;
            this.password = password;
            this.nvrIp = nvrIp;
            this.port = port;
            this.channelNo = channelNo;
        }

        @Override
        public String toString() {
            return String.format(
                    "RtspInfo{username='%s', password='%s', nvrIp='%s', port=%d, channelNo=%d}",
                    username, password, nvrIp, port, channelNo
            );
        }
    }

    /**
     * 解析海康威视 RTSP 地址，提取用户名、密码、NVR IP、端口、通道号
     *
     * @param rtspUrl RTSP 地址，格式如：rtsp://admin:pass@192.168.1.64:554/Streaming/Channels/101
     * @return RtspInfo 对象
     * @throws IllegalArgumentException 如果格式不合法
     */
    public static RtspInfo parseHikvisionRtspUrl(String rtspUrl) {
        if (rtspUrl == null || !rtspUrl.startsWith("rtsp://")) {
            throw new IllegalArgumentException("RTSP URL 必须以 rtsp:// 开头");
        }

        // 正则表达式解释：
        // rtsp://([^:]+):(.+)@([\d\.]+):(\d+)/Streaming/Channels/(\d+)
        // group1: username
        // group2: password (使用贪婪匹配，可以包含@)
        // group3: IP
        // group4: port
        // group5: channelCode (e.g., 101, 202)
        // 注意：密码可能包含@，所以使用 (.+) 而不是 ([^@]+)，并依赖后续的固定模式来正确分割
        String regex = "^rtsp://([^:]+):(.+)@(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}):(\\d+)/Streaming/Channels/(\\d+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(rtspUrl);

        if (!matcher.matches()) {
            throw new IllegalArgumentException("RTSP URL 格式不正确，无法解析: " + rtspUrl);
        }

        String username = matcher.group(1);
        String password = matcher.group(2);
        String nvrIp = matcher.group(3);
        int port = Integer.parseInt(matcher.group(4));
        String channelCodeStr = matcher.group(5);

        // 验证 IP 格式（简单校验）
        if (!nvrIp.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
            throw new IllegalArgumentException("无效的 IP 地址: " + nvrIp);
        }

        // 解析通道号：去掉末尾两位（01/02），前面的就是通道号
        if (channelCodeStr.length() < 3) {
            throw new IllegalArgumentException("通道编码长度不足，应为至少3位数字（如101）");
        }
        String channelNoStr = channelCodeStr.substring(0, channelCodeStr.length() - 2);
        int channelNo;
        try {
            channelNo = Integer.parseInt(channelNoStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("通道编码格式错误，无法解析通道号: " + channelCodeStr);
        }

        return new RtspInfo(username, password, nvrIp, port, channelNo);
    }

}
