package cn.stylefeng.guns.modular.accesscontrol.response;

import lombok.Data;

@Data
public class StaffCountResponse {
    private long innerCount = 0;//内部人员数量
    private long outerCount = 0;//外部人员数量
    private long innerVisitorCount = 0;//内部访客数量
    private long buildCount = 0;//施工人员数量
}
