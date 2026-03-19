package cn.stylefeng.guns.modular.tagmanagement.service;

import cn.stylefeng.guns.database.entity.TTagManagement;
import cn.stylefeng.guns.modular.tagmanagement.request.TagManagementListRequest;
import cn.stylefeng.guns.modular.tagmanagement.response.TagDeviceOptionResponse;
import cn.stylefeng.guns.modular.tagmanagement.response.TagManagementListResponse;
import cn.stylefeng.guns.modular.tagmanagement.response.TagModelOptionResponse;
import cn.stylefeng.guns.modular.tagmanagement.response.TagSubsystemOptionResponse;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;

import java.util.List;

public interface TagManagementQueryService {

    /**
     * 标签管理列表查询
     */
    PageResult<TagManagementListResponse> getTagList(TagManagementListRequest request);

    /**
     * 标签管理-子系统类型下拉
     */
    List<TagSubsystemOptionResponse> subsystemOptions();

    /**
     * 标签管理-设备下拉（按站场和子系统类型筛选）
     */
    List<TagDeviceOptionResponse> deviceOptions(String belongStationId, String subsystemType);

    /**
     * 标签管理-模型下拉（按站场筛选）
     */
    List<TagModelOptionResponse> modelOptions(String belongStationId);

    /**
     * 标签管理-新增
     */
    boolean addTag(TTagManagement tagManagement);

    /**
     * 标签管理-编辑
     */
    boolean updateTag(TTagManagement tagManagement);

    /**
     * 标签管理-删除
     */
    boolean deleteTag(String tagId);
}
