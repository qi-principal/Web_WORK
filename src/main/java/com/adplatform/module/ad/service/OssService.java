package com.adplatform.module.ad.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * OSS服务接口
 *
 * @author andrew
 * @date 2023-12-19
 */
public interface OssService {

    /**
     * 上传文件
     *
     * @param file 文件
     * @param dir 目录
     * @return 文件访问URL
     */
    String upload(MultipartFile file, String dir) throws IOException;

    /**
     * 删除文件
     *
     * @param url 文件URL
     */
    void delete(String url);
} 