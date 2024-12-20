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
        // 获取原始文件名并提取文件扩展名
        String originalFileName = file.getOriginalFilename();
         String extension = originalFileName != null && originalFileName.lastIndexOf('.') > 0
                        ? originalFileName.substring(originalFileName.lastIndexOf('.'))  // 获取扩展名
                        : ".jpg";  // 默认使用 .jpg 扩展名

        // 使用UUID生成文件名，并附加原文件扩展名
        String fileName = UUID.randomUUID().toString() + extension;
        System.out.println("生成的文件名: " + fileName);  // 打印生成的文件名

        System.out.println("// 创建OSSClient实例");
        System.out.println("fileName:"+fileName);
        
        // 生成文件路径
        String filePath = generateFilePath(dir, fileName);

        System.out.println("文件路径:"+filePath);

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