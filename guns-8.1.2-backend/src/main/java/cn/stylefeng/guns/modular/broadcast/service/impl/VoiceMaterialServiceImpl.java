package cn.stylefeng.guns.modular.broadcast.service.impl;

import cn.stylefeng.guns.core.dto.request.IdsRequest;
import cn.stylefeng.guns.core.utils.PageToPageResultUtils;
import cn.stylefeng.guns.database.entity.TVoiceBroadcastMaterialBaseInfo;
import cn.stylefeng.guns.database.service.TVoiceBroadcastMaterialBaseInfoService;
import cn.stylefeng.guns.modular.broadcast.request.VoiceMaterialRequest;
import cn.stylefeng.guns.modular.broadcast.service.VoiceMaterialService;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;
import cn.stylefeng.roses.kernel.file.api.pojo.request.SysFileInfoRequest;
import cn.stylefeng.roses.kernel.file.api.pojo.response.SysFileInfoResponse;
import cn.stylefeng.roses.kernel.file.modular.service.SysFileInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 语音播报素材服务实现
 */
@Service
public class VoiceMaterialServiceImpl implements VoiceMaterialService {

    @Autowired
    private TVoiceBroadcastMaterialBaseInfoService tVoiceBroadcastMaterialBaseInfoService;

    @Autowired
    private SysFileInfoService sysFileInfoService;

    @Value("${file.broadcast.bucket}")
    private String broadcastBucket;

    @Override
    public PageResult<TVoiceBroadcastMaterialBaseInfo> getVoiceMaterialList(VoiceMaterialRequest request) {
        LambdaQueryWrapper<TVoiceBroadcastMaterialBaseInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(request.getVoiceName()), TVoiceBroadcastMaterialBaseInfo::getVoiceName, request.getVoiceName())
                .eq(StringUtils.isNotBlank(request.getAudioType()), TVoiceBroadcastMaterialBaseInfo::getAudioType, request.getAudioType())
                .eq(StringUtils.isNotBlank(request.getEnableStatus()), TVoiceBroadcastMaterialBaseInfo::getEnableStatus, request.getEnableStatus());
        Page<TVoiceBroadcastMaterialBaseInfo> page = new Page<>(request.getPageNo(), request.getPageSize());
        Page<TVoiceBroadcastMaterialBaseInfo> result = tVoiceBroadcastMaterialBaseInfoService.page(page, queryWrapper);
        return PageToPageResultUtils.pageToPageResult(result);
    }

    @Override
    public SysFileInfoResponse uploadVoiceFile(MultipartFile file) {
        SysFileInfoRequest sysFileInfoRequest = new SysFileInfoRequest();
        sysFileInfoRequest.setFileBucket(broadcastBucket);
        sysFileInfoRequest.setFileLocation(4);
        sysFileInfoRequest.setSecretFlag("N");
        return sysFileInfoService.uploadFile(file, sysFileInfoRequest);
    }

    @Override
    public Boolean updateVoiceMaterial(TVoiceBroadcastMaterialBaseInfo request) {
        if (StringUtils.isBlank(request.getVoiceId())) {
            // 新增
            request.setVoiceId(IdWorker.getIdStr());
            return tVoiceBroadcastMaterialBaseInfoService.save(request);
        } else {
            // 修改
            return tVoiceBroadcastMaterialBaseInfoService.updateById(request);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean batchDeleteVoiceMaterial(IdsRequest request) {
        List<String> idList = request.getIdList();
        if (CollectionUtils.isEmpty(idList)) {
            return true;
        }
        // 查询要删除的记录，获取文件ID
        List<TVoiceBroadcastMaterialBaseInfo> materialList = tVoiceBroadcastMaterialBaseInfoService.listByIds(idList);
        // 删除素材记录
        tVoiceBroadcastMaterialBaseInfoService.removeByIds(idList);
        // 删除关联的文件
        for (TVoiceBroadcastMaterialBaseInfo material : materialList) {
            if (material.getAudioFileId() != null) {
                sysFileInfoService.removeFile(material.getAudioFileId());
            }
        }
        return true;
    }

}
