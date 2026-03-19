package cn.stylefeng.guns.modular.datimsien.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import cn.stylefeng.guns.modular.datimsien.dto.DatimsienRequestRt;
import cn.stylefeng.guns.modular.datimsien.dto.DatimsienResponseRt;
import cn.stylefeng.guns.modular.datimsien.service.DatimsienRtService;
import cn.stylefeng.roses.kernel.rule.enums.ResBizTypeEnum;
import cn.stylefeng.roses.kernel.rule.pojo.response.ResponseData;
import cn.stylefeng.roses.kernel.rule.pojo.response.SuccessResponseData;
import cn.stylefeng.roses.kernel.scanner.api.annotation.ApiResource;
import cn.stylefeng.roses.kernel.scanner.api.annotation.PostResource;

/**
 * Datimsien控制器
 *
 * @author system
 */
@RestController
@ApiResource(name = "Datimsien", resBizType = ResBizTypeEnum.BUSINESS)
public class DatimsienController {

    @Autowired
    private DatimsienRtService datimsienRtService;

    /**
     * 从实时库获取rt数据（远程调用）
     *
     * @param request 请求参数
     * @return 实时数据列表
     */
    @PostResource(name = "获取实时数据（远程）", path = "/api/datimsien/rt", requiredLogin = false)
    public ResponseData<List<DatimsienResponseRt>> rt(@RequestBody DatimsienRequestRt request) {
        List<DatimsienResponseRt> result = datimsienRtService.getRtDataRemote(request);
        return new SuccessResponseData<>(result);
    }
}
