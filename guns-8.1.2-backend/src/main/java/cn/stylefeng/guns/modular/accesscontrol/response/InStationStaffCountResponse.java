package cn.stylefeng.guns.modular.accesscontrol.response;

import cn.stylefeng.guns.database.entity.TAccessControlPersonnelBaseInfo;
import lombok.Data;

import java.util.List;

@Data
public class InStationStaffCountResponse {
    private long totalCount = 0;//今日进站总人数
    private long innerCount = 0;//今日在站人数
    private long outerCount = 0;//今日离站人数
    private List<TAccessControlPersonnelBaseInfo> inner;//内部人员列表
    private List<TAccessControlPersonnelBaseInfo> outer;//外部人员列表
    private List<TAccessControlPersonnelBaseInfo> innerVisitor;//内部访客列表
    private List<TAccessControlPersonnelBaseInfo> build;//施工人员列表
}
