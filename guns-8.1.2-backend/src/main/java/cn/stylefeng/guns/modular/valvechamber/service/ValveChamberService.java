package cn.stylefeng.guns.modular.valvechamber.service;

import cn.stylefeng.guns.database.entity.TValveChamberBaseInfo;
import cn.stylefeng.guns.modular.valvechamber.dto.ValveChamberQueryRequest;
import cn.stylefeng.guns.modular.valvechamber.entity.ValveChamberListVO;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 阀室管理 Service
 *
 * @author system
 */
public interface ValveChamberService extends IService<TValveChamberBaseInfo> {

    /**
     * 分页查询阀室列表
     *
     * @param request 查询条件（含分页、阀室名称、所属站场、所属站场区域）
     * @return 分页结果
     */
    PageResult<ValveChamberListVO> pageList(ValveChamberQueryRequest request);

    /**
     * 新增阀室
     *
     * @param entity 阀室信息
     * @return 是否成功
     */
    boolean add(TValveChamberBaseInfo entity);
}
