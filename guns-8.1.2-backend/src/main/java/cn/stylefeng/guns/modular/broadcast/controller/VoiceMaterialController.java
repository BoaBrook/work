package cn.stylefeng.guns.modular.broadcast.controller;

import cn.stylefeng.guns.core.dto.request.IdsRequest;
import cn.stylefeng.guns.database.entity.TVoiceBroadcastMaterialBaseInfo;
import cn.stylefeng.guns.modular.broadcast.request.VoiceMaterialRequest;
import cn.stylefeng.guns.modular.broadcast.service.VoiceMaterialService;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;
import cn.stylefeng.roses.kernel.file.api.pojo.response.SysFileInfoResponse;
import cn.stylefeng.roses.kernel.rule.enums.ResBizTypeEnum;
import cn.stylefeng.roses.kernel.rule.pojo.response.ResponseData;
import cn.stylefeng.roses.kernel.rule.pojo.response.SuccessResponseData;
import cn.stylefeng.roses.kernel.scanner.api.annotation.ApiResource;
import cn.stylefeng.roses.kernel.scanner.api.annotation.GetResource;
import cn.stylefeng.roses.kernel.scanner.api.annotation.PostResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 语音播报素材管理控制器
 */
@RestController
@ApiResource(name = "语音播报素材管理", resBizType = ResBizTypeEnum.BUSINESS)
public class VoiceMaterialController {

    @Autowired
    private VoiceMaterialService voiceMaterialService;

    /**
     * 分页查询语音播报素材
     */
    @GetResource(name = "语音播报素材查询", path = "/voiceMaterial/list")
    public ResponseData<PageResult<TVoiceBroadcastMaterialBaseInfo>> getVoiceMaterialList(VoiceMaterialRequest request) {
        return new SuccessResponseData<>(voiceMaterialService.getVoiceMaterialList(request));
    }

    /**
     * 上传语音文件
     */
    @PostResource(name = "上传语音文件", path = "/voiceMaterial/upload")
    public ResponseData<SysFileInfoResponse> uploadVoiceFile(@RequestParam("file") MultipartFile file) {
        return new SuccessResponseData<>(voiceMaterialService.uploadVoiceFile(file));
    }

    /**
     * 更新语音播报素材（新增/修改）
     */
    @PostResource(name = "更新语音播报素材", path = "/voiceMaterial/update")
    public ResponseData<?> updateVoiceMaterial(@RequestBody TVoiceBroadcastMaterialBaseInfo request) {
        return new SuccessResponseData<>(voiceMaterialService.updateVoiceMaterial(request));
    }

    /**
     * 批量删除语音播报素材
     */
    @PostResource(name = "批量删除语音播报素材", path = "/voiceMaterial/batchDelete")
    public ResponseData<?> batchDeleteVoiceMaterial(@RequestBody IdsRequest request) {
        return new SuccessResponseData<>(voiceMaterialService.batchDeleteVoiceMaterial(request));
    }

}
