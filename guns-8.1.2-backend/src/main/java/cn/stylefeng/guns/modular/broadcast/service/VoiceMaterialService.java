package cn.stylefeng.guns.modular.broadcast.service;

import cn.stylefeng.guns.core.dto.request.IdsRequest;
import cn.stylefeng.guns.database.entity.TVoiceBroadcastMaterialBaseInfo;
import cn.stylefeng.guns.modular.broadcast.request.VoiceMaterialRequest;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;
import cn.stylefeng.roses.kernel.file.api.pojo.response.SysFileInfoResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * 语音播报素材服务接口
 */
public interface VoiceMaterialService {

    /**
     * 分页查询语音播报素材
     */
    PageResult<TVoiceBroadcastMaterialBaseInfo> getVoiceMaterialList(VoiceMaterialRequest request);

    /**
     * 上传语音文件
     */
    SysFileInfoResponse uploadVoiceFile(MultipartFile file);

    /**
     * 更新语音播报素材（新增/修改）
     */
    Boolean updateVoiceMaterial(TVoiceBroadcastMaterialBaseInfo request);

    /**
     * 批量删除语音播报素材
     */
    Boolean batchDeleteVoiceMaterial(IdsRequest request);

}
