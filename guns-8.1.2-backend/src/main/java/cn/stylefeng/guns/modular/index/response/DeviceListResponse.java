package cn.stylefeng.guns.modular.index.response;

import cn.stylefeng.guns.database.entity.TTagManagement;
import lombok.Data;

@Data
public class DeviceListResponse {

    private Object device;
    private TTagManagement tag;

}
