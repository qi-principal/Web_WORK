package com.adplatform.module.ad.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

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

    /**
     * 上传字符串内容
     *
     * @param content 内容字符串
     * @param fileName 文件名
     * @param dir 目录
     * @param contentType 内容类型
     * @return 文件访问URL
     */
    String uploadContent(String content, String fileName, String dir, String contentType);

    /**
     * 上传字节数组
     *
     * @param contentBytes 内容字节数组
     * @param fileName 文件名
     * @param dir 目录
     * @param contentType 内容类型
     * @return 文件访问URL
     */
    String uploadBytes(byte[] contentBytes, String fileName, String dir, String contentType);

    /**
     * 上传输入流
     *
     * @param inputStream 输入流
     * @param fileName 文件名
     * @param dir 目录
     * @param contentType 内容类型
     * @param contentLength 内容长度
     * @return 文件访问URL
     */
    String uploadStream(InputStream inputStream, String fileName, String dir, String contentType, long contentLength);
} 