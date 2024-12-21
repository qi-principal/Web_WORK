package com.adplatform.module.delivery.service.impl;

import com.adplatform.module.ad.config.OssConfig;
import com.adplatform.module.ad.service.OssService;
import com.adplatform.module.delivery.service.CdnService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 阿里云OSS CDN服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AliyunCdnService implements CdnService {

    private final OssService ossService;
    private final OssConfig ossConfig;
    private static final String CDN_DIR = "ad/delivery";

    @Override
    public String uploadFile(String content, String fileName) {
        try {
            // 使用OssService上传内容
            String url = ossService.uploadContent(content, fileName, CDN_DIR, "text/html");
            log.info("文件上传到CDN成功：{}", fileName);
            return url;
        } catch (Exception e) {
            log.error("上传文件到CDN失败：" + fileName, e);
            throw new RuntimeException("上传文件到CDN失败：" + e.getMessage());
        }
    }

    @Override
    public void deleteFile(String fileName) {
        try {
            String url = generateUrl(fileName);
            ossService.delete(url);
            log.info("从CDN删除文件成功：{}", fileName);
        } catch (Exception e) {
            log.error("从CDN删除文件失败：" + fileName, e);
            throw new RuntimeException("从CDN删除文件失败：" + e.getMessage());
        }
    }

    @Override
    public String generateUrl(String fileName) {
        return ossConfig.getDomain() + "/" + CDN_DIR + "/" + fileName;
    }
} 