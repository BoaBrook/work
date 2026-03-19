package cn.stylefeng.guns.modular.accesscontrol.hikClient;


import cn.stylefeng.guns.modular.accesscontrol.util.CustomMultipartFile;
import cn.stylefeng.guns.modular.hikvision.NetSDKDemo.HCNetSDK;
import cn.stylefeng.roses.kernel.file.api.FileOperatorApi;
import cn.stylefeng.roses.kernel.file.api.enums.FileLocationEnum;
import cn.stylefeng.roses.kernel.file.api.pojo.request.SysFileInfoRequest;
import cn.stylefeng.roses.kernel.file.api.pojo.response.SysFileInfoResponse;
import cn.stylefeng.roses.kernel.file.modular.entity.SysFileInfo;
import cn.stylefeng.roses.kernel.file.modular.factory.FileInfoFactory;
import cn.stylefeng.roses.kernel.file.modular.service.SysFileStorageService;
import cn.stylefeng.roses.kernel.file.modular.service.impl.SysFileInfoServiceImpl;
import cn.stylefeng.roses.kernel.rule.enums.YesOrNotEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class HikUtil {
    @Resource
    private SysFileInfoServiceImpl sysFileInfoService;
    @Resource
    private SysFileStorageService sysFileStorageService;
    @Resource
    private FileOperatorApi fileOperatorApi;
    private static final SimpleDateFormat ISO_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private static final SimpleDateFormat NORMAL_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 上传图片文件（仅保存文件，不生成URL，适配非HTTP上下文）
     * @param picBuffer 图片字节缓冲区
     * @param picLen 图片长度
     * @param fileName 文件名
     * @return 文件ID
     */
    public Long uploadPicWithoutUrl(ByteBuffer picBuffer, int picLen, String fileName) {
        if (picBuffer == null || picLen <= 0) {
            log.warn("图片数据为空，跳过上传");
            return null;
        }

        byte[] bytes = new byte[picLen];
        picBuffer.rewind();
        picBuffer.get(bytes);

        // 构建MultipartFile
        MultipartFile multipartFile = new CustomMultipartFile(
                bytes,
                fileName,
                "image/jpeg"
        );

        // 构建文件上传请求
        SysFileInfoRequest request = new SysFileInfoRequest();
        request.setFileOriginName(fileName);
        request.setSecretFlag(YesOrNotEnum.N.getCode()); // 不加密

        try {
            // 手动构建SysFileInfo，避免调用uploadFile触发URL生成
            // 步骤1：创建文件信息并保存
            SysFileInfo sysFileInfo = FileInfoFactory.createSysFileInfo(multipartFile, request);
            sysFileInfoService.save(sysFileInfo);

            // 步骤2：存储文件到对应位置（数据库/本地/云）
            byte[] fileBytes = multipartFile.getBytes();
            if (FileLocationEnum.DB.getCode().equals(request.getFileLocation())) {
                sysFileStorageService.saveFile(sysFileInfo.getFileId(), fileBytes);
            } else {
                fileOperatorApi.storageFile(sysFileInfo.getFileBucket(), sysFileInfo.getFileObjectName(), fileBytes);
            }

            log.info("图片[{}]上传成功，文件ID：{}", fileName, sysFileInfo.getFileId());
            return sysFileInfo.getFileId();
        } catch (Exception e) {
            log.error("图片上传失败", e);
            return null;
        }
    }

    /**
     * 重载：通过byte[]上传图片（仅保存文件，不生成URL）
     */
    public Long uploadPicWithoutUrl(byte[] picBytes, String fileName) {
        if (picBytes == null || picBytes.length <= 0) {
            return null;
        }
        MultipartFile multipartFile = new CustomMultipartFile(
                picBytes,
                fileName,
                "image/jpeg"
        );
        SysFileInfoRequest request = new SysFileInfoRequest();
        request.setFileOriginName(fileName);
        request.setSecretFlag(YesOrNotEnum.N.getCode());
        try {
            SysFileInfo sysFileInfo = FileInfoFactory.createSysFileInfo(multipartFile, request);
            sysFileInfoService.save(sysFileInfo);

            byte[] fileBytes = multipartFile.getBytes();
            if (FileLocationEnum.DB.getCode().equals(request.getFileLocation())) {
                sysFileStorageService.saveFile(sysFileInfo.getFileId(), fileBytes);
            } else {
                fileOperatorApi.storageFile(sysFileInfo.getFileBucket(), sysFileInfo.getFileObjectName(), fileBytes);
            }

            return sysFileInfo.getFileId();
        } catch (Exception e) {
            log.error("图片上传失败", e);
            return null;
        }
    }



    /**
     * 重载：通过byte[]上传图片
     */
    public String uploadPic(byte[] picBytes, String fileName) {
        if (picBytes == null || picBytes.length <= 0) {
            return null;
        }
        MultipartFile multipartFile = new CustomMultipartFile(
                picBytes,
                fileName,
                "image/jpeg"
        );
        SysFileInfoRequest request = new SysFileInfoRequest();
        request.setFileOriginName(fileName);
        request.setSecretFlag(YesOrNotEnum.N.getCode());
        try {
            SysFileInfoResponse response = sysFileInfoService.uploadFile(multipartFile, request);
            return response.getFileUrl();
        } catch (Exception e) {
            log.error("图片上传失败", e);
            return null;
        }
    }

    /**
     * 解析ISO格式日期（yyyy-MM-dd'T'HH:mm:ss）
     */
    public Date parseIsoDate(String dateStr) {
        try {
            return ISO_FORMAT.parse(dateStr);
        } catch (ParseException e) {
            log.error("解析ISO日期失败：{}", dateStr, e);
            return null;
        }
    }

    /**
     * 解析常规格式日期（yyyy-MM-dd HH:mm:ss）
     */
    public Date parseNormalDate(String dateStr) {
        try {
            return NORMAL_FORMAT.parse(dateStr);
        } catch (ParseException e) {
            log.error("解析常规日期失败：{}", dateStr, e);
            return null;
        }
    }

    /**
     * 构建海康时间结构体的日期字符串
     */
    public String buildHikDateStr(int year, int month, int day, int hour, int minute, int second) {
        return String.format("%d-%d-%d %d:%d:%d", year, month, day, hour, minute, second);
    }

    /**
     * 填充海康时间结构体（用于查询条件）
     */
    public void fillHikTime(HCNetSDK.NET_DVR_TIME hikTime, LocalDateTime localDateTime) {
        hikTime.dwYear = localDateTime.getYear();
        hikTime.dwMonth = localDateTime.getMonthValue();
        hikTime.dwDay = localDateTime.getDayOfMonth();
        hikTime.dwHour = localDateTime.getHour();
        hikTime.dwMinute = localDateTime.getMinute();
        hikTime.dwSecond = localDateTime.getSecond();
    }
}
