package cn.stylefeng.guns.modular.accesscontrol.request;

import cn.stylefeng.guns.database.entity.TAccessControlEntryExitRecords;
import lombok.Data;

@Data
public class PersonalManageRequest extends TAccessControlEntryExitRecords {
    private String belongStationId;
}
