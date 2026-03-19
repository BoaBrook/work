package cn.stylefeng.guns.modular.industrialTV.controller;

import cn.stylefeng.guns.modular.industrialTV.service.VideoRecordService;
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
@ApiResource(name = "磁盘录像", resBizType = ResBizTypeEnum.BUSINESS)
public class VideoRecordController {

    @Autowired
    private VideoRecordService videoRecordService;

    @GetResource(name = "查询摄像头录像日期列表", path = "/industrialTV/video/record/date")
    public ResponseData<?> getVideoRecordDate(@RequestParam("deviceId") String deviceId) throws Exception {
        return new SuccessResponseData<>(videoRecordService.getCameraDates(deviceId));
    }

//    @GetResource(name = "查询摄像头指定日期下的录像", path = "/industrialTV/video/record/stream")
//    public ResponseData<?> getVideoRecordStream(@RequestParam("deviceId") String deviceId, @RequestParam("date") String date) throws Exception {
//        return new SuccessResponseData<>(videoRecordService.getVideoRecordStream(deviceId, date));
//    }

    @GetResource(name = "查询摄像头指定日期下的录像", path = "/industrialTV/video/record/stream")
    public ResponseData<?> getVideoRecordStream(@RequestParam("deviceId") String deviceId, @RequestParam("startTime") Date startTime, @RequestParam("endTime") Date endTime) {
        return new SuccessResponseData<>(videoRecordService.getVideoRecordStream(deviceId, startTime, endTime));
    }

}
