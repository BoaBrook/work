package cn.stylefeng.guns.modular.modelMap.service;

import cn.stylefeng.guns.core.dto.request.IdsRequest;
import cn.stylefeng.guns.database.entity.TModelMapManagement;
import cn.stylefeng.guns.modular.modelMap.request.ModelMapRequest;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;
import cn.stylefeng.roses.kernel.file.api.pojo.response.SysFileInfoResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * 模型地图管理服务接口
 */
public interface ModelMapService {

    /**
     * 分页查询模型地图
     */
    PageResult<TModelMapManagement> getModelMapList(ModelMapRequest request);

    /**
     * 上传模型文件
     */
    SysFileInfoResponse uploadModelFile(MultipartFile file);

    /**
     * 更新模型地图（新增/修改）
     */
    Boolean updateModelMap(TModelMapManagement request);

    /**
     * 批量删除模型地图
     */
    Boolean batchDeleteModelMap(IdsRequest request);

}
