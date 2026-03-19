package cn.stylefeng.guns.core.utils;

import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;
import cn.stylefeng.roses.kernel.rule.exception.base.ServiceException;
import cn.stylefeng.roses.kernel.rule.pojo.request.BaseRequest;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.function.Consumer;

public class CommonOperationUtils {

    public static <T> PageResult<T> pageQuery(BaseRequest request, Class<T> targetClass, IService<T>  service, List<Consumer<QueryWrapper>> consumers) {
        Page<T> page = new Page<>(request.getPageNo(), request.getPageSize());
        QueryWrapper<T> queryWrapper = WrapperGenerator.generateQueryWrapper(request, targetClass);
        if(CollectionUtils.isNotEmpty(consumers)) {
            consumers.forEach(consumer -> consumer.accept(queryWrapper));
        }
        IPage<T> pageResult = service.page(page, queryWrapper);
        return PageToPageResultUtils.pageToPageResult(pageResult);
    }

    public static boolean batchRemove(IService service, List<Long> ids){
        return service.removeBatchByIds(ids);
    }

    public static <T, R> boolean saveOrUpdate(R request, Class<T> targetClass, IService<T>  service){
        try {
            T targetObject = targetClass.newInstance();
            BeanUtils.copyProperties(request, targetObject);
            return service.saveOrUpdate(targetObject);
        } catch (ServiceException e){
            throw e;
        } catch (Exception e) {
            return false;
        }
    }

}
