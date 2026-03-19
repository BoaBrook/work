package cn.stylefeng.guns.modular.nodeSystem.controller;


import cn.stylefeng.guns.modular.nodeSystem.service.NodeSystemKafkaLogService;
import cn.stylefeng.roses.kernel.rule.enums.ResBizTypeEnum;
import cn.stylefeng.roses.kernel.scanner.api.annotation.ApiResource;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@ApiResource(name = "节点系统kafka日志", path = "/nodeSystemKafkaLog", resBizType = ResBizTypeEnum.BUSINESS)
public class NodeSystemKafkaLogController {

    @Resource
    private NodeSystemKafkaLogService nodeSystemKafkaLogService;

}
