package cn.stylefeng.guns.modular.firegas.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.stylefeng.guns.database.entity.TFireGasImage;
import cn.stylefeng.guns.database.service.TFireGasImageService;
import cn.stylefeng.guns.modular.firegas.dto.FireGasImageQueryRequest;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;
import cn.stylefeng.roses.kernel.rule.enums.ResBizTypeEnum;
import cn.stylefeng.roses.kernel.rule.pojo.response.ResponseData;
import cn.stylefeng.roses.kernel.rule.pojo.response.SuccessResponseData;
import cn.stylefeng.roses.kernel.scanner.api.annotation.ApiResource;
import cn.stylefeng.roses.kernel.scanner.api.annotation.GetResource;
import cn.stylefeng.roses.kernel.scanner.api.annotation.PostResource;

/**
 * 火气系统图片管理控制器
 *
 * @author system
 * @date 2026-01-14
 */
@RestController
@ApiResource(name = "火气系统图片管理", resBizType = ResBizTypeEnum.BUSINESS)
public class TFireGasImageController {

    @Resource
    private TFireGasImageService tFireGasImageService;

    /**
     * 分页查询火气系统图片
     *
     * @param request 查询请求参数
     * @return 分页结果
     */
    @GetResource(name = "分页查询火气系统图片", path = "/fireGas/image/page")
    public ResponseData<PageResult<TFireGasImage>> page(FireGasImageQueryRequest request) {
        return new SuccessResponseData<>(tFireGasImageService.pageList(request));
    }

    /**
     * 新增火气系统图片
     *
     * @param entity 请求参数（file、belongStationId、position 必填；modelCode、modelName 选填）
     * @return 是否新增成功
     */
    @PostResource(name = "新增火气系统图片", path = "/fireGas/image/add", requiredLogin = false)
    public ResponseData<Boolean> add(@ModelAttribute TFireGasImage entity) {
        return new SuccessResponseData<>(tFireGasImageService.add(entity));
    }

    /**
     * 编辑火气系统图片
     *
     * @param entity 请求参数（id 必填；file 可选，传入则替换原图；其他字段可选）
     * @return 是否更新成功
     */
    @PostResource(name = "编辑火气系统图片", path = "/fireGas/image/update", requiredLogin = false)
    public ResponseData<Boolean> update(@ModelAttribute TFireGasImage entity) {
        return new SuccessResponseData<>(tFireGasImageService.update(entity));
    }

    /**
     * 根据ID删除火气系统图片记录并删除关联的文件
     *
     * @param id 图片记录ID
     * @return 是否删除成功
     */
    @PostResource(name = "根据ID删除火气系统图片", path = "/fireGas/image/delete")
    public ResponseData<Boolean> delete(@RequestParam("id") String id) {
        return new SuccessResponseData<>(tFireGasImageService.deleteById(id));
    }
}
