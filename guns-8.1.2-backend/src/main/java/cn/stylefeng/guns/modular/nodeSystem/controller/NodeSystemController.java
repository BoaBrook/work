package cn.stylefeng.guns.modular.nodeSystem.controller;

import cn.stylefeng.guns.modular.nodeSystem.dto.AlarmRawDTO;
import cn.stylefeng.guns.modular.nodeSystem.dto.DeviceInventoryDTO;
import cn.stylefeng.guns.modular.nodeSystem.service.NodeSystemService;
import cn.stylefeng.roses.kernel.rule.enums.ResBizTypeEnum;
import cn.stylefeng.roses.kernel.rule.pojo.response.ResponseData;
import cn.stylefeng.roses.kernel.rule.pojo.response.SuccessResponseData;
import cn.stylefeng.roses.kernel.scanner.api.annotation.ApiResource;
import cn.stylefeng.roses.kernel.scanner.api.annotation.PostResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@ApiResource(name = "节点系统服务", path = "/nodeSystem", resBizType = ResBizTypeEnum.BUSINESS)
public class NodeSystemController {

    @Autowired
    private NodeSystemService nodeSystemService;

    @PostResource(name = "上传设备清单", path = "/send/device/inventory")
    public ResponseData<?> sendDeviceInventory(@RequestBody DeviceInventoryDTO deviceList){
        return new SuccessResponseData<>(nodeSystemService.sendDeviceInventory(Collections.singletonList(deviceList)));
    }

    @PostResource(name = "节点上报告警数据", path = "/send/alarm/raw")
    public ResponseData<?> sendAlarmRaw(@RequestBody AlarmRawDTO alarmRaw){
        return new SuccessResponseData<>(nodeSystemService.sendAlarmRaw(alarmRaw));
    }

}
