package cn.stylefeng.guns.modular.index.constants;

public enum DeviceStatusEnum {

    ONLINE("online", "在线"),
    OFFLINE("offline", "离线"),
    ALARM("alarm", "报警");

    private final String code;
    private final String message;

    DeviceStatusEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static DeviceStatusEnum getByCode(String code) {
        for (DeviceStatusEnum deviceStatusEnum : DeviceStatusEnum.values()) {
            if (deviceStatusEnum.getCode().equals(code)) {
                return deviceStatusEnum;
            }
        }
        return null;
    }

    public static DeviceStatusEnum getByMessage(String message) {
        for (DeviceStatusEnum deviceStatusEnum : DeviceStatusEnum.values()) {
            if (deviceStatusEnum.getMessage().equals(message)) {
                return deviceStatusEnum;
            }
        }
        return null;
    }

    public static DeviceStatusEnum getByCodeOrMessage(String codeOrMessage) {
        for (DeviceStatusEnum deviceStatusEnum : DeviceStatusEnum.values()) {
            if (deviceStatusEnum.getCode().equals(codeOrMessage) || deviceStatusEnum.getMessage().equals(codeOrMessage)) {
                return deviceStatusEnum;
            }
        }
        return null;
    }

}
