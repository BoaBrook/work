package cn.stylefeng.guns.modular.firegas.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.stylefeng.guns.database.entity.TAlarmResultRecords;
import cn.stylefeng.guns.database.entity.TFireGasImage;
import cn.stylefeng.guns.database.entity.TFireGasSensorBaseInfo;
import cn.stylefeng.guns.modular.firegas.dto.FireGasAlarmQueryRequest;
import cn.stylefeng.guns.modular.firegas.dto.FireGasSensorOnlineStatusResponse;
import cn.stylefeng.guns.modular.firegas.dto.FireGasSensorQueryRequest;
import cn.stylefeng.guns.modular.firegas.service.FireGasAlarmService;
import cn.stylefeng.guns.modular.firegas.service.FireGasImageService;
import cn.stylefeng.guns.modular.firegas.service.FireGasSensorService;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;
import cn.stylefeng.roses.kernel.rule.enums.ResBizTypeEnum;
import cn.stylefeng.roses.kernel.rule.pojo.response.ResponseData;
import cn.stylefeng.roses.kernel.rule.pojo.response.SuccessResponseData;
import cn.stylefeng.roses.kernel.scanner.api.annotation.ApiResource;
import cn.stylefeng.roses.kernel.scanner.api.annotation.GetResource;

/**
 * 火气系统控制器
 * 
 * @author system
 */
@RestController
@ApiResource(name = "火气系统", resBizType = ResBizTypeEnum.BUSINESS)
public class FireGasController {

    @Resource
    private FireGasSensorService fireGasSensorService;

    @Resource
    private FireGasImageService fireGasImageService;

    @Resource
    private FireGasAlarmService fireGasAlarmService;

    /**
     * 根据站场ID查询火气系统图片列表
     * 
     * @param belongStationId 所属站场ID
     * @return 图片列表
     */
    @GetResource(name = "根据站场ID查询火气系统图片列表", path = "/fireGas/image/listByStationId")
    public ResponseData<List<TFireGasImage>> getImagesByStationId(
            @RequestParam("belongStationId") String belongStationId) {
        List<TFireGasImage> imageList = fireGasImageService.getImagesByStationId(belongStationId);
        return new SuccessResponseData<>(imageList);
    }

    /**
     * 根据fileId下载火气系统图片
     * 
     * @param fileId 文件ID
     * @param response HTTP响应
     */
    @GetResource(name = "根据fileId下载火气系统图片", path = "/fireGas/image/download", requiredLogin = false)
    public void downloadImageByFileId(
            @RequestParam("fileId") String fileId, HttpServletResponse response) {
        fireGasImageService.downloadImageByFileId(fileId, response);
    }

    /**
     * 分页查询传感器设备列表（带关联信息）
     * 
     * @param request 查询请求参数
     * @return 分页结果
     */
    @GetResource(name = "分页查询传感器设备列表", path = "/fireGas/sensor/page")
    public ResponseData<PageResult<TFireGasSensorBaseInfo>> getSensorPage(FireGasSensorQueryRequest request) {
        return new SuccessResponseData<>(fireGasSensorService.getSensorPage(request));
    }

    /**
     * 统计所有传感器的在线状态
     * 
     * @param belongStationId 所属站场ID
     * @return 在线状态统计结果
     */
    @GetResource(name = "统计传感器在线状态", path = "/fireGas/sensor/onlineStatus")
    public ResponseData<FireGasSensorOnlineStatusResponse> getSensorOnlineStatus(
            @RequestParam(value = "belongStationId", required = false) String belongStationId) {
        return new SuccessResponseData<>(fireGasSensorService.getSensorOnlineStatus(belongStationId));
    }

    /**
     * 分页查询火气系统的报警记录
     * 
     * @param request 查询请求参数
     * @return 分页结果
     */
    @GetResource(name = "分页查询火气系统报警", path = "/fireGas/alarm/page")
    public ResponseData<PageResult<TAlarmResultRecords>> getAlarmRecordsPage(FireGasAlarmQueryRequest request) {
        return new SuccessResponseData<>(fireGasAlarmService.getAlarmRecordsPage(request));
    }
}
