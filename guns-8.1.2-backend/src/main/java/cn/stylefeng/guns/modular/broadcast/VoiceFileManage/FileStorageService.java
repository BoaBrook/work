package cn.stylefeng.guns.modular.broadcast.VoiceFileManage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {
    
    @Value("${file.broadcast.upload.relative-path}")
    private String relativeUploadPath;
    
    /**
     * 获取应用程序根路径
     */
    private String getAppPath() {
        try {
            // 获取当前jar包或项目的路径
            String path = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
            // 解码可能存在的URL编码字符
            path = java.net.URLDecoder.decode(path, "UTF-8");
            
            // 如果是jar包运行，获取jar所在目录
            if (path.endsWith(".jar")) {
                path = new File(path).getParent();
            } else {
                // 如果是开发环境，获取classes目录的父目录
                Path classesPath = Paths.get(path).getParent();
                if (classesPath != null) {
                    path = classesPath.toString();
                }
            }
            
            return path;
        } catch (UnsupportedEncodingException e) {
            // 如果解码失败，回退到当前工作目录
            return System.getProperty("user.dir");
        }
    }
    
    /**
     * 获取完整的上传路径（应用程序路径 + 相对路径）
     */
    public String getUploadPath() {
        String appPath = getAppPath();
        // 确保路径以文件分隔符结尾
        if (!appPath.endsWith(File.separator)) {
            appPath += File.separator;
        }
        return appPath + relativeUploadPath;
    }
    
    /**
     * 保存上传的文件
     */
    public String saveFile(MultipartFile file) throws IOException {
        // 获取完整的上传路径
        String fullPath = getUploadPath();
        
        // 确保上传目录存在
        File directory = new File(fullPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        
        // 生成唯一文件名
        String originalFileName = file.getOriginalFilename();
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        
        String uniqueFileName = System.currentTimeMillis() + "_" + 
                               UUID.randomUUID().toString().replace("-", "") + extension;
        
        // 保存文件
        File destFile = new File(fullPath + uniqueFileName);
        file.transferTo(destFile);
        
        return uniqueFileName;
    }
    
    /**
     * 根据路径获取文件字节数组
     */
    public byte[] getFileAsBytes(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("文件不存在: " + filePath);
        }
        return Files.readAllBytes(file.toPath());
    }
    
    /**
     * 根据文件名获取文件名（不含路径）
     */
    public String getFileName(String fileName) {
        String fullPath = getUploadPath();
        File file = new File(fullPath + fileName);
        if (!file.exists()) {
            return null;
        }
        return file.getName();
    }
    
    /**
     * 获取文件的MIME类型
     */
    public String getContentType(String fileName) throws IOException {
        String fullPath = getUploadPath();
        Path filePath = Paths.get(fullPath + fileName);
        return Files.probeContentType(filePath);
    }
    
    /**
     * 删除文件
     */
    public boolean deleteFile(String fileName) {
        String fullPath = getUploadPath();
        File file = new File(fullPath + fileName);
        return file.delete();
    }
    
    /**
     * 获取相对上传路径
     */
    public String getRelativeUploadPath() {
        return relativeUploadPath;
    }
}