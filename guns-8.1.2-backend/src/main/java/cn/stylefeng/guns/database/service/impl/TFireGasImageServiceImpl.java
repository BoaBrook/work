package cn.stylefeng.guns.database.service.impl;

import cn.stylefeng.guns.core.consts.ProjectConstants;
import cn.stylefeng.guns.database.entity.TFireGasImage;
import cn.stylefeng.guns.database.entity.TStationBaseInfo;
import cn.stylefeng.guns.database.mapper.TFireGasImageMapper;
import cn.stylefeng.guns.database.service.TFireGasImageService;
import cn.stylefeng.guns.database.service.TStationBaseInfoService;
import cn.stylefeng.guns.modular.firegas.dto.FireGasImageQueryRequest;
import cn.stylefeng.roses.kernel.db.api.factory.PageFactory;
import cn.stylefeng.roses.kernel.db.api.factory.PageResultFactory;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;
import cn.stylefeng.roses.kernel.file.api.pojo.request.SysFileInfoRequest;
import cn.stylefeng.roses.kernel.file.api.pojo.response.SysFileInfoResponse;
import cn.stylefeng.roses.kernel.file.modular.service.SysFileInfoService;
import cn.stylefeng.roses.kernel.rule.exception.base.ServiceException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 火气系统图片表 Service实现
 *
 * @author system
 * @date 2026-01-14
 */
@Slf4j
@Service
public class TFireGasImageServiceImpl extends ServiceImpl<TFireGasImageMapper, TFireGasImage> implements TFireGasImageService {

    @Autowired
    private SysFileInfoService sysFileInfoService;

    @Autowired
    private TStationBaseInfoService stationBaseInfoService;

    @Override
    public PageResult<TFireGasImage> pageList(FireGasImageQueryRequest request) {
        LambdaQueryWrapper<TFireGasImage> queryWrapper = new LambdaQueryWrapper<>();

        if (request.getBelongStationId() != null && !request.getBelongStationId().trim().isEmpty()) {
            queryWrapper.eq(TFireGasImage::getBelongStationId, request.getBelongStationId());
        }
        if (request.getPosition() != null && !request.getPosition().trim().isEmpty()) {
            queryWrapper.eq(TFireGasImage::getPosition, request.getPosition());
        }
        if (request.getModelCode() != null && !request.getModelCode().trim().isEmpty()) {
            queryWrapper.like(TFireGasImage::getModelCode, request.getModelCode());
        }
        if (request.getModelName() != null && !request.getModelName().trim().isEmpty()) {
            queryWrapper.like(TFireGasImage::getModelName, request.getModelName());
        }

        queryWrapper.orderByAsc(TFireGasImage::getModelCode);

        Page<TFireGasImage> pageable = PageFactory.defaultPage(request);
        Page<TFireGasImage> imagePage = this.page(pageable, queryWrapper);

        if (imagePage == null || imagePage.getRecords().isEmpty()) {
            return PageResultFactory.createPageResult(new Page<>(request.getPageNo(), request.getPageSize(), 0));
        }

        List<TFireGasImage> images = imagePage.getRecords();
        Set<String> stationIds = images.stream()
                .map(TFireGasImage::getBelongStationId)
                .filter(id -> id != null && !id.trim().isEmpty())
                .collect(Collectors.toSet());

        final Map<String, TStationBaseInfo> stationMap;
        if (!stationIds.isEmpty()) {
            List<TStationBaseInfo> stationList = stationBaseInfoService.list(
                    new LambdaQueryWrapper<TStationBaseInfo>().in(TStationBaseInfo::getStationId, stationIds));
            stationMap = stationList.stream()
                    .collect(Collectors.toMap(TStationBaseInfo::getStationId, s -> s, (a, b) -> a));
        } else {
            stationMap = Collections.emptyMap();
        }

        images.forEach(image -> {
            TStationBaseInfo station = stationMap.get(image.getBelongStationId());
            if (station != null) {
                image.setBelongStationName(station.getStationName());
            }
        });

        return PageResultFactory.createPageResult(imagePage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean add(TFireGasImage entity) {
        String belongStationId = entity.getBelongStationId();
        String position = entity.getPosition();

        LambdaQueryWrapper<TFireGasImage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TFireGasImage::getBelongStationId, belongStationId)
                .eq(TFireGasImage::getPosition, position);
        TFireGasImage existingImage = this.getOne(queryWrapper);
        if (existingImage != null) {
            throw new ServiceException(ProjectConstants.PROJECT_MODULE_NAME, "0",
                    "该站场和位置已存在图片，站场ID：" + belongStationId + "，位置：" + position);
        }

        SysFileInfoRequest sysFileInfoRequest = new SysFileInfoRequest();
        sysFileInfoRequest.setSecretFlag("N");
        sysFileInfoRequest.setFileLocation(4);
        sysFileInfoRequest.setFileBucket("firegas");

        SysFileInfoResponse fileInfoResponse = sysFileInfoService.uploadFile(entity.getFile(), sysFileInfoRequest);
        if (fileInfoResponse == null || fileInfoResponse.getFileId() == null) {
            return false;
        }

        String newFileId = fileInfoResponse.getFileId().toString();
        String fileUrl = fileInfoResponse.getFileUrl();

        TFireGasImage fireGasImage = new TFireGasImage();
        fireGasImage.setFileId(newFileId);
        fireGasImage.setBelongStationId(belongStationId);
        fireGasImage.setPosition(position);
        fireGasImage.setModelCode(entity.getModelCode());
        fireGasImage.setModelName(entity.getModelName());
        fireGasImage.setModelUrl(fileUrl);

        this.save(fireGasImage);
        log.info("火气系统图片新增成功，fileId: {}, belongStationId: {}, position: {}", newFileId, belongStationId, position);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(TFireGasImage entity) {
        String id = entity.getId();
        TFireGasImage existingImage = this.getById(id);
        if (existingImage == null) {
            log.warn("未找到要更新的图片记录，id: {}", id);
            return false;
        }

        if (entity.getFile() != null && !entity.getFile().isEmpty()) {
            SysFileInfoRequest sysFileInfoRequest = new SysFileInfoRequest();
            sysFileInfoRequest.setSecretFlag("N");
            sysFileInfoRequest.setFileLocation(4);
            sysFileInfoRequest.setFileBucket("firegas");

            SysFileInfoResponse fileInfoResponse = sysFileInfoService.uploadFile(entity.getFile(), sysFileInfoRequest);
            if (fileInfoResponse != null && fileInfoResponse.getFileId() != null) {
                String oldFileId = existingImage.getFileId();
                String newFileId = fileInfoResponse.getFileId().toString();
                String fileUrl = fileInfoResponse.getFileUrl();

                existingImage.setFileId(newFileId);
                existingImage.setModelUrl(fileUrl);

                if (oldFileId != null && !oldFileId.trim().isEmpty()) {
                    try {
                        sysFileInfoService.removeFile(Long.valueOf(oldFileId));
                    } catch (Exception e) {
                        log.warn("删除旧文件失败，fileId: {}", oldFileId, e);
                    }
                }
            }
        }

        if (entity.getBelongStationId() != null) {
            existingImage.setBelongStationId(entity.getBelongStationId());
        }
        if (entity.getPosition() != null) {
            existingImage.setPosition(entity.getPosition());
        }
        if (entity.getModelCode() != null) {
            existingImage.setModelCode(entity.getModelCode());
        }
        if (entity.getModelName() != null) {
            existingImage.setModelName(entity.getModelName());
        }

        String targetStationId = existingImage.getBelongStationId();
        String targetPosition = existingImage.getPosition();
        LambdaQueryWrapper<TFireGasImage> checkWrapper = new LambdaQueryWrapper<>();
        checkWrapper.eq(TFireGasImage::getBelongStationId, targetStationId)
                .eq(TFireGasImage::getPosition, targetPosition)
                .ne(TFireGasImage::getId, id);
        TFireGasImage duplicateImage = this.getOne(checkWrapper);
        if (duplicateImage != null) {
            throw new ServiceException(ProjectConstants.PROJECT_MODULE_NAME, "0",
                    "该站场和位置已存在其他图片，站场ID：" + targetStationId + "，位置：" + targetPosition);
        }

        this.updateById(existingImage);
        log.info("火气系统图片更新成功，id: {}", id);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteById(String id) {
        if (id == null || id.trim().isEmpty()) {
            log.warn("删除图片记录失败，id为空");
            return false;
        }

        TFireGasImage fireGasImage = this.getById(id);
        if (fireGasImage == null) {
            log.warn("未找到要删除的图片记录，id: {}", id);
            return false;
        }

        boolean deleted = this.removeById(id);

        if (deleted && fireGasImage.getFileId() != null && !fireGasImage.getFileId().trim().isEmpty()) {
            try {
                sysFileInfoService.removeFile(Long.valueOf(fireGasImage.getFileId()));
                log.info("火气系统图片删除成功，id: {}, fileId: {}", id, fireGasImage.getFileId());
            } catch (Exception e) {
                log.error("删除文件失败，fileId: {}", fireGasImage.getFileId(), e);
            }
        }

        return deleted;
    }

}
