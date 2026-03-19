package cn.stylefeng.guns.modular.modelMap.service.impl;

import cn.stylefeng.guns.core.dto.request.IdsRequest;
import cn.stylefeng.guns.core.utils.PageToPageResultUtils;
import cn.stylefeng.guns.database.entity.TModelMapManagement;
import cn.stylefeng.guns.database.entity.TStationBaseInfo;
import cn.stylefeng.guns.database.service.TModelMapManagementService;
import cn.stylefeng.guns.database.service.TStationBaseInfoService;
import cn.stylefeng.guns.modular.modelMap.request.ModelMapRequest;
import cn.stylefeng.guns.modular.modelMap.service.ModelMapService;
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
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 模型地图管理服务实现
 */
@Service
public class ModelMapServiceImpl implements ModelMapService {

    @Autowired
    private TModelMapManagementService tModelMapManagementService;

    @Autowired
    private TStationBaseInfoService tStationBaseInfoService;

    @Autowired
    private SysFileInfoService sysFileInfoService;

    @Value("${file.model-map.bucket}")
    private String modelMapBucket;

    @Override
    public PageResult<TModelMapManagement> getModelMapList(ModelMapRequest request) {
        LambdaQueryWrapper<TModelMapManagement> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotBlank(request.getBelongStationValveChamberId()), 
                        TModelMapManagement::getBelongStationValveChamberId, 
                        request.getBelongStationValveChamberId())
                .like(StringUtils.isNotBlank(request.getModelName()), 
                      TModelMapManagement::getModelName, 
                      request.getModelName());
        Page<TModelMapManagement> page = new Page<>(request.getPageNo(), request.getPageSize());
        Page<TModelMapManagement> result = tModelMapManagementService.page(page, queryWrapper);
        
        // 查询站场名称并关联
        List<TModelMapManagement> records = result.getRecords();
        if (CollectionUtils.isNotEmpty(records)) {
            // 收集所有站场ID
            List<String> stationIds = records.stream()
                    .map(TModelMapManagement::getBelongStationValveChamberId)
                    .filter(StringUtils::isNotBlank)
                    .distinct()
                    .collect(Collectors.toList());
            
            if (CollectionUtils.isNotEmpty(stationIds)) {
                // 批量查询站场信息
                List<TStationBaseInfo> stationList = tStationBaseInfoService.listByIds(stationIds);
                Map<String, String> stationNameMap = stationList.stream()
                        .collect(Collectors.toMap(TStationBaseInfo::getStationId, 
                                                  TStationBaseInfo::getStationName,
                                                  (v1, v2) -> v1));
                
                // 设置站场名称
                records.forEach(record -> {
                    if (StringUtils.isNotBlank(record.getBelongStationValveChamberId())) {
                        record.setBelongStationValveChamberName(
                                stationNameMap.get(record.getBelongStationValveChamberId()));
                    }
                });
            }
        }
        
        return PageToPageResultUtils.pageToPageResult(result);
    }

    @Override
    public SysFileInfoResponse uploadModelFile(MultipartFile file) {
        SysFileInfoRequest sysFileInfoRequest = new SysFileInfoRequest();
        sysFileInfoRequest.setFileBucket(modelMapBucket);
        sysFileInfoRequest.setFileLocation(4);
        sysFileInfoRequest.setSecretFlag("N");
        return sysFileInfoService.uploadFile(file, sysFileInfoRequest);
    }

    @Override
    public Boolean updateModelMap(TModelMapManagement request) {
        if (StringUtils.isBlank(request.getModelId())) {
            // 新增
            request.setModelId(IdWorker.getIdStr());
            return tModelMapManagementService.save(request);
        } else {
            // 修改
            return tModelMapManagementService.updateById(request);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean batchDeleteModelMap(IdsRequest request) {
        List<String> idList = request.getIdList();
        if (CollectionUtils.isEmpty(idList)) {
            return true;
        }
        // 查询要删除的记录，获取文件ID
        List<TModelMapManagement> modelMapList = tModelMapManagementService.listByIds(idList);
        // 删除模型地图记录
        tModelMapManagementService.removeByIds(idList);
        // 删除关联的文件
        for (TModelMapManagement modelMap : modelMapList) {
            if (modelMap.getModelFileId() != null) {
                sysFileInfoService.removeFile(modelMap.getModelFileId());
            }
        }
        return true;
    }

}
