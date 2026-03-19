package cn.stylefeng.guns.modular.perimeterintrusion.api;

import cn.stylefeng.guns.modular.perimeterintrusion.dto.PerimeterIntrusionRequest;
import cn.stylefeng.guns.modular.perimeterintrusion.service.PerimeterIntrusionService;
import cn.stylefeng.roses.kernel.rule.enums.ResBizTypeEnum;
import cn.stylefeng.roses.kernel.rule.pojo.response.ResponseData;
import cn.stylefeng.roses.kernel.rule.pojo.response.SuccessResponseData;
import cn.stylefeng.roses.kernel.scanner.api.annotation.ApiResource;
import cn.stylefeng.roses.kernel.scanner.api.annotation.PostResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@ApiResource(name = "周界入侵API", resBizType = ResBizTypeEnum.BUSINESS)
public class PerimeterIntrusionApiController {

    @Autowired
    private PerimeterIntrusionService perimeterIntrusionService;

    /**
     * 报警上传接口
     *
     * @param request 报警数据
     * @return 业务结果
     */
    @PostResource(name="周界报警上传", path = "/api/perimeterApi/alarm", requiredLogin = false)
    public ResponseData<?> alarm(@RequestBody PerimeterIntrusionRequest request) {
        return new SuccessResponseData<>(perimeterIntrusionService.alarm(request));
    }
}

