package cn.stylefeng.guns.modular.index.request;

import cn.stylefeng.roses.kernel.rule.pojo.request.BaseRequest;
import lombok.Data;

import java.util.Date;

@Data
public class AlarmInfoRequest extends BaseRequest {

    private String stationId;

    private String systemType;

    /**
     * 报警等级
     */
    private String alarmLevel;

    /**
     * 处置状态
     */
    private String disposalStatus;

    /**
     * 报警时间-开始
     */
    private Date alarmTimeStart;

    /**
     * 报警时间-结束
     */
    private Date alarmTimeEnd;

}
