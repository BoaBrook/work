package cn.stylefeng.guns.core.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class UrlToMultipartFileUtils {

    public static MultipartFile convertUrlToMultipartFile(String fileUrl, String filename) throws IOException {
        URL url = new URL(fileUrl);

        // 创建临时文件
        Path tempFile = Files.createTempFile("download_", "_" + filename);

        try (InputStream in = url.openStream()) {
            Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
        }

        File file = tempFile.toFile();

        return new MultipartFile() {
            @Override
            public String getName() {
                return filename;
            }

            @Override
            public String getOriginalFilename() {
                return filename;
            }

            @Override
            public String getContentType() {
                try {
                    return Files.probeContentType(tempFile);
                } catch (IOException e) {
                    return "application/octet-stream";
                }
            }

            @Override
            public boolean isEmpty() {
                return file.length() == 0;
            }

            @Override
            public long getSize() {
                return file.length();
            }

            @Override
            public byte[] getBytes() throws IOException {
                return Files.readAllBytes(tempFile);
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return new FileInputStream(file);
            }

            @Override
            public void transferTo(File dest) throws IOException, IllegalStateException {
                Files.copy(tempFile, dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        };
    }

}
