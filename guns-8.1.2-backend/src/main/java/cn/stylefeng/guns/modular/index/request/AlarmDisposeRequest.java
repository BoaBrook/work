package cn.stylefeng.guns.modular.index.request;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class AlarmDisposeRequest {

    /**
     * 处理结果
     */
    private String processResult;

    /**
     * 处理记录
     */
    private String processRemark;

    /**
     * 处理人
     */
    private String processUser;

    /**
     * 处理时间
     */
    private Date processTime;

    /**
     * 报警记录id
     */
    private List<String> alarmRecordIds;

}
