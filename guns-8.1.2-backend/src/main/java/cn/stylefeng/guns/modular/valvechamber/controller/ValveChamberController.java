package cn.stylefeng.guns.modular.valvechamber.controller;

import cn.stylefeng.guns.database.entity.TValveChamberBaseInfo;
import cn.stylefeng.guns.modular.valvechamber.dto.ValveChamberQueryRequest;
import cn.stylefeng.guns.modular.valvechamber.entity.ValveChamberListVO;
import cn.stylefeng.guns.modular.valvechamber.service.ValveChamberService;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;
import cn.stylefeng.roses.kernel.rule.enums.ResBizTypeEnum;
import cn.stylefeng.roses.kernel.rule.pojo.response.ResponseData;
import cn.stylefeng.roses.kernel.rule.pojo.response.SuccessResponseData;
import cn.stylefeng.roses.kernel.scanner.api.annotation.ApiResource;
import cn.stylefeng.roses.kernel.scanner.api.annotation.GetResource;
import cn.stylefeng.roses.kernel.scanner.api.annotation.PostResource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 阀室管理控制器
 *
 * @author system
 */
@RestController
@ApiResource(name = "阀室管理", resBizType = ResBizTypeEnum.BUSINESS)
public class ValveChamberController {

    @Resource
    private ValveChamberService valveChamberService;

    /**
     * 获取阀室列表（
     */
    @GetResource(name = "获取阀室列表", path = "/valveChamber/list")
    public ResponseData<PageResult<ValveChamberListVO>> list(ValveChamberQueryRequest request) {
        return new SuccessResponseData<>(valveChamberService.pageList(request));
    }

    /**
     * 新增阀室
     */
    @PostResource(name = "新增阀室", path = "/valveChamber/add")
    public ResponseData<Boolean> add(@RequestBody TValveChamberBaseInfo entity) {
        return new SuccessResponseData<>(valveChamberService.add(entity));
    }

    /**
     * 编辑阀室
     */
    @PostResource(name = "编辑阀室", path = "/valveChamber/update")
    public ResponseData<Boolean> update(@RequestBody TValveChamberBaseInfo entity) {
        return new SuccessResponseData<>(valveChamberService.updateById(entity));
    }

    /**
     * 批量删除阀室
     */
    @PostResource(name = "批量删除阀室", path = "/valveChamber/delete")
    public ResponseData<Boolean> batchDelete(@RequestBody List<String> valveChamberIds) {
        if (valveChamberIds == null || valveChamberIds.isEmpty()) {
            return new SuccessResponseData<>(true);
        }
        return new SuccessResponseData<>(valveChamberService.removeByIds(valveChamberIds));
    }
}
