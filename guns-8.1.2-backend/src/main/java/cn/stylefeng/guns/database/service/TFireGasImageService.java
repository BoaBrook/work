package cn.stylefeng.guns.database.service;

import cn.stylefeng.guns.database.entity.TFireGasImage;
import cn.stylefeng.guns.modular.firegas.dto.FireGasImageQueryRequest;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 火气系统图片表 Service接口
 *
 * @author system
 * @date 2026-01-14
 */
public interface TFireGasImageService extends IService<TFireGasImage> {

    /**
     * 分页查询火气系统图片列表
     *
     * @param request 查询条件（包含分页参数）
     * @return 分页结果
     */
    PageResult<TFireGasImage> pageList(FireGasImageQueryRequest request);

    /**
     * 新增火气系统图片
     *
     * @param entity 图片信息（含file、belongStationId、position等）
     * @return 是否成功
     */
    boolean add(TFireGasImage entity);

    /**
     * 编辑火气系统图片
     *
     * @param entity 图片信息（id必填；file可选）
     * @return 是否成功
     */
    boolean update(TFireGasImage entity);

    /**
     * 根据ID删除火气系统图片记录并删除关联的文件
     *
     * @param id 图片记录ID
     * @return 是否成功
     */
    boolean deleteById(String id);

}
