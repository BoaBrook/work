package cn.stylefeng.guns.modular.fileManage;

import cn.stylefeng.roses.kernel.file.api.pojo.request.SysFileInfoRequest;
import cn.stylefeng.roses.kernel.file.modular.service.SysFileInfoService;
import cn.stylefeng.roses.kernel.file.modular.service.SysFileStorageService;
import cn.stylefeng.roses.kernel.rule.enums.ResBizTypeEnum;
import cn.stylefeng.roses.kernel.rule.pojo.response.ResponseData;
import cn.stylefeng.roses.kernel.rule.pojo.response.SuccessResponseData;
import cn.stylefeng.roses.kernel.scanner.api.annotation.ApiResource;
import cn.stylefeng.roses.kernel.scanner.api.annotation.GetResource;
import cn.stylefeng.roses.kernel.scanner.api.annotation.PostResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

@RestController
@ApiResource(name = "文件管理", resBizType = ResBizTypeEnum.BUSINESS)
public class FileManageController {

    @Autowired
    private SysFileInfoService sysFileInfoService;

    @Autowired
    private SysFileStorageService sysFileStorageService;

    @PostResource(name = "上传文件", path = "/file/upload", requiredLogin = false, requiredPermission = false)
    public ResponseData<?> uploadFile(@RequestParam("file") MultipartFile file) {
        SysFileInfoRequest sysFileInfoRequest  = new SysFileInfoRequest();
        sysFileInfoRequest.setSecretFlag("N");
        sysFileInfoRequest.setFileLocation(4);
        sysFileInfoRequest.setFileBucket("file");
        return new SuccessResponseData<>(sysFileInfoService.uploadFile(file, sysFileInfoRequest));
    }

    @GetResource(name = "下载文件", path = "/file/download")
    public ResponseData<?> downloadFile(@RequestParam("fileId") Long fileId, HttpServletResponse response) {
        SysFileInfoRequest sysFileInfoRequest = new SysFileInfoRequest();
        sysFileInfoRequest.setFileId(fileId);
        sysFileInfoService.download(sysFileInfoRequest, response);
        return new SuccessResponseData<>();
    }

    @GetResource(name = "获取文件下载url", path = "/file/download/url")
    public ResponseData<?> getFileDownloadUrl(@RequestParam("fileId") String fileId) {
        return new SuccessResponseData<>(sysFileStorageService.getFileAuthUrl(fileId));
    }

//    @GetResource(name = "获取未鉴权文件下载url", path = "/file/download/unAuthUrl", requiredLogin = false, requiredPermission = false)
//    public ResponseData<?> getFileUnAuthDownloadUrl(@RequestParam("fileId") String fileId) {
//        return new SuccessResponseData<>(sysFileStorageService.getFileUnAuthUrl(fileId));
//    }

}
