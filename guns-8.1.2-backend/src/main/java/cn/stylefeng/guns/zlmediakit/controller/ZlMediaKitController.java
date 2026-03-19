package cn.stylefeng.guns.zlmediakit.controller;

import cn.stylefeng.guns.zlmediakit.ZlMediaKitService;
import cn.stylefeng.roses.kernel.rule.enums.ResBizTypeEnum;
import cn.stylefeng.roses.kernel.rule.pojo.response.ResponseData;
import cn.stylefeng.roses.kernel.rule.pojo.response.SuccessResponseData;
import cn.stylefeng.roses.kernel.scanner.api.annotation.ApiResource;
import cn.stylefeng.roses.kernel.scanner.api.annotation.GetResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@ApiResource(name = "流媒体服务", resBizType = ResBizTypeEnum.BUSINESS)
public class ZlMediaKitController {

    @Autowired
    private ZlMediaKitService baseService;

    @GetResource(name = "获取播放地址", path = "/video/zlmediakit/getPlayUrl")
    public ResponseData<?> getPlayUrl(@RequestParam("rtspUrl") String rtspUrl){
        return new SuccessResponseData<>(baseService.getPlayUrl(rtspUrl));
    }

    @GetResource(name = "获取回放地址", path = "/video/zlmediakit/getRecordPlayUrl")
    public ResponseData<?> getRecordPlayUrl(@RequestParam("rtspUrl") String rtspUrl,
                                            @RequestParam("startTime") Date startTime,
                                            @RequestParam("endTime") Date endTime){
        return new SuccessResponseData<>(baseService.getRecordPlayUrl(rtspUrl, startTime, endTime));
    }

    @GetResource(name = "获取截图", path = "/video/zlmediakit/getSnap")
    public ResponseData<?> getSnap(@RequestParam("rtspUrl") String rtspUrl) {
        return new SuccessResponseData<>(baseService.getSnap(rtspUrl));
    }

}
