package com.adplatform.module.ad.service.impl;

import com.adplatform.module.ad.config.OssConfig;
import com.adplatform.module.ad.service.OssService;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * OSS服务实现类
 *
 * @author andrew
 * @date 2023-12-19
 */
@Service
@RequiredArgsConstructor
public class OssServiceImpl implements OssService {

    private final OSS ossClient;
    private final OssConfig ossConfig;

    @Override
    public String upload(MultipartFile file, String dir) throws IOException {
        // 获取原始文件名并提取文件扩展名
        String originalFileName = file.getOriginalFilename();
         String extension = originalFileName != null && originalFileName.lastIndexOf('.') > 0
                        ? originalFileName.substring(originalFileName.lastIndexOf('.'))  // 获取扩展名
                        : ".jpg";  // 默认使用 .jpg 扩展名

        // 生成文件路径
        String fileName = UUID.randomUUID().toString() + extension;
        String filePath = generateFilePath(dir, fileName);

        // 设置文件元数据
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        
        // 上传文件
        ossClient.putObject(ossConfig.getBucketName(), filePath, file.getInputStream(), metadata);
        
        // 返回文件访问URL
        return ossConfig.getDomain() + "/" + filePath;
    }

    @Override
    public void delete(String url) {
        // 从URL中提取文件路径
        String filePath = url.replace(ossConfig.getDomain() + "/", "");
        
        // 删除文件
        ossClient.deleteObject(ossConfig.getBucketName(), filePath);
    }

    /**
     * 上传字符串内容
     * @param content 内容字符串
     * @param fileName 文件名
     * @param dir 目录
     * @param contentType 内容类型
     * @return 文件访问URL
     */
    public String uploadContent(String content, String fileName, String dir, String contentType) {
        try {
            byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
            return uploadBytes(contentBytes, fileName, dir, contentType);
        } catch (Exception e) {
            throw new RuntimeException("上传内容失败：" + e.getMessage(), e);
        }
    }

    /**
     * 上传字节数组
     * @param contentBytes 内容字节数组
     * @param fileName 文件名
     * @param dir 目录
     * @param contentType 内容类型
     * @return 文件访问URL
     */
    public String uploadBytes(byte[] contentBytes, String fileName, String dir, String contentType) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(contentBytes)) {
            return uploadStream(inputStream, fileName, dir, contentType, contentBytes.length);
        } catch (Exception e) {
            throw new RuntimeException("上传字节数组失败：" + e.getMessage(), e);
        }
    }

    /**
     * 上传输入流
     * @param inputStream 输入流
     * @param fileName 文件名
     * @param dir 目录
     * @param contentType 内容类型
     * @param contentLength 内容长度
     * @return 文件访问URL
     */
    public String uploadStream(InputStream inputStream, String fileName, String dir, String contentType, long contentLength) {
        try {
            // 生成文件路径
            String filePath = generateFilePath(dir, fileName);

            // 设置文件元数据
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(contentType);
            metadata.setContentLength(contentLength);
            
            // 上传文件
            ossClient.putObject(ossConfig.getBucketName(), filePath, inputStream, metadata);
            
            // 返回文件访问URL
            return ossConfig.getDomain() + "/" + filePath;
        } catch (Exception e) {
            throw new RuntimeException("上传流失败：" + e.getMessage(), e);
        }
    }

    /**
     * 生成文件路径
     * 格式：dir/yyyy/MM/dd/fileName
     */
    private String generateFilePath(String dir, String fileName) {
        LocalDateTime now = LocalDateTime.now();
        String datePath = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        return String.format("%s/%s/%s", dir, datePath, fileName);
    }
} 