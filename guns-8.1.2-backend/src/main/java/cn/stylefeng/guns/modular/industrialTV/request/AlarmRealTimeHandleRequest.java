package cn.stylefeng.guns.modular.industrialTV.request;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class AlarmRealTimeHandleRequest {
    // 报警记录id
    private String msgId;
    // 处理结果 deal 已处理 undeal 未处理
    private String dealResult;
    // 处理说明
    private String dealRemark;
    // 正常/误报
    private String msgTag;
    // 处理人
    private String dealUserName;
    // 处理时间（时间戳）
    private Date dealTime;
    // 文件列表
    private List<String> imageUrl;

}