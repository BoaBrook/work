package cn.stylefeng.guns.modular.tagmanagement.mapper;

import cn.stylefeng.guns.modular.tagmanagement.request.TagManagementListRequest;
import cn.stylefeng.guns.modular.tagmanagement.response.TagManagementListResponse;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

public interface TagManagementQueryMapper {

    IPage<TagManagementListResponse> selectTagPage(Page<TagManagementListResponse> page,
                                                   @Param("req") TagManagementListRequest request);
}
