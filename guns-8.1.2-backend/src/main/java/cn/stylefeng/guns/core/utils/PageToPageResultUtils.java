package cn.stylefeng.guns.core.utils;

import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;
import com.baomidou.mybatisplus.core.metadata.IPage;

public class PageToPageResultUtils {

    public static <T> PageResult<T> pageToPageResult(IPage<T> page){
        PageResult<T> pageResult = new PageResult<>();
        pageResult.setPageNo((int) page.getCurrent());
        pageResult.setPageSize((int) page.getSize());
        pageResult.setTotalPage((int) page.getPages());
        pageResult.setTotalRows((int) page.getTotal());
        pageResult.setRows(page.getRecords());
        return pageResult;
    }

}
