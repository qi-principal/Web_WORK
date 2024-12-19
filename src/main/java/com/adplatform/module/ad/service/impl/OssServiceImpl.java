package com.adplatform.module.ad.service.impl;

import com.adplatform.module.ad.config.OssConfig;
import com.adplatform.module.ad.service.OssService;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
        // 生成文件名
        String fileName = generateFileName(file.getOriginalFilename());
        
        // 生成文件路径
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
     * 生成文件名
     * 格式：uuid.扩展名
     */
    private String generateFileName(String originalFilename) {
        String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        return UUID.randomUUID().toString().replaceAll("-", "") + ext;
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