package cn.stylefeng.guns.modular.firegas.service;

import cn.stylefeng.guns.database.entity.TFireGasImage;
import cn.stylefeng.guns.database.service.TFireGasImageService;
import cn.stylefeng.roses.kernel.file.api.pojo.request.SysFileInfoRequest;
import cn.stylefeng.roses.kernel.file.modular.service.SysFileInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 火气系统图片服务
 * 
 * @author system
 */
@Slf4j
@Service
public class FireGasImageService {

    @Autowired
    private SysFileInfoService sysFileInfoService;

    @Autowired
    private TFireGasImageService fireGasImageService;

    /**
     * 根据fileId下载图片
     * 
     * @param fileId 文件ID
     * @param response HTTP响应
     */
    public void downloadImageByFileId(String fileId, HttpServletResponse response) {
        try {
            if (fileId == null || fileId.trim().isEmpty()) {
                log.warn("fileId为空");
                return;
            }
            
            // 构建下载请求
            SysFileInfoRequest sysFileInfoRequest = new SysFileInfoRequest();
            sysFileInfoRequest.setFileId(Long.valueOf(fileId));
            
            // 下载文件
            sysFileInfoService.download(sysFileInfoRequest, response);
            
            log.info("火气系统图片下载成功，fileId: {}", fileId);
        } catch (Exception e) {
            log.error("火气系统图片下载失败，fileId: {}", fileId, e);
            throw e;
        }
    }

    /**
     * 根据站场ID查询火气系统图片列表
     * 
     * @param belongStationId 所属站场ID
     * @return 图片列表
     */
    public List<TFireGasImage> getImagesByStationId(String belongStationId) {
        try {
            if (belongStationId == null || belongStationId.trim().isEmpty()) {
                return Collections.emptyList();
            }
            
            // 构建查询条件
            LambdaQueryWrapper<TFireGasImage> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TFireGasImage::getBelongStationId, belongStationId);
            
            // 按创建时间倒序
            queryWrapper.orderByAsc(TFireGasImage::getPosition);
            
            // 查询列表
            List<TFireGasImage> images = fireGasImageService.list(queryWrapper);
            
            if (images == null || images.isEmpty()) {
                return Collections.emptyList();
            }
            
            return images;
            
        } catch (Exception e) {
            log.error("根据站场ID查询火气系统图片失败，belongStationId: {}", belongStationId, e);
            throw e;
        }
    }
}
